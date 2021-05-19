package WhiteBoardClient.GUI;

import WhiteBoardClient.ClientController;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * This class is responsible for the login GUI on the client side
 */
public class WhiteBoardLoginFrame {
    public JFrame frame;
    private JPanel panel;

    public WhiteBoardLoginFrame() {

        // Initialize the frame
        frame = new JFrame();
        frame.setBounds(100, 100, 400, 300);
        frame.getContentPane().setLayout(null);
        frame.setTitle("Client Whiteboard ");

        // Initialize the panel
        panel = new JPanel();
        panel.setBounds(0, 0, 400, 300);
        frame.getContentPane().add(panel);
        panel.setLayout(null);

        int textField_x = 130;
        int label_text_x_gap = 80;
        int textField_y_gap = 45;
        int port_textFiled_y = 120;

        // set icon
        JLabel icon = new JLabel();
        icon.setText("Shared Canvas");
//        icon.setIcon(new ImageIcon(this.getClass().getResource("/home/rudy/Code/Distributed-Computing/Distributed-Whiteboard/bin/whiteboard.jpg")));
        icon.setBounds(textField_x + 35, port_textFiled_y - 2 * textField_y_gap, 120, 33);
        panel.add(icon);

        // The prompt label for port
        JLabel host_label = new JLabel();
        host_label.setBounds(textField_x - label_text_x_gap, port_textFiled_y - textField_y_gap, 50, 32);
        panel.add(host_label);
        host_label.setText("Host");

        // Enter dialogue textField
        JTextField host = new JTextField();
        host.setBounds(textField_x, port_textFiled_y - textField_y_gap, 181, 32);
        panel.add(host);
        host.setColumns(10);


        // The prompt label for port
        JLabel port_label = new JLabel();
        port_label.setBounds(textField_x - label_text_x_gap, port_textFiled_y, 50, 32);
        panel.add(port_label);
        port_label.setText("Port");

        // Enter dialogue textField
        JTextField port = new JTextField();
        port.setBounds(textField_x, port_textFiled_y, 181, 32);
        panel.add(port);
        port.setColumns(10);

        // The prompt label for port
        JLabel username_label = new JLabel();
        username_label.setBounds(textField_x - label_text_x_gap, port_textFiled_y + textField_y_gap, 80, 32);
        panel.add(username_label);
        username_label.setText("Username");

        // Enter dialogue textField
        JTextField username = new JTextField();
        username.setBounds(textField_x, port_textFiled_y + textField_y_gap, 181, 32);
        panel.add(username);
        username.setColumns(10);
        // -----------------------------------------------------

//         'send' button
        JButton btnJoin = new JButton("Join ");
        btnJoin.setBounds(textField_x + 55, port_textFiled_y + textField_y_gap * 2, 69, 33);
        panel.add(btnJoin);


        btnJoin.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                int portNum;
                if (username.getText() == null || username.getText().equals("")) {
                    JOptionPane.showMessageDialog(frame, "Please enter null empty username!");

                } else {
                    try {
                        portNum = Integer.parseInt(port.getText());
                        ClientController.getClientController().join(username.getText(), host.getText(), portNum);
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(frame, "Please enter integer for port number!");
                        e.printStackTrace();
                    }
                }
            }
        });


    }
}
