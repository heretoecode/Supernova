package com.archos.mediacenter.video.streaming.putio;

import java.util.*;

/** Pure reconciliation policy: never mutates a library row or treats a failed listing as deletion. */
public final class PutioReconciliation {
    public enum Failure { NONE, CANCELLED, UNAUTHORISED, RATE_LIMITED, OFFLINE, INVALID_PAGE }
    public enum Decision { LINK_EXISTING, ALREADY_LINKED, NEW_FILE, NEEDS_REVIEW }

    public static final class File {
        public final long id, parentId, size;
        public final String relativePath;
        public File(long id,long parentId,String relativePath,long size) {
            if(id<=0||parentId<0||size<0)throw new IllegalArgumentException("Invalid provider file");
            this.id=id;this.parentId=parentId;this.relativePath=path(relativePath);this.size=size;
        }
    }
    /** Caller supplies only records belonging to the explicitly selected existing source. */
    public static final class Existing {
        public final long mediaId,size,providerFileId;
        public final String relativePath;
        public Existing(long mediaId,String relativePath,long size,long providerFileId) {
            if(mediaId<=0||size<0||providerFileId<0)throw new IllegalArgumentException("Invalid library record");
            this.mediaId=mediaId;this.relativePath=path(relativePath);this.size=size;this.providerFileId=providerFileId;
        }
    }
    public static final class Change {
        public final Decision decision;
        public final File file;
        public final long mediaId;
        public final List<Long> candidates;
        private Change(Decision decision,File file,long mediaId,List<Long> candidates) {
            this.decision=decision;this.file=file;this.mediaId=mediaId;
            this.candidates=Collections.unmodifiableList(new ArrayList<>(candidates));
        }
    }
    /** A single selected-folder traversal, including every descendant page. */
    public static final class Snapshot {
        public final String scope;
        private final Map<Long,File> files=new LinkedHashMap<>();
        private final Set<String> pageKeys=new HashSet<>(),pending=new HashSet<>();
        private Failure failure=Failure.NONE;
        private boolean finished;
        public Snapshot(String scope,String firstPage) {
            if(scope==null||scope.isEmpty()||firstPage==null||firstPage.isEmpty())throw new IllegalArgumentException("Missing scope");
            this.scope=scope;pending.add(firstPage);
        }
        /** Opaque page keys must include folder identity; continuation and child pages are explicit. */
        public synchronized void page(String pageKey,Collection<File> values,Collection<String> morePages) {
            if(failure!=Failure.NONE||finished)return;
            if(!pending.remove(pageKey)||!pageKeys.add(pageKey)||values==null||morePages==null){fail(Failure.INVALID_PAGE);return;}
            for(File file:values) {
                if(file==null||files.containsKey(file.id)){fail(Failure.INVALID_PAGE);return;}
                files.put(file.id,file);
            }
            for(String key:morePages) {
                if(key==null||key.isEmpty()||pageKeys.contains(key)||!pending.add(key)){fail(Failure.INVALID_PAGE);return;}
            }
        }
        public synchronized void fail(Failure reason) {if(reason==null||reason==Failure.NONE)throw new IllegalArgumentException("Missing failure");failure=reason;finished=false;}
        public synchronized boolean finish() {finished=failure==Failure.NONE&&pending.isEmpty();return finished;}
        public synchronized boolean complete() {return finished&&failure==Failure.NONE&&pending.isEmpty();}
        public synchronized Failure failure() {return failure;}
        public synchronized List<File> files() {return new ArrayList<>(files.values());}
    }
    public static final class Plan {
        public final boolean complete;
        public final List<Change> changes;
        /** Missing stable IDs are review candidates, never an instruction to erase history. */
        public final List<Long> missingMediaIds;
        private Plan(boolean complete,List<Change> changes,List<Long> missing) {
            this.complete=complete;this.changes=Collections.unmodifiableList(changes);
            this.missingMediaIds=Collections.unmodifiableList(missing);
        }
    }
    public static Plan plan(Snapshot snapshot,Collection<Existing> existing) {
        synchronized(snapshot) {
            // Atomic all-or-nothing initial association. Partial pages cannot change discovery ownership.
            if(!snapshot.complete())return new Plan(false,new ArrayList<>(),new ArrayList<>());
            Map<Long,List<Existing>> byId=new HashMap<>();
            Map<String,List<Existing>> byPath=new HashMap<>();
            List<Existing> records=new ArrayList<>(existing);
            Set<Long> mediaIds=new HashSet<>();
            for(Existing row:records) {
                if(!mediaIds.add(row.mediaId))throw new IllegalArgumentException("Duplicate library record");
                if(row.providerFileId>0)byId.computeIfAbsent(row.providerFileId,k->new ArrayList<>()).add(row);
                byPath.computeIfAbsent(row.relativePath,k->new ArrayList<>()).add(row);
            }
            Map<String,Integer> incomingPaths=new HashMap<>();
            for(File file:snapshot.files())incomingPaths.put(file.relativePath,incomingPaths.getOrDefault(file.relativePath,0)+1);
            List<Change> result=new ArrayList<>();Set<Long> present=new HashSet<>(),claimed=new HashSet<>();
            for(File file:snapshot.files()) {
                present.add(file.id);
                List<Existing> stable=byId.getOrDefault(file.id,Collections.emptyList());
                if(stable.size()==1) {
                    Existing row=stable.get(0);claimed.add(row.mediaId);
                    result.add(change(Decision.ALREADY_LINKED,file,row.mediaId,stable));continue;
                }
                if(stable.size()>1){result.add(change(Decision.NEEDS_REVIEW,file,0,stable));continue;}
                List<Existing> samePath=byPath.getOrDefault(file.relativePath,Collections.emptyList());
                if(samePath.size()==1) {
                    Existing row=samePath.get(0);
                    // Size 0/unknown, already-owned records, and multiple claims are never auto-linked.
                    if(incomingPaths.get(file.relativePath)==1&&file.size>0&&row.size==file.size&&row.providerFileId==0&&claimed.add(row.mediaId)) {
                        result.add(change(Decision.LINK_EXISTING,file,row.mediaId,samePath));continue;
                    }
                }
                if(!samePath.isEmpty()){result.add(change(Decision.NEEDS_REVIEW,file,0,samePath));continue;}
                List<Existing> plausible=new ArrayList<>();
                for(Existing row:records)if(name(row.relativePath).equals(name(file.relativePath))&&row.size==file.size)plausible.add(row);
                result.add(change(plausible.isEmpty()?Decision.NEW_FILE:Decision.NEEDS_REVIEW,file,0,plausible));
            }
            List<Long> missing=new ArrayList<>();
            for(Existing row:records)if(row.providerFileId>0&&!present.contains(row.providerFileId))missing.add(row.mediaId);
            return new Plan(true,result,missing);
        }
    }
    private static Change change(Decision decision,File file,long id,List<Existing> rows) {
        List<Long> candidates=new ArrayList<>();for(Existing row:rows)candidates.add(row.mediaId);
        return new Change(decision,file,id,candidates);
    }
    private static String name(String path){return path.substring(path.lastIndexOf('/')+1);}
    private static String path(String value) {
        if(value==null||value.isEmpty()||value.startsWith("/")||value.indexOf('\0')>=0||value.indexOf('\\')>=0)
            throw new IllegalArgumentException("Expected source-relative path");
        for(String part:value.split("/",-1))if(part.isEmpty()||part.equals(".")||part.equals(".."))throw new IllegalArgumentException("Ambiguous source-relative path");
        // Do not case-fold or decode percent escapes: both can conflate distinct provider files.
        return value;
    }
    private PutioReconciliation(){}
}
