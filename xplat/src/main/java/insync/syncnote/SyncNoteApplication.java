package insync.syncnote;

import com.google.gson.Gson;

import java.awt.EventQueue;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class SyncNoteApplication {

    private final Object lock = new Object();
    public void notifyLock() {
        synchronized (lock) {
            // this notifies the wait() below, in case the caller of this method
            // was the last window to close (which will get checked anyway)
            lock.notify();
        }
    }

    public static void main(String[] args) {
        // create main instance of our application
        SyncNoteApplication main = new SyncNoteApplication();
        // load config from a settings file, or create it if it doesn't exist
        main.loadConfig("settings.json");

        // create the default note window. this will always show up, even if you have no notes
        NoteWindow defaultWindow = new NoteWindow(main);
        EventQueue.invokeLater(() -> defaultWindow.setVisible(true));
        main.activeWindows.add(defaultWindow);

        CoreConfig config = SyncNoteCore.getInst().getConfig();
        // if the user hasn't specifically chosen to remain offline, and isn't logged in,
        // show the login dialog for them
        if (!config.isOffline() && config.getAuthToken().isEmpty()) {
            // first time login
            LoginDialog login = new LoginDialog(main);
            EventQueue.invokeLater(() -> login.setVisible(true));
        }

        // create a lock & thread to wait indefinitely until all windows (notes & settings) close
        // only once the user closes all windows can the application exit
        Thread t = new Thread(() -> {
            //System.out.println("Running thread");
            synchronized (main.lock) {
                while (!main.activeWindows.isEmpty() || (main.settingsWindow != null)) {
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

    final List<NoteWindow> activeWindows = new ArrayList<>();
    SettingsWindow settingsWindow = null;

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
            } else {
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
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}