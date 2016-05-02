/*
 * SyncNote 2016
 * CSC470 Final Project
 * Jan-Lucas Ott, Connor Davis, Nate Harris, Randell Carrido
 */

package insync.syncnote;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class DragPanel extends JPanel {
    private Point startClick;
    private JFrame parent; // the note window to which we belong

    public DragPanel(JFrame note) {
        this.parent = note;


        // record the point where we clicked first
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                startClick = e.getPoint();
            }
        });

        // move as the mouse is moved
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int currX = parent.getX();
                int currY = parent.getY();

                // find the mouse displacement due to this event
                int movX = (currX + e.getX()) - (currX + startClick.x);
                int movY = (currY + e.getY()) - (currY + startClick.y);

                // move window
                parent.setLocation(currX + movX, currY + movY);
            }
        });
    }
}
