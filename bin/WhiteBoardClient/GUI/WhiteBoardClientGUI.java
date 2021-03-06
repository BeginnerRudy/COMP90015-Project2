package WhiteBoardClient.GUI;

import Utils.Mode;
import WhiteBoardClient.ClientController;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.*;


/**
 * This class is responsible for the main Client GUI of the shared whiteboard
 */
public class WhiteBoardClientGUI {

    // Define GUI elements
    public JFrame frame;
    //    private Client client;
    private JPanel panel;
    // Used as the scroll panel of the user list
    private JScrollPane scrollPaneForStatus;
    public JButton closeWhiteBoardButton;
    private JButton quitBoardContentButton;
    public JButton kickButton;
    private JLabel userListTitle;
    public JList<String> userList;
    // ---------------------------------
    public DefaultListModel<String> listModel;

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
        frame.getContentPane().setLayout(null);
        frame.setTitle("Client Whiteboard ");

        // Initialize the panel
        panel = new JPanel();
        panel.setLayout(null);
        panel.setBounds(0, 0, 900, 900);

        int width = 130;

        // 'Join WhiteBoard' button
        closeWhiteBoardButton = new JButton("Close");
        closeWhiteBoardButton.setFont(new Font("Comic Sans MS", Font.PLAIN, 18));
        closeWhiteBoardButton.setBounds(25, 56, width, 29);
        panel.add(closeWhiteBoardButton);
        closeWhiteBoardButton.setVisible(false);
        // 'Clear Board' button
        quitBoardContentButton = new JButton("Quit");
        quitBoardContentButton.setFont(new Font("Comic Sans MS", Font.PLAIN, 18));
        quitBoardContentButton.setBounds(26, 100, width, 29);
        panel.add(quitBoardContentButton);

        // 'Clear Board' button
        kickButton = new JButton("Kick");
        kickButton.setFont(new Font("Comic Sans MS", Font.PLAIN, 18));
        kickButton.setBounds(26, 144, width, 29);
        panel.add(kickButton);
        kickButton.setVisible(false);

        // 'Clear Board' button
        createBtn = new JButton("Create");
        createBtn.setFont(new Font("Comic Sans MS", Font.PLAIN, 18));
        createBtn.setBounds(26, 188, width, 29);
        panel.add(createBtn);
        createBtn.setVisible(false);


        userListTitle = new JLabel("User List");
        userListTitle.setFont(new Font("Comic Sans MS", Font.PLAIN, 18));
        userListTitle.setBounds(26, 220, width, 34);
        panel.add(userListTitle);

        // Initialize the scroll bar for user list
        scrollPaneForStatus = new JScrollPane();
        scrollPaneForStatus.setBounds(26, 270, width, 289);
        panel.add(scrollPaneForStatus);

        // User list
        listModel = new DefaultListModel();
        userList = new JList(listModel);
        userList.setFont(new Font("Comic Sans MS", Font.PLAIN, 18));
        scrollPaneForStatus.setViewportView(userList);


        // Add menu bar
        // create a menubar
        mb = new JMenuBar();

        // create a menu
        JMenu x = new JMenu("Menu");

        // create menuitems
        JMenuItem m1 = new JMenuItem("Save");
        JMenuItem m2 = new JMenuItem("Save As");
        JMenuItem m3 = new JMenuItem("Open");

        // add ActionListener to menuItems
        m1.addActionListener(e -> {
//            System.out.println("Click on save");
            ClientController.getClientController().save();
        });

        m2.addActionListener(e -> {

//            System.out.println("Click on Save as");
            if (this.canvas != null) {
                JFileChooser j = new JFileChooser("./data/");
                j.showDialog(this.frame, "Save as");
                j.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
//            String filename = "d.png";
                File file = j.getSelectedFile();
                if (file == null) {
//                    System.out.println("no file selected");
                    return;
                }
                ClientController.getClientController().saveAs(file);
            } else {
                JOptionPane.showMessageDialog(this.frame, "Please create a whiteboard first!");
            }


        });

        m3.addActionListener(e -> {
//            System.out.println("Click on open");

//            if (this.)
            if (ClientController.getClientController().isClosedByServer()){
                    JOptionPane.showMessageDialog(this.frame, "Invalid! The whiteboard has closed by server!");
            }else {
                JFileChooser filePicker = new JFileChooser("./data");
                filePicker.setFileSelectionMode(JFileChooser.FILES_ONLY);
                filePicker.showDialog(this.frame, "Select File");

                File file = filePicker.getSelectedFile();
                if (file == null) {
//                    System.out.println("no file selected");
                    return;
                }
                ClientController.getClientController().open(file);
            }
        });

        // add menu items to menu
        x.add(m1);
        x.add(m2);
        x.add(m3);


        // add menu to menu bar
        mb.add(x);

        // add menubar to frame
        frame.setJMenuBar(mb);
        mb.setVisible(false);


        frame.getContentPane().add(panel);


        quitBoardContentButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
//                System.out.println("User clicked on quit");
                ClientController.getClientController().quit();
            }
        });

        closeWhiteBoardButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
//                System.out.println("User clicked on close");
                ClientController.getClientController().close();
            }
        });

        kickButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
//                System.out.println("kick");
                String username = (String) userList.getSelectedValue();
                ClientController.getClientController().kick(username);
            }
        });

        createBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent arg0) {
//                System.out.println("create white board");
                ClientController.getClientController().createWhiteBoard();
            }
        });

        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                int res = JOptionPane.showConfirmDialog(frame,
                        "Are you sure you want to close this whiteboard?", "Close Window?",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);

                if ( res == JOptionPane.YES_OPTION){
                    if (ClientController.getClientController().isManager()&&!ClientController.getClientController().isClosedByServer()){
                        ClientController.getClientController().close();
                    }else {
                        ClientController.getClientController().quit();
                    }
                }
            }
        });
    }

    /**
     * This method is used to create canvas, that is the place where the user goes to draw on.
     *
     * @param canvas  The BufferedImage to put on to the Whiteboard
     */
    public void createCanvas(BufferedImage canvas) {
        //        Container contentPane = this.frame.getContentPane();
//        JPanel c = new JPanel();
//        c.setLayout(null);
//        c.setBounds(300, 0, 300, 600);

        this.canvas = new WhiteBoard(canvas);
        this.modeSelect = new JComboBox<>(Mode.values());
        this.modeSelect.setEditable(false);

        this.scroller = new JScrollPane(this.canvas);
        this.scroller.setBackground(Color.LIGHT_GRAY);
        this.modeSelect.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == 1) {
                    ClientController.getClientController().changeMode((Mode) e.getItem());
                }
            }
        });

        this.modeSelect.setBounds(410, 10, 108, 25);
        panel.add(this.modeSelect);


        this.canvas.addMouseListener(ClientController.getClientController());
        this.canvas.addMouseMotionListener(ClientController.getClientController());
        this.canvas.addKeyListener(ClientController.getClientController());
        // Add the canvas and controls to the main GUI. Canvas above controls.
//        c.add(whiteBoardController);
        this.scroller.setBounds(180, 50, 720, 850);
        panel.add(scroller);
//        this.frame.getContentPane().add(c);
        this.panel.revalidate();
        this.panel.repaint();
        this.frame.getContentPane().revalidate();
        this.frame.getContentPane().repaint();
    }
}
