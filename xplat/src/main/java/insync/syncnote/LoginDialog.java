package insync.syncnote;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.Timer;

import insync.syncnote.exceptions.RequestForbiddenException;
import insync.syncnote.exceptions.RequestInvalidException;

public class LoginDialog extends JDialog {

    private final SyncNoteApplication parent;

    JPanel box = new JPanel(new BorderLayout(5, 10));
    JLabel titleText;

    JLabel name = new JLabel("Username: ");
    JLabel pass = new JLabel("Password: ");

    JTextField nameField = new JTextField();
    JPasswordField passField = new JPasswordField();

    JButton btnLogin = new JButton("Login");
    JButton btnReg = new JButton("Register");
    JButton btnCancel = new JButton("Cancel");

    public LoginDialog(SyncNoteApplication parent) {
        this.parent = parent;
        setupButtons();
        setupUI();
    }

    private void setupButtons() {
        btnLogin.addActionListener(e -> {
            titleText.setText("");
            if (inputFilled()) {
                // attempt login
                EventQueue.invokeLater(() -> {
                    String token = "";
                    try {
                        token = HTTPTasks.login(nameField.getText(), new String(passField.getPassword()));
                    } catch (RequestForbiddenException ex) {
                        titleText.setForeground(Color.RED);
                        titleText.setText("Your password did not match. Try again.");
                        return;
                    } catch (RequestInvalidException ex2) {
                        System.err.println("Emptiness check in login was skipped.");
                        ex2.printStackTrace();
                        return;
                    }
                    if (!token.isEmpty()) {
                        SyncNoteCore.getInst().getConfig().setAuthToken(token);
                        titleText.setForeground(Color.GREEN);
                        titleText.setText("Logged in successfully!");
                        parent.triggerDownload();
                        closeGracefully();
                    }
                });
            } else {
                titleText.setText("Username and password can't be blank!");
            }
        });

        btnReg.addActionListener(e2 -> {
            titleText.setText("");
            if (inputFilled()) {
                // attempt register
                EventQueue.invokeLater(() -> {
                    String usn = nameField.getText();
                    String pass = new String(passField.getPassword());
                    try {
                        HTTPTasks.register(usn, pass);
                    } catch (RequestForbiddenException ex) {
                        titleText.setForeground(Color.RED);
                        titleText.setText("That username is already taken. Please choose a different one.");
                        return;
                    } catch (RequestInvalidException ex2) {
                        System.err.println("Emptiness check in register was skipped.");
                        ex2.printStackTrace();
                        return;
                    }
                    String token = "";
                    try {
                        token = HTTPTasks.login(usn, pass);
                    } catch (RequestForbiddenException | RequestInvalidException ignored) {
                        // since we just registered, assume we're ok...
                    }
                    if (!token.isEmpty()) {
                        SyncNoteCore.getInst().getConfig().setAuthToken(token);
                        titleText.setForeground(Color.GREEN);
                        titleText.setText("Registered and logged in successfully!");
                        closeGracefully();
                        parent.triggerDownload();
                    }
                });
            } else {
                titleText.setText("Username and password can't be blank!");
            }
        });

        btnCancel.addActionListener(e3 -> {
            this.setVisible(false);
            this.dispose();
        });
    }

    private void closeGracefully() {
        btnLogin.setEnabled(false);
        btnReg.setEnabled(false);
        btnCancel.setEnabled(false);
        Timer timer = new Timer(750, e -> {
            this.setVisible(false);
            this.dispose();
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void setupUI() {
        setTitle("Login or Register");

        titleText = new JLabel("Enter your username and password.", SwingConstants.CENTER);
        titleText.setForeground(Color.BLACK);
        box.add(titleText, BorderLayout.NORTH);

        JPanel labels = new JPanel(new GridLayout(2, 1, 5, 5));
        labels.add(name);
        labels.add(pass);
        box.add(labels, BorderLayout.WEST);

        JPanel fields = new JPanel(new GridLayout(2, 1, 5, 5));
        fields.add(nameField);
        fields.add(passField);
        box.add(fields, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new GridLayout(1, 3, 5, 5));
        buttons.add(btnLogin);
        buttons.add(btnReg);
        buttons.add(btnCancel);
        box.add(buttons, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(btnLogin);

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().add(box);
        setPreferredSize(new Dimension(400, 150));
        setLocation(parent.getLocation());
        pack();
    }

    private boolean inputFilled() {
        boolean ret = true;
        if (nameField.getText().isEmpty()) {
            name.setForeground(Color.RED);
            ret = false;
        } else {
            name.setForeground(Color.BLACK);
        }
        if (new String(passField.getPassword()).isEmpty()) {
            pass.setForeground(Color.RED);
            ret = false;
        } else {
            pass.setForeground(Color.BLACK);
        }
        return ret;
    }
}
