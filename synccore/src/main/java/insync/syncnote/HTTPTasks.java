/*
 * SyncNote 2016
 * CSC470 Final Project
 * Jan-Lucas Ott, Connor Davis, Nate Harris, Randell Carrido
 */

package insync.syncnote;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import insync.syncnote.exceptions.RequestForbiddenException;
import insync.syncnote.exceptions.RequestInvalidException;

public final class HTTPTasks {

    /**
     * Uploads a JSON representation of a notes file to the user's server note file.
     *
     * @param key the session key of the user
     * @param text the JSON-encoded text
     * @throws RequestForbiddenException if the given key does not correspond to an active session
     * @throws RequestInvalidException if the key is null
     */
    public static void uploadText(String key, String text)
            throws RequestForbiddenException, RequestInvalidException {
        if (key == null) throw new RequestInvalidException();
        try {
            request("note.php", "?key=", URLEncoder.encode(key, "UTF-8"),
                    "&message=", URLEncoder.encode(text == null ? "{}" : text, "UTF-8"));
        } catch (UnsupportedEncodingException ignored) {
        }

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

    }

    /**
     * Downloads a JSON representation of a notes file from the user's server note file.
     *
     * @param key the session key of the user
     * @return the JSON-formatted notes file
     * @throws RequestForbiddenException if the given key does not correspond to an active session
     * @throws RequestInvalidException if the key is null
     */
    public static String downloadText(String key)
            throws RequestForbiddenException, RequestInvalidException {
        if (key == null) throw new RequestInvalidException();
        return request("note.php", "?key=", key);
    }

    /**
     * Logs a user in, giving them a unique session token to be used until they logout.
     *
     * @param username the user's registered name
     * @param password the user's password
     * @return a session token
     * @throws RequestForbiddenException if the user does not exist, or the password does not match
     * @throws RequestInvalidException if username or password are null
     */
    public static String login(String username, String password)
            throws RequestForbiddenException, RequestInvalidException {
        if (username == null || password == null) throw new RequestInvalidException();
        return request("login.php", "?username=", username, "&password=", password);
    }

    /**
     * Logs a given session token out, invalidating that token.
     * <p>Note that this fails silently if the given token does not correspond to an active session.
     * <p>Note that this only logs out a given token, not all session that user may have.
     *
     * @param session the token to log out
     * @throws RequestForbiddenException never
     * @throws RequestInvalidException if the session token is  null
     */
    public static void logout(String session)
            throws RequestForbiddenException, RequestInvalidException {
        if (session == null) throw new RequestInvalidException();
        request("logout.php", "?key=", session);
    }

    /**
     * Register a new account using the given username and password.
     *
     * @param username the user's desired name
     * @param password the user's desired password
     * @throws RequestForbiddenException if the desired username has already been registered
     * @throws RequestInvalidException if username or password are null
     */
    public static void register(String username, String password)
        throws RequestForbiddenException, RequestInvalidException {
        if (username == null || password == null) throw new RequestInvalidException();
        request("register.php", "?username=", username, "&password=", password);
    }

    private static String request(String loc, String... args)
            throws RequestForbiddenException, RequestInvalidException {
        String req = Constants.SERVER.concat(loc);
        for (String arg : args) {
            req += arg;
        }
        try {
            URL url = new URL(req);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            switch (conn.getResponseCode()) {
                case HttpURLConnection.HTTP_FORBIDDEN:
                    throw new RequestForbiddenException();
                case HttpURLConnection.HTTP_BAD_REQUEST:
                    throw new RequestInvalidException();
                default:
                    // NO-OP
            }
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));

            StringBuilder ret = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                ret.append(line).append("\n");
            }
            return ret.toString();
        } catch (IOException e) {
            e.printStackTrace(); // TODO if you are reading this, it's probably because the server
                                 // is down or missing or moved or something. or your internet is.
        }
        return null;
    }
}
