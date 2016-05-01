package insync.syncnote;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores settings for SyncNote. Everything pretty self-explanatory here.
 */
public class CoreConfig {

    private String authToken = "";
    private boolean offline;
    private List<String> openNotes;


    public boolean isOffline() {
        return offline;
    }

    public void setOffline(boolean offline) {
        this.offline = offline;
    }

    public List<String> getOpenNotes() {
        if (openNotes == null) openNotes = new ArrayList<>();
        return openNotes;
    }

    public void setOpenNotes(List<String> openNotes) {
        this.openNotes = openNotes;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken.trim();
    }
}
