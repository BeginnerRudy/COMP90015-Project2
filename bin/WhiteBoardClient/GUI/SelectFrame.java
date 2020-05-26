package WhiteBoardClient.GUI;

import javax.swing.*;
import java.awt.*;

public class SelectFrame {
    public JFrame frame;
    private JPanel panel;

    private JList userList;

    public static void main(String[] args) {
        new SelectFrame(new DefaultListModel()).frame.setVisible(true);
    }

    public SelectFrame(DefaultListModel dlm) {

        // Initialize the frame
        frame = new JFrame();
        frame.setBounds(100, 100, 200, 300);
        frame.getContentPane().setLayout(null);
        frame.setTitle("Users ");

        // Initialize the panel
        panel = new JPanel();
        panel.setBounds(0, 0, 200, 300);

        // add data
        userList = new JList(dlm);
        userList.setFont(new Font("Comic Sans MS", Font.PLAIN, 18));
        dlm.addElement("rudy");
        dlm.addElement("rudy");
        dlm.addElement("rudy");
        dlm.addElement("rudy");
        dlm.addElement("rudy");
        userList.revalidate();
        userList.repaint();
        userList.setVisible(true);

        JScrollPane scrollPane = new JScrollPane(userList);
        scrollPane.setVisible(true);

        scrollPane.setBounds(26, 50, 130, 180);


        panel.add(scrollPane);
        frame.getContentPane().add(panel);
        panel.setLayout(null);

    }

    public JList getUserList() {
        return userList;
    }
}
