package WhiteBoardServer;

import javax.swing.*;
import java.awt.*;

public class ServerGUI {
    private JFrame frame;
    private JPanel panel;

    private JButton closeButton;


    public ServerGUI(){
        // Initialize the frame
        frame = new JFrame();

        this.frame.setSize(250, 250);
        frame.getContentPane().setLayout(null);
        frame.setTitle(" Whiteboard Server ");

        // Initialize the panel
        panel = new JPanel();
        panel.setLayout(null);
        panel.setBounds(0, 0, 250, 250);

        // Close WhiteBoard' button
        closeButton = new JButton("Close");
        closeButton.setFont(new Font("Comic Sans MS", Font.PLAIN, 18));
        closeButton.setBounds(75, 90, 100, 29);
        panel.add(closeButton);

        frame.getContentPane().add(panel);
        frame.setVisible(true);
    }

    public JButton getCloseButton() {
        return closeButton;
    }
}
