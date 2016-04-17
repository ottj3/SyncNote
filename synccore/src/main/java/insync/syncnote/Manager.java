package insync.syncnote;

import android.support.annotation.Nullable;

import java.util.HashMap;
import java.util.Map;

public class Manager {

    private Map<String, Note>  notes;

    public Manager() {
        this.notes = new HashMap<>();
    }

    @Nullable
    public Note addNote(Note note) {
        // TODO better error returns, make exceptions?
        if (note == null) return null;
        if (notes.containsKey(note.getId())) {
            return null; // should not overwrite notes?
        }
        return notes.put(note.getId(), note);
    }

    @Nullable
    public Note get(String id) {
        return notes.get(id);
    }
}
