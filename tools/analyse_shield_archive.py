#!/usr/bin/env python3
"""Aggregate a private diagnostic ZIP without exporting paths, IDs, payloads or raw traces."""
import argparse
import collections
import hashlib
import json
from pathlib import Path
import re
import zipfile


def label(value, default="unrecognized"):
    value=str(value)
    return value if re.fullmatch(r"[A-Za-z0-9_.$-]{1,80}", value) else default


def analyse(path):
    events={};decoded=malformed=duplicates=conflicts=0
    with zipfile.ZipFile(path) as archive:
        streams=[n for n in archive.infolist() if re.search(r"\.jsonl(?:\.[0-9]+)?$", n.filename)]
        if sum(n.file_size for n in streams)>128*1024*1024:
            raise ValueError("Diagnostic streams exceed analysis bound")
        for entry in streams:
            for line in archive.read(entry).decode("utf-8",errors="replace").splitlines():
                try:
                    event=json.loads(line)
                    if not isinstance(event,dict) or not isinstance(event.get("process"),str) or not isinstance(event.get("sequence"),int):
                        raise ValueError("Missing event identity")
                except (ValueError,TypeError):
                    malformed+=1
                    continue
                decoded+=1
                key=(event["process"],event["sequence"])
                if key in events:
                    duplicates+=1
                    conflicts+=events[key]!=event
                else:
                    events[key]=event
    values=sorted(events.values(),key=lambda e:(e.get("utc_ms",0),e["sequence"]))
    versions={e["process"]:label(e.get("version"),"unretained") for e in values if e.get("event")=="startup"}
    by_type=collections.defaultdict(list)
    last={}
    for e in values:
        by_type[label(e.get("event"))].append(e)
        last[e["process"]]=max(last.get(e["process"],0),e.get("utc_ms",0))
    count=lambda rows,key:dict(sorted(collections.Counter(label(e.get(key)) for e in rows).items()))
    warnings=by_type["PREVIOUS_SESSION_UNCLEAN_EXIT"]
    focus=by_type["focus_anomaly_suspected"]
    attempts=by_type["playback_attempt"]
    classes=[label(e.get("trace","").splitlines()[0] if e.get("trace") else "unretained") for e in by_type["uncaught_exception"]]
    important=["focus_anomaly_suspected","scan_failed","scan_batch_failed","metadata_network_failed","metadata_package_failed","metadata_semantic_failed","player_error","uncaught_exception"]
    return {
        "archive_sha256":hashlib.sha256(Path(path).read_bytes()).hexdigest(),
        "scope":"Retained events only; partial coverage; warning/event counts are not unique bugs or crash counts",
        "decoded_lines":decoded,"malformed_lines":malformed,"duplicate_copies":duplicates,"conflicting_identities":conflicts,"distinct_events":len(events),
        "events":{k:len(v) for k,v in sorted(by_type.items())},
        "startup_versions":dict(sorted(collections.Counter(versions.values()).items())),
        "event_versions":{kind:dict(sorted(collections.Counter(versions.get(e["process"],"unretained") for e in by_type[kind]).items())) for kind in important},
        "focus_kinds":count(focus,"kind"),
        "overlap_target_classes":dict(sorted(collections.Counter(label(e.get("target","").split(":",1)[0]) for e in focus if e.get("kind")=="navigation_overlap").items())),
        "restoration_outcomes":dict(sorted(collections.Counter(str(e.get("success")) for e in focus if e.get("kind")=="restoration_fallback").items())),
        "unclean_warnings_naming_process_with_later_events":sum(last.get(e.get("previous_process"),0)>e.get("utc_ms",0)+1000 for e in warnings),
        "network_exception_types":count(by_type["metadata_network_failed"],"type"),
        "package_failure_categories":count(by_type["metadata_package_failed"],"failure_category"),
        "os_exit_reasons":count(by_type["android_process_exit"],"reason"),
        "uncaught_exception_classes":dict(sorted(collections.Counter(classes).items())),
        "distinct_playback_attempt_ids":len(set(e["attempt_id"] for e in attempts if isinstance(e.get("attempt_id"),str))),
        "playback_operation_records":count(attempts,"operation"),
        "raw_event_payloads_exported":False,
    }


if __name__=="__main__":
    parser=argparse.ArgumentParser(description=__doc__);parser.add_argument("archive",type=Path);parser.add_argument("--output",type=Path);args=parser.parse_args()
    result=json.dumps(analyse(args.archive),indent=2)+"\n"
    if args.output:args.output.write_text(result)
    else:print(result,end="")
