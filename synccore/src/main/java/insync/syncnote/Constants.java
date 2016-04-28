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
