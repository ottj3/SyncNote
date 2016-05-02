/*
 * SyncNote 2016
 * CSC470 Final Project
 * Jan-Lucas Ott, Connor Davis, Nate Harris, Randell Carrido
 */

package insync.syncnote;

public class Note {

    private String id;
    private String text;

    public Note(String id, String text) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("note cannot have empty name");
        }
        this.id = id;
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
