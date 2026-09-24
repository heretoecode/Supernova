package com.archos.mediacenter.video.diagnostics;

import java.nio.charset.StandardCharsets;
import java.util.*;

/** Bounded in-memory flight data; elapsed time avoids wall-clock adjustments. */
public final class DiagnosticFlightRecorder {
    private static final class Record {final long time;final String line;final int size;Record(long time,String line){this.time=time;this.line=line;size=line.getBytes(StandardCharsets.UTF_8).length;}}
    private final Deque<Record> rows=new ArrayDeque<>();private final int budget;private final long window;private int bytes;private long evicted;
    public DiagnosticFlightRecorder(int budget,long window){if(budget<1||window<1)throw new IllegalArgumentException();this.budget=budget;this.window=window;}
    public synchronized void add(long elapsed,String line){Record record=new Record(elapsed,line);if(record.size>budget){evicted++;return;}while(!rows.isEmpty()&&(elapsed-rows.peekFirst().time>window||bytes+record.size>budget)){bytes-=rows.removeFirst().size;evicted++;}rows.addLast(record);bytes+=record.size;}
    public synchronized String snapshot(long elapsed){while(!rows.isEmpty()&&elapsed-rows.peekFirst().time>window){bytes-=rows.removeFirst().size;evicted++;}StringBuilder result=new StringBuilder();for(Record row:rows)result.append(row.line);return result.toString();}
    public synchronized int bytes(){return bytes;}public synchronized long evicted(){return evicted;}
    public synchronized void clear(){rows.clear();bytes=0;evicted=0;}
}
