package insync.syncnote;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.table.AbstractTableModel;

public class SettingsWindow extends JFrame {

    private static final Color CANARY = new Color(252, 250, 173);
    private final SyncNoteApplication parentApp;

    public SettingsWindow(SyncNoteApplication parent) {
        this.parentApp = parent;
        init();
    }

    private void init() {
        setUndecorated(true);

        // hacks because swing can't do this otherwise
        UIManager.put("TabbedPane.selected", CANARY);
        UIManager.put("TabbedPane.borderHighlightColor", CANARY);
        UIManager.put("TabbedPane.darkShadow", CANARY);
        UIManager.put("TabbedPane.light", CANARY);
        UIManager.put("TabbedPane.selectHighlight", CANARY);
        UIManager.put("TabbedPane.darkShadow", CANARY);
        UIManager.put("TabbedPane.focus", CANARY);
        UIManager.put("TableHeader.background", CANARY);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(CANARY);

        JPanel cardAccount = new JPanel();
        cardAccount.setLayout(new BorderLayout());
        JLabel logLabel;
        JButton logBtn;
        CoreConfig config = SyncNoteCore.getInst().getConfig();
        if (config.getAuthToken().isEmpty()) {
            logBtn = createButton("Login/Register", "Login to an existing account or make a new one.");
            logBtn.addActionListener(e -> login());
            logLabel = new JLabel("You are not logged in.", SwingConstants.CENTER);
        } else {
            logBtn = createButton("Logout", "Logout of SyncNote");
            logBtn.addActionListener(e -> logout());
            logLabel = new JLabel("You are currently logged in.", SwingConstants.CENTER);
        }
        cardAccount.setBackground(CANARY);
        cardAccount.add(logLabel, BorderLayout.CENTER);
        cardAccount.add(logBtn, BorderLayout.SOUTH);
        cardAccount.setMaximumSize(new Dimension(200, 200));

        JPanel cardNotes = new JPanel();
        cardNotes.setLayout(new BorderLayout());
        cardNotes.setToolTipText("Click to open note, or use checkboxes and buttons below.");
        List<Note> namesList = SyncNoteCore.getInst().getManager().getAllNotes();
        Object[][] data = new Object[namesList.size()][2];
        for (int i = 0; i < namesList.size(); i++) {
            data[i][0] = false;
            data[i][1] = namesList.get(i).getId();
        }
        String[] columns = {"", "Note Title"};
        JTable notesList = new JTable(new AbstractTableModel() {
            @Override
            public String getColumnName(int col) {
                return columns[col];
            }

            @Override
            public int getRowCount() {
                return data.length;
            }

            @Override
            public int getColumnCount() {
                return columns.length;
            }

            @Override
            public void setValueAt(Object value, int row, int col) {
                data[row][col] = value;
                fireTableCellUpdated(row, col);
            }

            @Override
            public Class getColumnClass(int column) {
                // ensure first column renders as check boxes
                switch (column) {
                    case 0:
                        return Boolean.class;
                    case 1:
                    default:
                        return String.class;
                }
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 0;
            }

            @Override
            public Object getValueAt(int rowIndex, int columnIndex) {
                return data[rowIndex][columnIndex];
            }
        });
        notesList.getColumnModel().getColumn(0).setMaxWidth(20);
        notesList.setFillsViewportHeight(true);
        JScrollPane notesScroll = new JScrollPane(notesList);
        cardNotes.add(notesScroll, BorderLayout.CENTER);
        JPanel notesButtons = new JPanel(new GridLayout(1, 2));
        JButton openSelected = createButton("Open Notes", "Open all selected notes in new windows.");
        JButton deleteSelected = createButton("Delete Notes", "Delete all currently selected notes.");
        openSelected.addActionListener(e -> open(notesList));
        deleteSelected.addActionListener(e -> delete(notesList));
        notesButtons.add(openSelected);
        notesButtons.add(deleteSelected);
        cardNotes.add(notesButtons, BorderLayout.SOUTH);


        tabs.add("Account", cardAccount);
        tabs.add("Notes", cardNotes);
        tabs.add("Close", new JPanel());
        tabs.addChangeListener(e -> {
            JTabbedPane tabbedPane = ((JTabbedPane) e.getSource());
            if (tabbedPane.getSelectedIndex() == 2) { // close tab
                dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
            }
        });

        tabs.setBackgroundAt(0, CANARY);
        tabs.setBackgroundAt(1, CANARY);
        tabs.setBackgroundAt(2, CANARY);

        getContentPane().add(tabs);
        getContentPane().setBackground(CANARY);
        pack();

        setTitle("SyncNote Settings");
        setSize(250, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    private JButton createButton(String name, String toolTip) {
        JButton btn = new JButton(name);
        btn.setToolTipText(toolTip);
        btn.setBackground(CANARY);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        return btn;
    }

    private void login() {
        if (SyncNoteCore.getInst().getConfig().getAuthToken().isEmpty()) {
            EventQueue.invokeLater(() -> {
                LoginDialog login = new LoginDialog(parentApp);
                login.setVisible(true);
            });
        }
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    private void logout() {
        SyncNoteCore.getInst().getManager().removeAll();
        SyncNoteCore.getInst().getConfig().setAuthToken("");
        String n = "You are logged out.\nNotes you write now will not be synced.";
        for (NoteWindow win : parentApp.activeWindows) {
            win.setTextBox(n);
        }
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    private void open(JTable table) {
        for (int i = 0; i < table.getRowCount(); i++) {
            if (((Boolean) table.getValueAt(i, 0))) {
                String id = ((String) table.getValueAt(i, 1));
                Note n = SyncNoteCore.getInst().getManager().get(id);
                if (n != null) {
                    EventQueue.invokeLater(() -> {
                        NoteWindow newWindow = new NoteWindow(parentApp);
                        newWindow.showNote(n);
                        newWindow.setVisible(true);
                        parentApp.activeWindows.add(newWindow);
                    });
                }
            }
        }
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }

    private void delete(JTable table) {
        String n = "You are logged out.\nNotes you write now will not be synced.";
        for (int i = 0; i < table.getRowCount(); i++) {
            if (((Boolean) table.getValueAt(i, 0))) {
                String id = ((String) table.getValueAt(i, 1));
                SyncNoteCore.getInst().getManager().remove(id);
                for (NoteWindow noteWindow : parentApp.activeWindows) {
                    if (noteWindow.getCurrentNoteId().equals(id)) {
                        noteWindow.setTextBox(n);
                    }
                }
            }
        }
        dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
    }
}
