package com.archos.mediacenter.video.leanback;

import android.database.Cursor;
import android.database.CursorWrapper;
import com.archos.mediacenter.video.diagnostics.Diagnostics;

/** Records schema coordinates, never values, for failed library reads. */
final class PreviewMappingCursor extends CursorWrapper {
    private int column = -1, type = -1;
    PreviewMappingCursor(Cursor cursor) { super(cursor); }
    private void reading(int index) {
        column = index;
        type = -1;
        if (index < 0 || index >= getColumnCount())
            throw new IllegalArgumentException("Library projection is missing a required column");
        type = super.getType(index);
    }
    @Override public int getInt(int index) { reading(index); numeric(); return super.getInt(index); }
    @Override public long getLong(int index) { reading(index); numeric(); return super.getLong(index); }
    @Override public float getFloat(int index) { reading(index); numeric(); return super.getFloat(index); }
    @Override public String getString(int index) { reading(index); return super.getString(index); }
    private void numeric() { if(type == Cursor.FIELD_TYPE_BLOB) throw new IllegalArgumentException("Binary value in numeric library field"); }
    void report(String stage, RuntimeException failure) {
        // No filename/path, raw value, SQL, or exception message is recorded.
        long id = -1;
        try { int i = super.getColumnIndex("_id"); if (i >= 0) id = super.getLong(i); }
        catch (RuntimeException unavailable) { /* The window itself may be invalid. */ }
        int windowStart=-1,windowRows=-1;
        try {
            Cursor raw=getWrappedCursor();while(raw instanceof CursorWrapper)raw=((CursorWrapper)raw).getWrappedCursor();
            if(raw instanceof android.database.AbstractWindowedCursor){
                android.database.CursorWindow window=((android.database.AbstractWindowedCursor)raw).getWindow();
                if(window!=null){windowStart=window.getStartPosition();windowRows=window.getNumRows();}
            }
        } catch(RuntimeException unavailable) { /* Window coordinates may themselves be unavailable. */ }
        Diagnostics.event("indexed_library_record_failed", "stage", stage,
                "window_start",windowStart,"window_rows",windowRows,
                "row", getPosition(), "record_id", id, "column_index", column,
                "column", column >= 0 && column < getColumnCount() ? getColumnName(column) : "missing",
                "storage_type", type, "exception", failure.getClass().getSimpleName());
        Diagnostics.error("indexed_library_record_trace", failure);
    }
}
