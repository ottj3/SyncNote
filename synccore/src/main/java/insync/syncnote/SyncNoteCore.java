package insync.syncnote;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

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

    public NoteParser getParser() {
        return parser;
    }

    public static void uploadText(String key, String text) {
        try {
            String req = Constants.SERVER + "note.php";

            // for uploading via GET
            req = req.concat("?key=" + URLEncoder.encode(key, "UTF-8")
                    + "&message=" + URLEncoder.encode(text, "UTF-8"));
            URL url = new URL(req);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // for uploading via POST
//            URL url = new URL(req);
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//            conn.setDoOutput(true);
//            conn.setRequestMethod("POST");
//            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
//            writer.write("key=" + URLEncoder.encode(key, "UTF-8") +
//                    "&message=" + URLEncoder.encode(text, "UTF-8"));
//            writer.flush();
//            writer.close();


//            String line;
//            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//            while ((line = reader.readLine()) != null) {
//                System.out.println(line);
//            }
//            reader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String downloadText(String key) {
        String req = Constants.SERVER.concat("note.php").concat("?key=").concat(key);
        StringBuilder res = new StringBuilder();
        try {
            URL url = new URL(req);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                res.append(line).append("\n");
            }
            reader.close();
            //System.out.println(res.toString());
            return res.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}