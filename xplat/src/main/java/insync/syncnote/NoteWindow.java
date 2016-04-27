package insync.syncnote;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import insync.syncnote.exceptions.InvalidNotesFileException;
import insync.syncnote.exceptions.RequestForbiddenException;
import insync.syncnote.exceptions.RequestInvalidException;

public class NoteWindow extends JFrame {

    private SyncNoteApplication parentApp;
    private JEditorPane textEditorPane;
    private JTextPane noteIdPane;
    private boolean docChanged;
    private Timer updateTask;

    public NoteWindow(SyncNoteApplication parent) {
        this.parentApp = parent;
        init();
    }

    private void init() {
        JButton newButton = new JButton("+");
        newButton.addActionListener(e -> EventQueue.invokeLater(() -> {
            NoteWindow newWindow = new NoteWindow(parentApp);
            newWindow.setVisible(true);
            parentApp.activeWindows.add(newWindow);
            newWindow.noteIdPane.setText(this.noteIdPane.getText());
            newWindow.textEditorPane.setText(this.textEditorPane.getText());
        }));
        JButton uploadButton = new JButton("U");
        JButton downloadButton = new JButton("D");
        uploadButton.addActionListener(e -> upload());
        downloadButton.addActionListener(e -> download());

        JButton settingsButton = new JButton("L");
        JButton deleteButton = new JButton("X");
        settingsButton.addActionListener(e -> openSettings());
        deleteButton.addActionListener(e -> deleteNote());

        textEditorPane = new JEditorPane();
        noteIdPane = new JTextPane();
        noteIdPane.setBackground(Color.GRAY);

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

        updateTask = new Timer(100, e -> {
            if (docChanged && !noteIdPane.isFocusOwner()) {
                if (!noteIdPane.getText().isEmpty()) {
                    saveCurrentNote();
                }
            }
        });

        JButton prevNote = new JButton("<");
        JButton nextNote = new JButton(">");
        prevNote.addActionListener(e -> {
            saveCurrentNote();
            List<Note> notes = SyncNoteCore.getInst().getManager().getAllNotes();
            if (notes.size() > 1) {
                prevNote.setForeground(Color.BLACK);
                nextNote.setForeground(Color.BLACK);
                Note prev = null;
                String curr = noteIdPane.getText();
                for (Note n : notes) {
                    if (n.getId().equals(curr)) {
                        if (prev == null) {
                            prevNote.setForeground(Color.RED);
                            break;
                        } else {
                            noteIdPane.setText(prev.getId());
                            textEditorPane.setText(prev.getText());
                            break;
                        }
                    }
                    prev = n;
                }
            } else {
                prevNote.setForeground(Color.RED);
                nextNote.setForeground(Color.RED);
            }
        });
        nextNote.addActionListener(e -> {
            saveCurrentNote();
            List<Note> notes = SyncNoteCore.getInst().getManager().getAllNotes();
            if (notes.size() > 1) {
                prevNote.setForeground(Color.BLACK);
                nextNote.setForeground(Color.BLACK);
                boolean next = false;
                String curr = noteIdPane.getText();
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
                if (next) {
                    nextNote.setForeground(Color.RED);
                }
            } else {
                prevNote.setForeground(Color.RED);
                nextNote.setForeground(Color.RED);
            }
        });

        JButton exitButton = new JButton("X");
        exitButton.addActionListener(e -> close());

        JComponent[] titlePane = new JComponent[] {
                newButton,
                new JPanel(),
                settingsButton,
                exitButton
        };
        JComponent[] notePane = new JComponent[] {
                //newButton,
                uploadButton, downloadButton,
                prevNote,
                noteIdPane,
                nextNote,
                //settingsButton,
                deleteButton,
                //exitButton
        };

        setUndecorated(true);

        setupUI(titlePane, notePane, textEditorPane);

        setTitle("SyncNote");
        setSize(300, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                parentApp.activeWindows.remove(NoteWindow.this);
                parentApp.notifyLock();
            }
        });
        updateTask.start();
    }

    public void close() {
        saveCurrentNote();
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    private void saveCurrentNote() {
        if (noteIdPane.getText().isEmpty()) return;
        Note current = SyncNoteCore.getInst().getManager().get(noteIdPane.getText());
        if (current != null) {
            current.setText(textEditorPane.getText());
        } else {
            SyncNoteCore.getInst().getManager()
                    .addNote(new Note(noteIdPane.getText(), textEditorPane.getText()));
        }
    }

    private void setupUI(JComponent[] titlePane, JComponent[] notePane, JEditorPane textBox) {
        JPanel titleBar = new JPanel();
        titleBar.setLayout(new GridLayout(1, titlePane.length));
        for (JComponent component : titlePane) {
            titleBar.add(component);
        }
        JPanel noteBar = new JPanel();
        noteBar.setLayout(new GridLayout(1, notePane.length));
        for (JComponent component : notePane) {
            noteBar.add(component);
        }

        JPanel topBars = new JPanel();
        topBars.setLayout(new GridLayout(2, 1));
        topBars.add(titleBar);
        topBars.add(noteBar);

        getContentPane().add(topBars, BorderLayout.NORTH);
        getContentPane().add(textBox, BorderLayout.CENTER);


        this.pack();
    }

    private void upload() {
        if (!noteIdPane.getText().isEmpty()) {
            SyncNoteCore.getInst().getManager().addNote(new Note(noteIdPane.getText(), textEditorPane.getText()));
        }

        String key = SyncNoteCore.getInst().getConfig().getAuthToken();
        String text = SyncNoteCore.getInst().getParser().encode();
        try {
            HTTPTasks.uploadText(key, text);
        } catch (RequestForbiddenException e) {
            JOptionPane.showMessageDialog(this, "Couldn't upload your note because your session "
                            + "key is invalid. Try logging back in.",
                    "Error Uploading", JOptionPane.ERROR_MESSAGE);
        } catch (RequestInvalidException e) {
            JOptionPane.showMessageDialog(this, "You need to login to do that.",
                    "Error Uploading", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void triggerDownload() {
        download();
    }

    private void download() {
        String key = SyncNoteCore.getInst().getConfig().getAuthToken();
        try {
            String res = HTTPTasks.downloadText(key);

            SyncNoteCore.getInst().getParser().decode(res);
            List<Note> notes = SyncNoteCore.getInst().getManager().getAllNotes();
            if (notes.isEmpty()) {
                textEditorPane.setText("You have no notes :(");
            } else {
                textEditorPane.setText(notes.get(0).getText());
                noteIdPane.setText(notes.get(0).getId());
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
            SyncNoteCore.getInst().getManager().remove(current);
        }
        List<Note> remaining = SyncNoteCore.getInst().getManager().getAllNotes();
        if (!remaining.isEmpty()) {
            Note next = remaining.get(0);
            noteIdPane.setText(next.getId());
            textEditorPane.setText(next.getText());
        } else {
            noteIdPane.setText("");
            textEditorPane.setText("You have no notes left :(");
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
            noteIdPane.setText("");
            textEditorPane.setText("You have no notes left :(");
        }
    }

}
