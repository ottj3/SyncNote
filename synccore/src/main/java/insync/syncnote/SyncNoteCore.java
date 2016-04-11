package insync.syncnote;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class SyncNoteCore {

    //private static final String server = "http://www.tcnj.edu/~davisc27/Test2.php";
    private static final String server = "http://www.tcnj.edu/~ottj3/demo.php";

    public static void uploadText(String key, String text) {
        try {
            String req = server;

            // for uploading via GET
//            req = req.concat("?key=" + URLEncoder.encode(key, "UTF-8")
//                    + "&message=" + URLEncoder.encode(text, "UTF-8"));
//            URL url = new URL(req);
//            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // for uploading via POST
            URL url = new URL(req);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write("key=" + URLEncoder.encode(key, "UTF-8") +
                    "&message=" + URLEncoder.encode(text, "UTF-8"));
            writer.flush();
            writer.close();


            String line;
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String downloadText(String key) {
        String req = server.concat("?key=").concat(key);
        StringBuilder res = new StringBuilder();
        try {
            URL url = new URL(req);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                res.append(line + "\n");
                System.out.println("read: " + line);
            }
            reader.close();
            System.out.println(res.toString());
            return res.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}