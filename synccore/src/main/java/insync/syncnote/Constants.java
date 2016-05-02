/*
 * SyncNote 2016
 * CSC470 Final Project
 * Jan-Lucas Ott, Connor Davis, Nate Harris, Randell Carrido
 */

package insync.syncnote;

public final class Constants {

    static {
        String serverName = System.getProperty("syncnote.server");
        if (serverName != null) {
            SERVER = serverName;
        } else {
            SERVER = "http://tcnj.edu/~ottj3/";
        }
    }

    public static final String SERVER;
}
