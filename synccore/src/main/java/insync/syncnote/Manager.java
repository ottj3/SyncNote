package insync.syncnote;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

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
        return new ArrayList<Note>(notes.values());
    }

    public Note addNote(Note note) {
        if (note == null) return null;
        markDirty();
        return notes.put(note.getId(), note);
    }

    public boolean remove(String id) {
        markDirty();
        return notes.remove(id) != null;
    }

    public Note get(String id) {
        return notes.get(id);
    }
}
