#!/usr/bin/env python3
"""Refuse runtime dependency drift before the protected key is prepared."""
import argparse
import hashlib
import json
from pathlib import Path

def verify(inventory, reviewed):
    actual=[]
    for artifact in json.loads(Path(inventory).read_text()):
        path=Path(artifact['file'])
        actual.append((artifact['coordinate'],path.name,hashlib.sha256(path.read_bytes()).hexdigest()))
    expected=[(x['coordinate'],x['filename'],x['sha256']) for x in json.loads(Path(reviewed).read_text())['artifacts']]
    if sorted(actual) != sorted(expected):
        raise ValueError('Resolved Foundation runtime differs from reviewed SBOM; regenerate and review notices before signing')
    return len(actual)

if __name__=='__main__':
    parser=argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--inventory',required=True)
    parser.add_argument('--reviewed',required=True)
    args=parser.parse_args()
    print('Reviewed runtime artifact hashes matched:',verify(args.inventory,args.reviewed))
