package insync.syncnote;

import com.google.gson.Gson;
import java.util.List;

public class NoteParser {

    private Gson gson;

    public NoteParser() {
        gson = new Gson();
    }

    public void decode(String json, Manager target) {
        Note[] notes = gson.fromJson(json, Note[].class);

    }

    public void encode(Manager from) {
        List<Note> notes = from.getAllNotes();
        Note[] data = new Note[notes.size()];
        notes.toArray();
    }
}
