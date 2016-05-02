/*
 * SyncNote 2016
 * CSC470 Final Project
 * Jan-Lucas Ott, Connor Davis, Nate Harris, Randell Carrido
 */

package insync.syncnote;

public class SyncNoteCore {

    private static final SyncNoteCore INST = new SyncNoteCore();
    private Manager mgr;
    private CoreConfig config;
    private NoteParser parser;

    private SyncNoteCore() {
        mgr = new Manager();
        config = new CoreConfig();
        parser = new NoteParser();
    }

    public static SyncNoteCore getInst() {
        return INST;
    }

    public Manager getManager() {
        return mgr;
    }

    public CoreConfig getConfig() {
        return config;
    }

    public void setConfig(CoreConfig config) {
        this.config = config;
    }

    public NoteParser getParser() {
        return parser;
    }

}