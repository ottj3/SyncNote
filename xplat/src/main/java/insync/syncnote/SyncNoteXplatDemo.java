package insync.syncnote;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

public class SyncNoteXplatDemo extends JFrame {

    private JEditorPane textEditorPane;
    private JEditorPane keyEditorPane;

    public SyncNoteXplatDemo() {
        init();
    }

    private void init() {
        JButton uploadButton = new JButton("Upload");
        JButton downloadButton = new JButton("Download");
        uploadButton.addActionListener(e -> upload());
        downloadButton.addActionListener(e -> download());

        textEditorPane = new JEditorPane();
        keyEditorPane = new JEditorPane();

        setupUI(uploadButton, downloadButton, keyEditorPane, textEditorPane);

        setTitle("SyncNote Demo");
        setSize(300, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private void setupUI(JButton uploadButton, JButton downloadButton, JEditorPane keyEditorPane, JEditorPane textEditorPane) {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(1,3));
        panel.add(uploadButton);
        panel.add(keyEditorPane);
        panel.add(downloadButton);

        getContentPane().add(textEditorPane, BorderLayout.CENTER);
        getContentPane().add(panel, BorderLayout.SOUTH);

        this.pack();
    }

    private void upload() {
        String key = keyEditorPane.getText();
        String text = textEditorPane.getText();
        SyncNoteCore.uploadText(key, text);
    }

    private void download() {
        String key = keyEditorPane.getText();
        String res =  SyncNoteCore.downloadText(key);
        textEditorPane.setText(res);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            SyncNoteXplatDemo demo = new SyncNoteXplatDemo();
            demo.setVisible(true);
        });
    }
}