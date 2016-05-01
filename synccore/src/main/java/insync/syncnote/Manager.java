package insync.syncnote;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * Simple Map wrapper for Note management.
 * Everything should be self-explanatory, since it just wraps a Map.
 * Except lastModifiedTime, which isn't used yet. TODO features
 */
public class Manager {

    private Map<String, Note>  notes;
    private long lastModifiedTime;

    public Manager() {
        this.notes = new HashMap<>();
    }

    public long getLastModifiedTime() {
        return lastModifiedTime;
    }

    private void markDirty() {
        lastModifiedTime = System.currentTimeMillis();
    }

    public List<Note> getAllNotes() {
        return new ArrayList<>(notes.values());
    }

    public Note addNote(Note note) {
        if (note == null || note.getId().isEmpty()) return null;
        markDirty();
        return notes.put(note.getId(), note);
    }

    public boolean remove(String id) {
        markDirty();
        return notes.remove(id) != null;
    }

    public void removeAll() {
        notes = new HashMap<>();
    }

    public Note get(String id) {
        return notes.get(id);
    }
}
