package WhiteBoardClient;

import RemoteInterface.IRemoteWhiteBoard;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginFrame {
    public JFrame frame;
    private JPanel panel;

    public LoginFrame() {

        // Initialize the frame
        frame = new JFrame();
        frame.setBounds(100, 100, 603, 670);
        frame.getContentPane().setLayout(null);
        frame.setTitle("Client Whiteboard ");

        // Initialize the panel
        panel = new JPanel();
        panel.setBounds(0, 0, 581, 614);
        frame.getContentPane().add(panel);
        panel.setLayout(null);

        // Enter dialogue textField
        JTextField port = new JTextField();
        port.setBounds(260, 450, 181, 32);
        panel.add(port);
        port.setColumns(10);

        // Enter dialogue textField
        JTextField username = new JTextField();
        username.setBounds(260, 497, 181, 32);
        panel.add(username);
        username.setColumns(10);
        // -----------------------------------------------------

        // 'send' button
        JButton btnSend = new JButton("Join ");
        btnSend.setBounds(446, 496, 69, 33);
        panel.add(btnSend);

        btnSend.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                // communicate with the Remote White board

                // if could join
                if (LoginController.getLoginController().join(username.getText())){
                    frame.dispose();
                    new WhiteBoardClientGUI().frame.setVisible(true);
                }

            }
        });


    }
}
