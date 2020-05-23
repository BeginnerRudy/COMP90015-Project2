package WhiteBoardClient.GUI;

import Utils.Mode;
import WhiteBoardClient.ClientController;
import WhiteBoardServer.IRemoteWhiteBoard;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.*;

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
    //    WhiteBoardController whiteBoardController;
    JScrollPane scroller;
    public JButton createBtn;

    // Drawing mode selector.
    public JComboBox<Mode> modeSelect;

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

        this.frame.setSize(900, 900);
        frame.setLayout(new BorderLayout());
        frame.setTitle("Client Whiteboard ");

        // Initialize the panel
        panel = new JPanel();
        panel.setBounds(0, 0, 300, 600);

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
            ClientController.getClientController().save();
        });

        m2.addActionListener(e -> {
            //TODO implement file type and check whether there is already a whiteboard

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
            ClientController.getClientController().saveAs(file);
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
            ClientController.getClientController().open(file);
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


        frame.getContentPane().add(panel, BorderLayout.NORTH);


        quitBoardContentButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                System.out.println("User clicked on quit");
                ClientController.getClientController().quit();
            }
        });

        closeWhiteBoardButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                System.out.println("User clicked on close");
                ClientController.getClientController().close();
            }
        });

        kickButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                System.out.println("kick");
                String username = (String) userList.getSelectedValue();
                ClientController.getClientController().kick(username);
            }
        });

        createBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
                System.out.println("create white board");
                ClientController.getClientController().createWhiteBoard();
            }
        });
    }

    public void createCanvas(BufferedImage canvas, IRemoteWhiteBoard remoteWhiteBoard, String username) {
        //        Container contentPane = this.frame.getContentPane();
        JPanel c = new JPanel();

        this.canvas = new WhiteBoard(canvas);
        this.modeSelect = new JComboBox<>(Mode.values());
        this.modeSelect.setEditable(false);
//        whiteBoardController = new WhiteBoardController(this.canvas, remoteWhiteBoard, username);

        this.scroller = new JScrollPane(this.canvas);
        this.scroller.setBackground(Color.LIGHT_GRAY);
        this.modeSelect.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == 1){
                    ClientController.getClientController().changeMode((Mode) e.getItem());
                }
            }
        });

        c.add(this.modeSelect);


        this.canvas.addMouseListener(ClientController.getClientController());
        this.canvas.addMouseMotionListener(ClientController.getClientController());
        this.canvas.addKeyListener(ClientController.getClientController());
        // Add the canvas and controls to the main GUI. Canvas above controls.
//        c.add(whiteBoardController);
        c.add(scroller);
        this.frame.getContentPane().add(c, BorderLayout.WEST);
        this.frame.getContentPane().revalidate();
        this.frame.getContentPane().repaint();
    }
}
