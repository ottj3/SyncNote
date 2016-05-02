package insync.syncnote;

import com.google.gson.Gson;

import java.awt.EventQueue;
import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import insync.syncnote.exceptions.InvalidNotesFileException;
import insync.syncnote.exceptions.RequestForbiddenException;
import insync.syncnote.exceptions.RequestInvalidException;

public class SyncNoteApplication {

    final List<NoteWindow> activeWindows = new ArrayList<>();
    private final Object lock = new Object();

    public static void main(String[] args) {
        // create main instance of our application
        SyncNoteApplication main = new SyncNoteApplication();
        // load config from a settings file, or create it if it doesn't exist
        main.loadConfig("settings.json");

        CoreConfig config = SyncNoteCore.getInst().getConfig();
        // if the user hasn't specifically chosen to remain offline, and isn't logged in,
        // show the login dialog for them
        if (!config.isOffline() && config.getAuthToken().isEmpty()) {
            // first time login
            LoginDialog login = new LoginDialog(main);
            login.setModal(true);
            login.setVisible(true);
        }
        if (!config.getAuthToken().isEmpty()) {
            // if they ARE logged in already, fetch their notes
            try {
                Thread dl = new Thread(() -> {
                    String key = config.getAuthToken();
                    try {
                        String json = HTTPTasks.downloadText(key);
                        SyncNoteCore.getInst().getParser().decode(json);
                    } catch (RequestForbiddenException e) {
                        // they got logged out, log back in
                        LoginDialog login = new LoginDialog(main);
                        EventQueue.invokeLater(() -> login.setVisible(true));
                    } catch (InvalidNotesFileException e2) {
                        JOptionPane.showMessageDialog(new JFrame(), "Tried to get your notes from "
                                        + "the server, but the file was corrupted.",
                                "Error Downloading", JOptionPane.ERROR_MESSAGE);
                    } catch (RequestInvalidException ignored) {
                    }
                });
                dl.start();
                dl.join();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // if the user had notes open last time they quit, re-open those all in windows
        if (!config.getOpenNotes().isEmpty()) {
            List<String> setLater = new ArrayList<>();
            int offset = 30;
            for (int i = 0; i < config.getOpenNotes().size(); i++) {
                String s = config.getOpenNotes().get(i);
                Note note = SyncNoteCore.getInst().getManager().get(s);
                if (note == null) {
                    // had a note open that's gone now, remove it
                    return;
                }
                setLater.add(s);
                NoteWindow window = new NoteWindow(main);
                window.showNote(note);
                Point start = window.getLocation();
                window.setLocation(start.x + i * offset, start.y + i * offset);
                EventQueue.invokeLater(() -> window.setVisible(true));
                main.activeWindows.add(window);
            }
            config.setOpenNotes(setLater);
        }
        // if we failed to create windows above (if we got logged out or notes are missing,
        // for example), then make the default window anyway
        if (main.activeWindows.isEmpty()) {
            // create the default note window. this will always show up, even if you have no notes,
            // or no open notes
            NoteWindow defaultWindow = new NoteWindow(main);
            EventQueue.invokeLater(() -> defaultWindow.setVisible(true));
            main.activeWindows.add(defaultWindow);
        }

        // create a lock & thread to wait indefinitely until all windows (notes & settings) close
        // only once the user closes all windows can the application exit
        Thread t = new Thread(() -> {
            //System.out.println("Running thread");
            synchronized (main.lock) {
                while (!main.activeWindows.isEmpty()) {
                    try {
                        // if there are still open windows, wait until we get a notify() from above
                        //System.out.println("Wait starting");
                        main.lock.wait();
                        //System.out.println("Wait finished");
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                //System.out.println("Ran thread");
            }
        });
        t.start();
        try {
            // yield to our waiting thread. this thread will merge with it and not run anything else
            // until that thread finishes, which only happens when all windows close
            //System.out.println("Joining thread.");
            t.join();
            //System.out.println("Joined thread finished.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // before exiting, save any changes to config
        // (notes should save when each individual window closes)
        main.saveConfig("settings.json");
        System.exit(0);
    }

    public void notifyLock() {
        synchronized (lock) {
            // this notifies the wait() below, in case the caller of this method
            // was the last window to close (which will get checked anyway)
            lock.notify();
        }
    }

    public void saveConfig(String fileName) {
        File configFile = new File(fileName);
        Gson gson = new Gson();
        // write the current configuration state out to the file
        try (FileWriter writer = new FileWriter(configFile)) {
            String out = gson.toJson(SyncNoteCore.getInst().getConfig());
            writer.write(out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadConfig(String fileName) {
        File configFile = new File(fileName);
        Gson gson = new Gson();
        try {
            if (configFile.exists()) {
                // if the file already exists, load settings from there
                FileInputStream fis = new FileInputStream(configFile);
                InputStreamReader reader = new InputStreamReader(fis);
                SyncNoteCore.getInst().setConfig(gson.fromJson(reader, CoreConfig.class));
                fis.close();
            }/* else {
                // NOTE this else block isn't actually necessary because the Gson serializer
                // will create a valid settings file out of our CoreConfig class already

                // otherwise, load it from the internal resource
                InputStream is = this.getClass().getClassLoader().getResourceAsStream(fileName);
                if (is == null) {
                    System.out.println("Default config not found in jar!");
                    return;
                }
                // and then write it out to the filesystem for future startups
                FileOutputStream fos = new FileOutputStream(configFile);
                byte[] buf = new byte[8192];
                int length;
                while ((length = is.read(buf)) > 0) {
                    fos.write(buf, 0, length);
                }
                fos.close();
                is.close();

            }*/
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}