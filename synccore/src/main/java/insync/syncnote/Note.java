package insync.syncnote;

public class Note {

    private String id;
    private String text;

    public Note(String id, String text) {
        if (id == null || id.equals("")) {
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
