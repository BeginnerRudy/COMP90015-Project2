package WhiteBoardClient;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListModel;
import javax.swing.JList;

/**
 * @author Chaoxian Zhou, Yangyang Long, Jiuzhou Han, Wentao Yan
 * @date 19/10/2019
 */
public class WhiteBoardClientGUI {

    // Define GUI elements
    public JFrame frame;
    private JLabel titleOfFrame;
//    private Client client;
    private JPanel panel;
    private JScrollPane scrollPaneForStatus;
    private JButton joinWhiteBoardButton;
    private JButton clearBoardContentButton;
    private JLabel userListTitle;
    private JList<String> userList;
    // ---------------------------------
    private JTextPane chatPane;
    // ---------------------------------
    private Thread uploadImg;
    private Thread downloadImg;
    BufferedImage getIma;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    LoginFrame loginFrame = new LoginFrame();
                    loginFrame.frame.setVisible(true);

//                    WhiteBoardClientGUI window = new WhiteBoardClientGUI();
//                    window.frame.setVisible(true);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create an empty constructor here for 'Client_Connection' to determine who is the manager and who are clients
     */
    public WhiteBoardClientGUI() {
        // For coding only
        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    public void initialize() {

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

        // Initialize the title of the frame
        titleOfFrame = new JLabel("Client GUI DEMO");
        titleOfFrame.setFont(new Font("Comic Sans MS", Font.PLAIN, 18));
        titleOfFrame.setBounds(26, 15, 219, 34);
        panel.add(titleOfFrame);

        // Initialize the scroll bar for user list
        scrollPaneForStatus = new JScrollPane();
        scrollPaneForStatus.setBounds(26, 240, 208, 289);
        panel.add(scrollPaneForStatus);

        // User list
        userList = new JList();
        userList.setFont(new Font("Comic Sans MS", Font.PLAIN, 18));
        scrollPaneForStatus.setViewportView(userList);

        // Initialize the area to display connection status
		/*statusArea = new JTextArea();
		statusArea.setFont(new Font("Comic Sans MS", Font.PLAIN, 20));
		statusArea.setText("");
		statusArea.setEditable(false);
		scrollPaneForStatus.setViewportView(statusArea);*/

        userListTitle = new JLabel("User List");
        userListTitle.setFont(new Font("Comic Sans MS", Font.PLAIN, 18));
        userListTitle.setBounds(26, 191, 219, 34);
        panel.add(userListTitle);

        // 'Clear Board' button
        clearBoardContentButton = new JButton("Quit Board");
        clearBoardContentButton.setFont(new Font("Comic Sans MS", Font.PLAIN, 18));
        clearBoardContentButton.setBounds(26, 100, 208, 29);
        panel.add(clearBoardContentButton);

//        // 'Join WhiteBoard' button
//        joinWhiteBoardButton = new JButton("Join WhiteBoard");
//        joinWhiteBoardButton.setFont(new Font("Comic Sans MS", Font.PLAIN, 18));
//        joinWhiteBoardButton.setBounds(25, 56, 209, 29);
//        panel.add(joinWhiteBoardButton);
    }
}
