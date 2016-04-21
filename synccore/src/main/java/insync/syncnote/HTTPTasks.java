package insync.syncnote;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public final class HTTPTasks {

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

    public static String login(String username, String password) {
        return ""; // TODO
    }

    private static String request(String loc, String... args) {
        String req = Constants.SERVER.concat(loc);
        for (String arg : args) {
            req += arg;
        }
        try {
            URL url = new URL(req);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            switch (conn.getResponseCode()) {
                // TODO impl classes
                case HttpURLConnection.HTTP_FORBIDDEN:
                    //throw new RequestForbiddenException();
                case HttpURLConnection.HTTP_BAD_REQUEST:
                    //throw new RequestInvalidException();
                default:
                    // NO-OP
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder ret = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                ret.append(line).append("\n");
            }
            return ret.toString();
        } catch (IOException e) {
            return null;
        }
    }
}
