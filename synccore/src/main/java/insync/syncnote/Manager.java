package insync.syncnote;

import android.support.annotation.Nullable;

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

    public boolean hasUpdates(long since) {
        return lastModifiedTime > since;
    }

    public List<Note> getAllNotes() {
        return new ArrayList<Note>(notes.values());
    }

    @Nullable
    public Note addNote(Note note) {
        if (note == null) return null;
        if (notes.containsKey(note.getId())) {
            return null; // should not overwrite notes?
        }
        lastModifiedTime = System.currentTimeMillis();
        return notes.put(note.getId(), note);
    }

    @Nullable
    public Note get(String id) {
        return notes.get(id);
    }
}
