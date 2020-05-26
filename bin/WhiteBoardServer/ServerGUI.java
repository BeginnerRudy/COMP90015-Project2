package WhiteBoardServer;

import javax.swing.*;
import java.awt.*;

public class ServerGUI {
    private JFrame frame;
    private JPanel panel;

    private JTextField managerName;

    private JButton closeButton;
    public static void main(String[] args) {
        new ServerGUI().frame.setVisible(true);
    }

    public ServerGUI() {
        // Initialize the frame
        frame = new JFrame();

        this.frame.setSize(250, 200);
        frame.getContentPane().setLayout(null);
        frame.setTitle(" Whiteboard Server ");

        // Initialize the panel
        panel = new JPanel();
        panel.setLayout(null);
        panel.setBounds(0, 0, 250, 200);

        // The prompt label for port
        JLabel manager_label = new JLabel();
        manager_label.setBounds(10, 40, 100, 32);
        panel.add(manager_label);
        manager_label.setText("Manager");

        // Enter dialogue textField
        managerName = new JTextField();
        managerName.setBounds(85, 40, 150, 32);
        panel.add(managerName);
        managerName.setBackground(Color.white);
        managerName.setColumns(10);
        managerName.setEditable(false);

        // Close WhiteBoard' button
        closeButton = new JButton("Close");
        closeButton.setFont(new Font("Comic Sans MS", Font.PLAIN, 18));
        closeButton.setBounds(110, 90, 100, 29);
        panel.add(closeButton);

        frame.getContentPane().add(panel);
        frame.setVisible(true);
    }

    public JButton getCloseButton() {
        return closeButton;
    }

    public JTextField getManagerName() {
        return managerName;
    }
}
