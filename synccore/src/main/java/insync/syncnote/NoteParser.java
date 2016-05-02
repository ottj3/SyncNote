/*
 * SyncNote 2016
 * CSC470 Final Project
 * Jan-Lucas Ott, Connor Davis, Nate Harris, Randell Carrido
 */

package insync.syncnote;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.List;

import insync.syncnote.exceptions.InvalidNotesFileException;

public class NoteParser {

    private Gson gson;

    public NoteParser() {
        gson = new Gson();
    }

    public void decode(String json) throws InvalidNotesFileException {
        decode(json, SyncNoteCore.getInst().getManager());
    }

    public void decode(String json, Manager target) throws InvalidNotesFileException {
        Note[] notes;
        try {
            notes = gson.fromJson(json, Note[].class);
            if (notes == null) {
                return;
            }
            for (Note n : notes) {
                target.addNote(n);
            }
        } catch (JsonSyntaxException e) {
            throw new InvalidNotesFileException();
        }
    }

    public String encode() {
        return encode(SyncNoteCore.getInst().getManager());
    }

    public String encode(Manager from) {
        List<Note> notes = from.getAllNotes();
        Note[] data = new Note[notes.size()];
        notes.toArray(data);
        return gson.toJson(data);
    }
}
