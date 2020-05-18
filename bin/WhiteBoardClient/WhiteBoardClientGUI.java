package WhiteBoardClient;

import RemoteInterface.IRemoteWhiteBoard;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileSystemView;

/**
 * @author Chaoxian Zhou, Yangyang Long, Jiuzhou Han, Wentao Yan
 * @date 19/10/2019
 */
public class WhiteBoardClientGUI {

    public static void main(String[] args) {
        new WhiteBoardClientGUI().frame.setVisible(true);
    }

    // Define GUI elements
    public JFrame frame;
    private JLabel titleOfFrame;
    //    private Client client;
    private JPanel panel;
    private JScrollPane scrollPaneForStatus;
    public JButton closeWhiteBoardButton;
    private JButton quitBoardContentButton;
    public JButton kickButton;
    private JLabel userListTitle;
    public JList<String> userList;
    // ---------------------------------
    private JTextPane chatPane;
    // ---------------------------------
    private Thread uploadImg;
    private Thread downloadImg;
    public DefaultListModel<String> listModel;
    BufferedImage getIma;

    public WhiteBoard canvas;
    WhiteBoardController whiteBoardController;
    JScrollPane scroller;
    JButton createBtn;

    public JMenuBar mb;

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
//        frame.setBounds(100, 100, 603, 670);

        this.frame.setSize(828, 893);
        frame.setLayout(new BorderLayout());
//        frame.getContentPane().setLayout(new BorderLayout());
        frame.setTitle("Client Whiteboard ");

        // Initialize the panel
        panel = new JPanel();
        panel.setBounds(0, 0, 300, 600);
//        panel.setLayout(null);

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
        listModel = new DefaultListModel();
        userList = new JList(listModel);
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

        // 'Join WhiteBoard' button
        closeWhiteBoardButton = new JButton("Close WhiteBoard");
        closeWhiteBoardButton.setFont(new Font("Comic Sans MS", Font.PLAIN, 18));
        closeWhiteBoardButton.setBounds(25, 56, 209, 29);
        panel.add(closeWhiteBoardButton);
        closeWhiteBoardButton.setVisible(false);
        // 'Clear Board' button
        quitBoardContentButton = new JButton("Quit Board");
        quitBoardContentButton.setFont(new Font("Comic Sans MS", Font.PLAIN, 18));
        quitBoardContentButton.setBounds(26, 100, 208, 29);
        panel.add(quitBoardContentButton);

        // 'Clear Board' button
        kickButton = new JButton("Kick");
        kickButton.setFont(new Font("Comic Sans MS", Font.PLAIN, 18));
        kickButton.setBounds(26, 144, 208, 29);
        panel.add(kickButton);
        kickButton.setVisible(false);

        // 'Clear Board' button
        createBtn = new JButton("Create");
        createBtn.setFont(new Font("Comic Sans MS", Font.PLAIN, 18));
        createBtn.setBounds(26, 188, 208, 29);
        panel.add(createBtn);
        createBtn.setVisible(false);


        // Add menu bar
        // create a menubar
        mb = new JMenuBar();

        // create a menu
        JMenu x = new JMenu("Menu");
        JMenu x1 = new JMenu("submenu");

        // create menuitems
        JMenuItem m1 = new JMenuItem("Save");
        JMenuItem m2 = new JMenuItem("Save As");
        JMenuItem m3 = new JMenuItem("Open");
        JMenuItem s1 = new JMenuItem("SubMenuItem1");
        JMenuItem s2 = new JMenuItem("SubMenuItem2");

        // add ActionListener to menuItems
        m1.addActionListener(e -> {
            System.out.println("Click on save");
            LoginController.getLoginController().save();
        });

        m2.addActionListener(e -> {

            System.out.println("Click on Save as");
            JFileChooser j = new JFileChooser("./data/");
            j.showDialog(this.frame, "Save as");
            j.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
//            String filename = "d.png";
            File file = j.getSelectedFile();
            if (file == null) {
                System.out.println("no file selected");
                return;
            }
            LoginController.getLoginController().saveAs(file);
        });

        m3.addActionListener(e -> {
            System.out.println("Click on open");

            JFileChooser filePicker = new JFileChooser("./data");
            filePicker.setFileSelectionMode(JFileChooser.FILES_ONLY);
            filePicker.showDialog(this.frame, "Select File");

            File file = filePicker.getSelectedFile();
            if (file == null) {
                System.out.println("no file selected");
                return;
            }
            LoginController.getLoginController().open(file);
        });

        // add menu items to menu
        x.add(m1);
        x.add(m2);
        x.add(m3);
        x1.add(s1);
        x1.add(s2);

        // add submenu
        x.add(x1);

        // add menu to menu bar
        mb.add(x);

        // add menubar to frame
        frame.setJMenuBar(mb);
        mb.setVisible(false);
        frame.getContentPane().add(panel, BorderLayout.EAST);


        quitBoardContentButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                System.out.println("User clicked on quit");
                LoginController.getLoginController().quit();
            }
        });

        closeWhiteBoardButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                System.out.println("User clicked on close");
                LoginController.getLoginController().close();
            }
        });

        kickButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                System.out.println("kick");
                String username = (String) userList.getSelectedValue();
                LoginController.getLoginController().kick(username);
            }
        });

        createBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                System.out.println("create white board");
//                String username = (String) userList.getSelectedValue();
                LoginController.getLoginController().createWhiteBoard();
            }
        });
    }

    public void createCanvas(BufferedImage canvas, IRemoteWhiteBoard remoteWhiteBoard, String username) {
        //        Container contentPane = this.frame.getContentPane();
        Panel c = new Panel();
        this.canvas = new WhiteBoard(canvas);
        whiteBoardController = new WhiteBoardController(this.canvas, remoteWhiteBoard, username);
        this.whiteBoardController.setBounds(350, 0, 300, 600);

        this.scroller = new JScrollPane(this.canvas);
        this.scroller.setBackground(Color.LIGHT_GRAY);
        this.scroller.setBounds(350, 0, 300, 600);
//        contentPane.setBounds(350, 0, 300, 600);

        // Add the canvas and controls to the main GUI. Canvas above controls.
        c.add(whiteBoardController);
        c.add(scroller);
        this.frame.getContentPane().add(c, BorderLayout.SOUTH);
//        c.revalidate();
//        c.repaint();
        this.frame.getContentPane().revalidate();
        this.frame.getContentPane().repaint();
//        this.frame.revalidate();
//        this.frame.repaint();
    }
}
