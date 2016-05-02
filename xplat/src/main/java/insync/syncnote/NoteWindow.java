package insync.syncnote;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import insync.syncnote.exceptions.InvalidNotesFileException;
import insync.syncnote.exceptions.RequestForbiddenException;
import insync.syncnote.exceptions.RequestInvalidException;

public class NoteWindow extends JFrame {

    private static final Color CANARY = new Color(252, 250, 173); // approximate color of post-it notes
    private SyncNoteApplication parentApp;
    private JEditorPane textEditorPane;
    private JTextPane noteIdPane;
    private boolean docChanged;
    private JButton prevNote;
    private JButton nextNote;

    public NoteWindow(SyncNoteApplication parent) {
        this.parentApp = parent;
        init();
    }

    public void close() {
        // make sure we always save before closing
        saveCurrentNote();

        // throw closing event, allowing Swing to do its thing
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    public void triggerDownload() {
        download();
    }

    // shortcut method to display a note object in this window, since we do this a lot
    public void showNote(Note note) {
        noteIdPane.setText(note.getId());
        textEditorPane.setText(note.getText());
    }

    private void init() {
        // create all the buttons
        JButton newButton = createButton("+", "Open a new Note."); // "new note"
        newButton.addActionListener(e -> EventQueue.invokeLater(() -> {
            NoteWindow newWindow = new NoteWindow(parentApp);
            int x = (int) NoteWindow.this.getSize().getWidth();
            int y = (int) NoteWindow.this.getSize().getHeight();
            // make the new window offset a bit so you can see the old window behind it
            newWindow.setLocation(this.getX() + x / 2, this.getY() + y / 2);
            newWindow.setVisible(true);
            parentApp.activeWindows.add(newWindow);
        }));

        JButton uploadButton = createButton("\u21e7", "Upload all your notes to the server."); // "upload"
        JButton downloadButton = createButton("\u21e9", "Download all your notes from the server."); // "download"
        uploadButton.addActionListener(e -> upload());
        downloadButton.addActionListener(e -> download());

        JButton settingsButton = createButton("S", "View Settings.");
        JButton deleteButton = createButton("X", "Delete this note (permanently)");
        settingsButton.addActionListener(e -> openSettings());
        deleteButton.addActionListener(e -> deleteNote());

        prevNote = createButton("\u21e6", "View the previous note.");
        nextNote = createButton("\u21e8", "View the next note.");
        prevNote.addActionListener(e -> prevNote());
        nextNote.addActionListener(e -> nextNote());

        JButton exitButton = createButton("X", "Close this window, without deleting the note permanently.");
        exitButton.addActionListener(e -> close());

        // other panels and components
        JPanel dragPanel = new DragPanel(this); // allows dragging (because we don't have a normal border)
        dragPanel.setToolTipText("Click and drag to move window.");
        dragPanel.setBackground(CANARY);

        textEditorPane = new JEditorPane();
        noteIdPane = new JTextPane();

        // center the note title...yes this is all necessary
        StyledDocument doc = noteIdPane.getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);

        noteIdPane.setBackground(CANARY);
        textEditorPane.setBackground(CANARY);


        // keep track of when the note text gets changed
        textEditorPane.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                docChanged = true;
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                docChanged = true;
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                docChanged = true;
            }
        });
        // and periodically save the note when it gets changed
        Timer updateTask = new Timer(100, e -> {
            // also make sure the user isn't just editting the id
            // since that will create a lot of notes
            if (docChanged && !noteIdPane.isFocusOwner()) {
                if (!noteIdPane.getText().isEmpty()) {
                    saveCurrentNote();
                }
            }
        });

        JComponent[] titlePane = new JComponent[]{ // top bar of buttons
                newButton,
                uploadButton, downloadButton,
                dragPanel,
                settingsButton,
                exitButton,
        };
        JComponent[] notePane = new JComponent[]{ // second bar of buttons
                prevNote,
                noteIdPane,
                deleteButton,
                nextNote,
        };


        // wrap text in scroll bars, if needed
        JScrollPane textPane = new JScrollPane(textEditorPane,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        textPane.setBackground(CANARY);
        textPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            public void configureScrollBarColors() {
                this.thumbColor = CANARY.darker();
                this.thumbDarkShadowColor = CANARY.darker().darker();
                this.trackColor = CANARY;
            }

            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createEmptyButton();
            }

            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createEmptyButton();
            }

            private JButton createEmptyButton() {
                JButton btn = new JButton();
                Dimension zero = new Dimension(0, 0);
                btn.setMinimumSize(zero);
                btn.setMaximumSize(zero);
                btn.setPreferredSize(zero);
                return btn;
            }

        });

        // remove default borders and buttons
        setUndecorated(true);
        // put all the buttons and stuff in place
        setupUI(titlePane, notePane, textPane);

        // set up the window itself
        setTitle("SyncNote");
        setSize(300, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        // allow the window to be resized, since we don't have native borders
        ComponentResizer cr = new ComponentResizer();
        cr.setMinimumSize(new Dimension(300, 200)); // buttons get squished if this gets smaller
        cr.setDragInsets(new Insets(5, 5, 8, 8));
        cr.registerComponent(this);

        // when the window gets closed, update the parent application for tracking purposes
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (parentApp.activeWindows.size() > 1) {
                    SyncNoteCore.getInst().getConfig().getOpenNotes().remove(noteIdPane.getText());
                }
                parentApp.activeWindows.remove(NoteWindow.this);
                parentApp.notifyLock();
            }
        });
        // start the task that saves notes automatically, now that UI is set up
        updateTask.start();
    }

    private JButton createButton(String name, String toolTip) {
        JButton btn = new JButton(name);
        btn.setToolTipText(toolTip);
        btn.setBackground(CANARY);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        return btn;
    }

    private void setupUI(JComponent[] titlePane, JComponent[] notePane, JComponent textBox) {
        JPanel main = new JPanel(new BorderLayout());
        main.setBorder(new EmptyBorder(5, 5, 5, 5));
        main.setBackground(Color.BLACK);

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH; // fill the entire row

        JPanel titleBar = new JPanel();
        titleBar.setLayout(new GridBagLayout());
        for (JComponent comp : titlePane) {
            if (comp instanceof JButton) {
                c.weightx = 0.0; // buttons stay small
            } else {
                c.weightx = 1.0; // extend other elements to fill space
            }
            titleBar.add(comp, c);
        }

        JPanel noteBar = new JPanel();
        noteBar.setLayout(new GridBagLayout());
        for (JComponent comp : notePane) {
            if (comp instanceof JButton) {
                c.weightx = 0.0;
            } else {
                c.weightx = 1.0;
            }
            noteBar.add(comp, c);
        }

        JPanel topBars = new JPanel();
        topBars.setLayout(new GridLayout(2, 1));
        topBars.add(titleBar);
        topBars.add(noteBar);


        main.add(topBars, BorderLayout.NORTH);
        // creates a separator between all the buttons and the note itself
        textBox.setBorder(BorderFactory.createMatteBorder(1, -1, -1, -1, CANARY.darker()));
        main.add(textBox, BorderLayout.CENTER);
        main.setBackground(CANARY);

        getContentPane().add(main);

        this.pack();
    }

    private void saveCurrentNote() {
        if (noteIdPane.getText().isEmpty()) return; // don't save notes without a title
        // access the not object from Manager, and either update if it exists or
        // add it if it does not
        Note current = SyncNoteCore.getInst().getManager().get(noteIdPane.getText());
        if (current != null) {
            current.setText(textEditorPane.getText());
        } else {
            SyncNoteCore.getInst().getManager()
                    .addNote(new Note(noteIdPane.getText(), textEditorPane.getText()));
        }
    }

    private void prevNote() {
        saveCurrentNote();
        List<Note> notes = SyncNoteCore.getInst().getManager().getAllNotes();
        String curr = noteIdPane.getText(); // we store the current note
        if (curr.isEmpty()) {
            // show first
            Note note = notes.get(0);
            if (note != null) showNote(note);
            return;
        }
        if (notes.size() > 1) {
            // check for the previous note by iterating through all notes
            prevNote.setForeground(Color.BLACK);
            nextNote.setForeground(Color.BLACK);
            Note prev = null; // keep track of the previous note
            for (Note n : notes) {
                if (n.getId().equals(curr)) { // when we get to the current, we know the previous
                    if (prev == null) { // if we are on the first note, prev is null
                        prevNote.setForeground(Color.RED);
                        break;
                    } else { // otherwise, set this window to the previous note
                        showNote(prev);
                        break;
                    }
                }
                prev = n;
            }
        } else {
            // no other notes to view
            prevNote.setForeground(Color.RED);
            nextNote.setForeground(Color.RED);
        }
    }

    private void nextNote() {
        saveCurrentNote();
        List<Note> notes = SyncNoteCore.getInst().getManager().getAllNotes();
        // this works just like the last one, except we store a boolean for "next"
        // instead of storing the Note object for "previous"
        String curr = noteIdPane.getText();
        if (curr.isEmpty()) {
            Note note = notes.get(0);
            if (note != null) showNote(note);
            return;
        }
        if (notes.size() > 1) {
            prevNote.setForeground(Color.BLACK);
            nextNote.setForeground(Color.BLACK);
            boolean next = false;
            for (Note n : notes) {
                if (!next) {
                    if (n.getId().equals(curr)) {
                        next = true;
                    }
                } else {
                    noteIdPane.setText(n.getId());
                    textEditorPane.setText(n.getText());
                    next = false;
                    break;
                }
            }
            if (next) { // if we are on the last note, next will be set to true
                // but there will be no next note
                nextNote.setForeground(Color.RED);
            }
        } else {
            prevNote.setForeground(Color.RED);
            nextNote.setForeground(Color.RED);
        }
    }

    private void upload() {
        saveCurrentNote(); // ...

        // encode all notes to the json string, then attempt uploading to server
        String key = SyncNoteCore.getInst().getConfig().getAuthToken();
        String text = SyncNoteCore.getInst().getParser().encode();
        try {
            HTTPTasks.uploadText(key, text);
        } catch (RequestForbiddenException e) {
            // and provide feedback when things go wrong
            JOptionPane.showMessageDialog(this, "Couldn't upload your note because your session "
                            + "key is invalid. Try logging back in.",
                    "Error Uploading", JOptionPane.ERROR_MESSAGE);
        } catch (RequestInvalidException e) {
            JOptionPane.showMessageDialog(this, "You need to login to do that.",
                    "Error Uploading", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void download() {
        String curr = noteIdPane.getText();
        String key = SyncNoteCore.getInst().getConfig().getAuthToken();
        try {
            // downloads all notes and parses them into the manager
            String res = HTTPTasks.downloadText(key);
            SyncNoteCore.getInst().getParser().decode(res);

            List<Note> notes = SyncNoteCore.getInst().getManager().getAllNotes();
            if (notes.isEmpty()) {
                textEditorPane.setText("You have no notes.");
            } else {
                if (curr.isEmpty()) { // display the first note if we weren't already looking at a note
                    showNote(notes.get(0));
                } else {
                    // try to display the same we just had
                    Note note = SyncNoteCore.getInst().getManager().get(curr);
                    if (note != null) {
                        showNote(note);
                    } else { // note was deleted remotely perhaps, set to first note
                        showNote(notes.get(0));
                    }
                }
            }
        } catch (RequestForbiddenException e) {
            JOptionPane.showMessageDialog(this, "Couldn't download your note because your session "
                            + "key is invalid. Try logging back in.",
                    "Error Downloading", JOptionPane.ERROR_MESSAGE);
        } catch (RequestInvalidException e) {
            JOptionPane.showMessageDialog(this, "You need to login to do that.",
                    "Error Downloading", JOptionPane.ERROR_MESSAGE);
        } catch (InvalidNotesFileException e) {
            JOptionPane.showMessageDialog(this, "Your notes file was corrupt. Try reuploading your notes.",
                    "Error Downloading", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteNote() {
        String current = noteIdPane.getText();
        if (!current.isEmpty()) {
            // remove current note from manager
            SyncNoteCore.getInst().getManager().remove(current);
        }
        // get a list of notes (minus the one that was just deleted)
        List<Note> remaining = SyncNoteCore.getInst().getManager().getAllNotes();
        if (!remaining.isEmpty()) {
            // try to show the next remaining note
            Note next = remaining.get(0);
            showNote(next);
        } else {
            noteIdPane.setText("");
            textEditorPane.setText("You have no notes left. Type a title above to start writing"
                    + " a new note.");
        }
    }

    private void openSettings() {
        // TODO full settings menu, not just login
        if (SyncNoteCore.getInst().getConfig().getAuthToken().isEmpty()) {
            EventQueue.invokeLater(() -> {
                LoginDialog login = new LoginDialog(parentApp);
                login.setVisible(true);
            });
        } else {
            // logout
            upload();
            SyncNoteCore.getInst().getManager().removeAll();
            SyncNoteCore.getInst().getConfig().setAuthToken("");
            Note n = new Note("Logged Out", "You are logged out.\nNotes you write now will not be synced.");
            for (NoteWindow win : parentApp.activeWindows) {
                win.showNote(n);
            }
        }
    }

}