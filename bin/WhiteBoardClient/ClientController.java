package WhiteBoardClient;

import Utils.*;
import WhiteBoardClient.GUI.WhiteBoardLoginFrame;
import WhiteBoardClient.GUI.WhiteBoardClientGUI;
import WhiteBoardServer.IRemoteWhiteBoard;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.List;

import static Utils.Util.*;

/**
 * This class is the major class who is responsible for the client input as well as interactive with the remote server.
 */
public class ClientController extends UnicastRemoteObject implements IRemoteClient, MouseListener, MouseMotionListener, KeyListener {
    private static ClientController clientController;
    // The user name of the current user
    private String username;

    // Whether the user is manager
    private boolean isManager = false;
    // Whether the whiteboard is closed.
    private boolean isClosed = true;

    // Whether the whiteboard is closed by the server
    private boolean isClosedByServer = false;

    // Thu login GUI
    private WhiteBoardLoginFrame whiteBoardLoginFrame;

    // The main control GUI
    private WhiteBoardClientGUI whiteBoardClientGUI = new WhiteBoardClientGUI();

    // The remote interface of the server
    private IRemoteWhiteBoard remoteWhiteBoard;

    static {
        try {
            clientController = new ClientController();
        } catch (RemoteException e) {
//            e.printStackTrace();
            clientPrinter("Error", "Failed to create client");
        }
    }

    protected ClientController() throws RemoteException {
    }

    public static ClientController getClientController() {
        return clientController;
    }


    /**
     * This method would initialize the ClientController
     * @param whiteBoardLoginFrame The login GUI
     */
    public void init(WhiteBoardLoginFrame whiteBoardLoginFrame) {
        this.whiteBoardLoginFrame = whiteBoardLoginFrame;
    }


    /*========================================Advanced features========================================*/

    /**
     * @return True for closed by server, false for not
     */
    public boolean isClosedByServer() {
        return isClosedByServer;
    }

    /**
     * @return True for is manager and false for not
     */
    public boolean isManager() {
        return isManager;
    }

    /**
     * This method would save the file with provided name
     *
     * @param out The file to save
     */
    public void saveAs(File out) {
        if (this.whiteBoardClientGUI.canvas != null) {

            try {
                ImageIO.write(this.whiteBoardClientGUI.canvas.getWhiteBoard(), "png", out);
                JOptionPane.showMessageDialog(this.whiteBoardClientGUI.frame, "Successful!");
                clientPrinter("Success", "Save as successfully");
            } catch (IOException e) {
//                e.printStackTrace();
                clientPrinter("Error", "Failed to save as");
                JOptionPane.showMessageDialog(this.whiteBoardClientGUI.frame, "Fail to save as");
            }
        }
    }

    /**
     * This method method is used to created the whiteboard for the client by the given canvas parameter.
     *
     * @param canvas This is the whiteboard object the server would send
     * @throws RemoteException
     */
    @Override
    public void createCanvas(SerializableBufferedImage canvas) throws RemoteException {
        if (this.isClosedByServer) {
            JOptionPane.showMessageDialog(this.whiteBoardClientGUI.frame, "Invalid! The whiteboard has closed by server!");
        } else if (this.whiteBoardClientGUI.canvas == null) {
            this.isClosed = false;
            this.whiteBoardClientGUI.createCanvas(canvas.getWhiteBoard());
        } else {
            JOptionPane.showMessageDialog(this.whiteBoardClientGUI.frame, "Invalid! The whiteboard has already exists!");
        }
    }


    /**
     * This method is responsible for create a new blank whiteboard
     */
    public void createWhiteBoard() {
        try {
            if (this.isClosedByServer) {
                JOptionPane.showMessageDialog(this.whiteBoardClientGUI.frame, "Invalid! The whiteboard has closed by server!");
            } else if (this.whiteBoardClientGUI.canvas == null) {
                SerializableBufferedImage canvas = new SerializableBufferedImage(WHITEBOARD_WIDTH, WHITEBOARD_HEIGHT);
                this.remoteWhiteBoard.create(canvas);
                this.whiteBoardClientGUI.createCanvas(canvas.getWhiteBoard());
                this.isClosed = false;
            } else {
                JOptionPane.showMessageDialog(this.whiteBoardClientGUI.frame, "Invalid! The whiteboard has already exists!");
            }
        } catch (RemoteException e) {
//            e.printStackTrace();
            clientPrinter("Error", "Failed to create canvas on other clients");
            JOptionPane.showMessageDialog(this.whiteBoardClientGUI.frame, "The server might be down. Failed to create.");
        }
    }

    /**
     * This method would open a canvas depends on the given canvas File.
     *
     * @param canvas The file to open
     */
    public void open(File canvas) {
        try {
            if (this.isClosedByServer) {
                JOptionPane.showMessageDialog(this.whiteBoardClientGUI.frame, "Invalid! The whiteboard has closed by server!");
            } else if (this.whiteBoardClientGUI.canvas == null) {
                BufferedImage imageOnDisk = ImageIO.read(canvas);
                this.remoteWhiteBoard.create(new SerializableBufferedImage(imageOnDisk));
                this.whiteBoardClientGUI.createCanvas(imageOnDisk);
                this.isClosed = false;
            } else {
                JOptionPane.showMessageDialog(this.whiteBoardClientGUI.frame, "Invalid! The whiteboard has already exists!");
            }

        } catch (IOException e) {
//            e.printStackTrace();
            clientPrinter("Error", "Failed to open the canvas");
            JOptionPane.showMessageDialog(this.whiteBoardClientGUI.frame, "The server might be down. Failed to open.");
        }
    }

    /**
     * This mehtod would save the whiteboard
     */
    public void save() {
        if (this.whiteBoardClientGUI.canvas != null) {
            try {
                String PATH = "./data";

                File directory = new File(PATH);
                if (! directory.exists()){
                    directory.mkdir();
                }

                File outfile = new File("./data/Untitled" + ".png");
                ImageIO.write(this.whiteBoardClientGUI.canvas.getWhiteBoard(), "png", outfile);
                JOptionPane.showMessageDialog(this.whiteBoardClientGUI.frame, "Saved as Untitled.png.");
            } catch (IOException ee) {
                // handle exception
//                ee.printStackTrace();
                clientPrinter("Error", "Failed to save the canvas.");
                JOptionPane.showMessageDialog(this.whiteBoardClientGUI.frame, "Fail to save");
            }
        } else {
            JOptionPane.showMessageDialog(this.whiteBoardClientGUI.frame, "Please create a whiteboard first!");
        }
    }


    /**
     * This method would close the application
     */
    public void close() {
        try {
            if (this.isClosedByServer) {
                JOptionPane.showMessageDialog(this.whiteBoardClientGUI.frame, "Invalid! The whiteboard has closed by server! Please click quit to exit.");
            } else {
                this.remoteWhiteBoard.close(this.username, CloseType.MANAGER_CLOSE);
                this.closeGUI(CloseType.SELF_CLOSE);
            }
        } catch (RemoteException e) {
//            e.printStackTrace();
            JOptionPane.showMessageDialog(this.whiteBoardClientGUI.frame, "Fail to close, the remote server might crashed.");
            clientPrinter("Error", "Failed to close canvas on other clients");
            JOptionPane.showMessageDialog(this.whiteBoardClientGUI.frame, "The app would be closed directly.");
            System.exit(1);
        }
    }


    /*========================================WhiteBoard Management========================================*/
    /**
     * This method would quit the user from the current whiteboard
     */
    public void quit() {
        try {
            if (isManager && !this.isClosedByServer) {
                this.close();
            } else {

                if (this.whiteBoardClientGUI.canvas == null || !this.isClosed && !this.isClosedByServer) {

                    this.remoteWhiteBoard.quit(this.username);
//                    System.out.println("quit");
                }
//                System.out.println(isClosed);
//                System.out.println(isClosedByServer);
                this.closeGUI(CloseType.SELF_CLOSE);
//                System.out.println("closed!!!!");
            }
        } catch (RemoteException e) {
//            e.printStackTrace();
            clientPrinter("Error", "Failed to quit the canvas remotely");
            JOptionPane.showMessageDialog(this.whiteBoardClientGUI.frame, "Fail to quit, the remote server might crashed.");
            JOptionPane.showMessageDialog(this.whiteBoardClientGUI.frame, "The app would be closed directly.");
            System.exit(1);
        }
    }

    /**
     * This method would delegate this client to be manager of the system.
     * @throws RemoteException
     */
    @Override
    public void becomeManager() throws RemoteException {
        this.isManager = true;
//        System.out.println("be come manager");
        this.whiteBoardClientGUI.closeWhiteBoardButton.setVisible(true);
        this.whiteBoardClientGUI.kickButton.setVisible(true);
        this.whiteBoardClientGUI.createBtn.setVisible(true);
        this.whiteBoardClientGUI.mb.setVisible(true);
        JOptionPane.showMessageDialog(this.whiteBoardClientGUI.frame, "You are the manager now!");

        this.whiteBoardLoginFrame.frame.dispose();
        this.whiteBoardClientGUI.frame.setVisible(true);
        this.whiteBoardClientGUI.frame.revalidate();
        this.whiteBoardClientGUI.frame.validate();
    }


    /**
     * This method would ask the remote server to close the whiteboard
     *
     * @param username The username of user to be kciked out of the whitaboard
     */
    public void kick(String username) {
        if (this.isClosedByServer) {
            JOptionPane.showMessageDialog(this.whiteBoardClientGUI.frame, "Invalid! The whiteboard has closed by server!");
        } else if (username == null) {
            JOptionPane.showMessageDialog(this.whiteBoardClientGUI.frame, "Please select a user first!!");
        } else if (!username.equals(this.username)) {
            try {
                this.remoteWhiteBoard.kick(username);
            } catch (RemoteException e) {
//                e.printStackTrace();
                clientPrinter("Error", "Failed to kick the user");
                JOptionPane.showMessageDialog(this.whiteBoardClientGUI.frame, "Failed to kick the user, the remote server might crashed.");

            }
        } else {
            // Manager cannot kick itself
            try {
//                for (Iterator<Enumeration<String>> it = Arrays.asList(this.whiteBoardClientGUI.listModel.elements()).iterator(); it.hasNext(); ) {
//                    Enumeration<String> user = it.next();
//                    users.addElement(user);
//                }
//                this.whiteBoardClientGUI.listModel.removeElement(this.username);
//                SelectFrame selectFrame = new SelectFrame((DefaultListModel) this.whiteBoardClientGUI.userList.getModel());
//                JPopupMenu popup = new JPopupMenu();
//                popup.add(this.whiteBoardClientGUI.userList);
////                popup.show();
//                popup.show(this.whiteBoardClientGUI.frame, 60, 60);

                DefaultListModel listModel_2 = new DefaultListModel();
                List<Object> users = Arrays.asList(this.whiteBoardClientGUI.listModel.toArray());
                for (Object user : users) {
                    if (!user.equals(this.username))
                        listModel_2.addElement((String) user);
                }

                if (listModel_2.size() == 0) {
                    JOptionPane.showMessageDialog(this.whiteBoardClientGUI.frame, "Please click quit or close, since only you are in the room!");
                }else {
                    JList list = new JList(listModel_2);
                    JOptionPane.showMessageDialog(
                            this.whiteBoardClientGUI.frame, list, "Select New Manager", JOptionPane.PLAIN_MESSAGE);
                    String newManager = (String) list.getSelectedValue();
                    if (newManager != null){
                        if (remoteWhiteBoard.transfer(newManager )) {
                            this.isManager = false;
                            this.whiteBoardClientGUI.closeWhiteBoardButton.setVisible(false);
                            this.whiteBoardClientGUI.kickButton.setVisible(false);
                            this.whiteBoardClientGUI.createBtn.setVisible(false);
                            this.whiteBoardClientGUI.mb.setVisible(false);
                            JOptionPane.showMessageDialog(this.whiteBoardClientGUI.frame, "You are not the manager now!");

                            this.whiteBoardLoginFrame.frame.dispose();
                            this.whiteBoardClientGUI.frame.setVisible(true);
                            this.whiteBoardClientGUI.frame.revalidate();
                            this.whiteBoardClientGUI.frame.validate();
                        } else {
                            // deal with concurrency.
                            JOptionPane.showMessageDialog(this.whiteBoardClientGUI.frame, "The user is not in the room!");
                        }
                    }
//                    System.out.println(newManager);
                }


            } catch (Exception e) {
//                e.printStackTrace();
                clientPrinter("Error", "Failed to transfer the manager to another user.");
                JOptionPane.showMessageDialog(this.whiteBoardClientGUI.frame, "The server might be crashed. Failed to transfer the manager to another user.");
            }
        }

    }


    /**
     * This method would try to join the user to the whiteboard by it given host, port and user name
     * @param username The username provided by the user
     * @param host The host ip
     * @param port The port
     * @return
     */
    public boolean join(String username, String host, int port) {
        try {
            Registry registry = LocateRegistry.getRegistry(host, port);
            IRemoteWhiteBoard remoteWhiteBoard = (IRemoteWhiteBoard) registry.lookup("WhiteBoard");
            this.remoteWhiteBoard = remoteWhiteBoard;
            // communicate with the Remote White board
            // if could join
            MessageType res = this.remoteWhiteBoard.join(username, this);
            if (res.equals(MessageType.SUCCESS_JOIN)) {
                this.username = username;

                this.whiteBoardLoginFrame.frame.dispose();
                this.whiteBoardClientGUI.frame.setVisible(true);
                // show manager options
                if (isManager) {
                    this.whiteBoardClientGUI.closeWhiteBoardButton.setVisible(true);
                    this.whiteBoardClientGUI.kickButton.setVisible(true);
                    this.whiteBoardClientGUI.createBtn.setVisible(true);
                    this.whiteBoardClientGUI.mb.setVisible(true);
                    JOptionPane.showMessageDialog(this.whiteBoardClientGUI.frame, "You are the manager now!");
                }

                return true;
            } else {
                switch (res) {
                    case DUPLICATE_NAME:
                        JOptionPane.showMessageDialog(this.whiteBoardLoginFrame.frame, "The user name has already been taken!");
                        break;
                    case REFUSED_JOIN:
                        JOptionPane.showMessageDialog(this.whiteBoardLoginFrame.frame, "The manager are not allow you to join!");
                        break;
                    default:
                        System.out.println("not support");
                }
            }
        } catch (RemoteException e) {
            JOptionPane.showMessageDialog(this.whiteBoardLoginFrame.frame, "The host or port maybe not correct, cannot find the RMI in the registry!");
            clientPrinter("Error", "The host or port maybe not correct, cannot find the RMI in the registry!");
//            e.printStackTrace();
        } catch (NotBoundException e) {
//            e.printStackTrace();
            JOptionPane.showMessageDialog(this.whiteBoardLoginFrame.frame, "Cannot find the RMI in the registry! Name may not bind!");
            clientPrinter("Error", "Cannot find the RMI in the registry! Name may not bind!");

        }
        return false;
    }

    /**
     * This method is used for the server to print string on the server side
     *
     * @param msg The msg sent to the client
     * @throws RemoteException
     */
    @Override
    public void say(String msg) throws RemoteException {
        System.out.println(msg);

    }

    /**
     * This method is responsible for update the user list with all current users.
     * @param user_names The usernames of all current users
     * @throws RemoteException
     */
    @Override
    public void updateUserList(ArrayList<String> user_names) throws RemoteException {
        // add it to the frame
        ArrayList<String> users_info = this.remoteWhiteBoard.getUserList();
        for (String user : users_info) {
            this.whiteBoardClientGUI.listModel.addElement(user);
        }
    }

    /**
     * This method is responsible for add new user to the user list on the client side
     * @param username The username of the new user
     * @throws RemoteException
     */
    @Override
    public void addUser(String username) throws RemoteException {
        this.whiteBoardClientGUI.listModel.addElement(username);
        this.whiteBoardClientGUI.frame.revalidate();
        this.whiteBoardClientGUI.frame.revalidate();
    }

    /**
     * This method is responsible for remove the given username
     * @param username The username of the user who left the whiteboard
     * @throws RemoteException
     */
    @Override
    public void removeUser(String username) throws RemoteException {
        this.whiteBoardClientGUI.listModel.removeElement(username);
        this.whiteBoardClientGUI.frame.revalidate();
        this.whiteBoardClientGUI.frame.revalidate();
    }

    /**
     * This method set the user to be the manager by set its isManager fields to be true.
     *
     * @throws RemoteException
     */
    @Override
    public void setToBeManager() throws RemoteException {
        this.isManager = true;
    }

    /**
     * This method is used to close the whiteboard
     * @param closeType This indicates the close type of the close operation
     * @throws RemoteException
     */
    @Override
    public void closeGUI(CloseType closeType) throws RemoteException {
        if (closeType.equals(CloseType.KICKED)) {
            JOptionPane.showMessageDialog(this.whiteBoardClientGUI.frame, "You have been kicked out by the manager!");
            this.closing();
        } else if (closeType.equals(CloseType.MANAGER_CLOSE)) {
            JOptionPane.showMessageDialog(this.whiteBoardClientGUI.frame, "The manager has closed the whiteboard!");
            this.isClosed = true;
            // The whiteboard is closed, so no more action on the white board
            if (this.whiteBoardClientGUI.canvas != null) {
                this.whiteBoardClientGUI.canvas.removeMouseListener(this);
                this.whiteBoardClientGUI.canvas.removeMouseMotionListener(this);
                this.whiteBoardClientGUI.canvas.removeKeyListener(this);
            }
        } else if (closeType.equals(CloseType.SERVER_CLOSE)) {
            this.isClosedByServer = true;
            JOptionPane.showMessageDialog(this.whiteBoardClientGUI.frame, "The SERVER has closed the whiteboard!");
            // The whiteboard is closed, so no more action on the white board
            if (this.whiteBoardClientGUI.canvas != null) {
                this.whiteBoardClientGUI.canvas.removeMouseListener(this);
                this.whiteBoardClientGUI.canvas.removeMouseMotionListener(this);
                this.whiteBoardClientGUI.canvas.removeKeyListener(this);
            }
        } else if (closeType.equals(CloseType.SELF_CLOSE)) {
//            JOptionPane.showMessageDialog(this.whiteBoardLoginFrame.frame, "The port is not correct, cannot find the RMI in the registry!");
            this.closing();

        }

    }

    /**
     * This method would shut down the application
     */
    private void closing() {
        new Thread(() -> {
            System.out.print("Shutting down...");
            System.out.println("done");
            System.exit(0);
        }).start();
    }

    /**
     * This method is used to ask the manager whether allow the user with given username to join.
     *
     * @param username The username of the user who wants to join in.
     * @return true indicates the manager allows the join and false means join been refused
     * @throws RemoteException
     */
    @Override
    public boolean allowJoins(String username) throws RemoteException {
        int result = JOptionPane.showConfirmDialog(this.whiteBoardClientGUI.frame, username, "New user join request",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);
        if (result == JOptionPane.YES_OPTION) {
            return true;
        }
        return false;
    }


    /*========================================WhiteBoard Drawing========================================*/

    /**
     * This method would change the drawing mode of the whiteboard
     *
     * @param mode The mode to change to
     */
    public void changeMode(Mode mode) {
        this.whiteBoardClientGUI.canvas.setMode(mode);
//        System.out.println(mode);
    }

    /**
     * This method is used to draw character on to the remote client whiteboard on the given start point
     *
     * @param start The point for the char to start
     * @param c The char to draw
     * @throws RemoteException
     */
    @Override
    public void drawString(MyPoint start, Character c) throws RemoteException {
        this.whiteBoardClientGUI.canvas.setRemoteDraw(true);
        this.whiteBoardClientGUI.canvas.drawString(start, c);
    }

    /**
     * This method is used to draw shape on the remote client
     * @param start The start point of the shape
     * @param end The end point of the shape
     * @param mode The drawing mode
     * @throws RemoteException
     */
    @Override
    public void drawShape(MyPoint start, MyPoint end, Mode mode) throws RemoteException {
        this.whiteBoardClientGUI.canvas.requestFocusInWindow();
        this.whiteBoardClientGUI.canvas.lastPoint = start;
        this.whiteBoardClientGUI.canvas.firstPoint = end;
        this.whiteBoardClientGUI.canvas.setRemoteDraw(true);
        this.whiteBoardClientGUI.canvas.fixed = true;

        switch (mode) {
            case LINE:
            case FREEHAND:
                this.whiteBoardClientGUI.canvas.drawLine(this.whiteBoardClientGUI.canvas.lastPoint, this.whiteBoardClientGUI.canvas.firstPoint, Color.BLACK, 1);
                break;
            case RECTANGLE:
                this.whiteBoardClientGUI.canvas.drawRect(this.whiteBoardClientGUI.canvas.lastPoint, this.whiteBoardClientGUI.canvas.firstPoint);
                break;
            case CIRCLE:
                this.whiteBoardClientGUI.canvas.drawCircle(this.whiteBoardClientGUI.canvas.lastPoint, this.whiteBoardClientGUI.canvas.firstPoint);
                break;
            default:
                System.out.println("not support");
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
//        System.out.println("Pressed");
        this.whiteBoardClientGUI.canvas.fixed = false;
        this.whiteBoardClientGUI.canvas.lastPoint = new MyPoint(e.getPoint());
    }

    @Override
    public void mouseDragged(MouseEvent e) {
//        System.out.println("dragged");
        this.whiteBoardClientGUI.canvas.firstPoint = new MyPoint(e.getPoint());
        if (this.whiteBoardClientGUI.canvas.getMode() != Mode.FREEHAND) {
            this.whiteBoardClientGUI.canvas.repaint();
        } else {
            try {
                this.remoteWhiteBoard.drawShape(this.username, this.whiteBoardClientGUI.canvas.lastPoint, this.whiteBoardClientGUI.canvas.firstPoint, this.whiteBoardClientGUI.canvas.getMode());
                this.whiteBoardClientGUI.canvas.drawLine(this.whiteBoardClientGUI.canvas.lastPoint, this.whiteBoardClientGUI.canvas.firstPoint, Color.BLACK, 1);
            } catch (RemoteException ee) {
//                ee.printStackTrace();
                JOptionPane.showMessageDialog(this.whiteBoardClientGUI.frame, "The server might be down. Failed to draw.");
                clientPrinter("Error", "Failed to draw remotely!!!");
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
//        System.out.println("released");
        try {
            if (this.whiteBoardClientGUI.canvas.getMode() != Mode.FREEHAND) {
                this.whiteBoardClientGUI.canvas.firstPoint = new MyPoint(e.getPoint());
                this.remoteWhiteBoard.drawShape(this.username, this.whiteBoardClientGUI.canvas.lastPoint,
                        this.whiteBoardClientGUI.canvas.firstPoint, this.whiteBoardClientGUI.canvas.getMode());
//                this.draw(this.whiteBoardClientGUI.canvas.lastPoint, this.whiteBoardClientGUI.canvas.firstPoint, this.whiteBoardClientGUI.canvas.getMode());

                this.whiteBoardClientGUI.canvas.requestFocusInWindow();
                this.whiteBoardClientGUI.canvas.fixed = true;
                this.whiteBoardClientGUI.canvas.repaint();
            }
        } catch (RemoteException ee) {
//            ee.printStackTrace();
            JOptionPane.showMessageDialog(this.whiteBoardClientGUI.frame, "The server might be down. Failed to draw.");
            clientPrinter("Error", "Failed to draw remotely!!!");
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
//        System.out.println("Clicked");
        if (this.whiteBoardClientGUI.canvas.getMode() == Mode.TEXT) {
            this.whiteBoardClientGUI.canvas.lastPoint = new MyPoint(e.getPoint());
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
//        System.out.println("typed");
        if (this.whiteBoardClientGUI.canvas.getMode().equals(Mode.TEXT)) {
            try {
                this.remoteWhiteBoard.drawString(this.username, this.whiteBoardClientGUI.canvas.lastPoint, e.getKeyChar());
                this.whiteBoardClientGUI.canvas.drawString(e.getKeyChar());
            } catch (RemoteException ee) {
//                ee.printStackTrace();
                JOptionPane.showMessageDialog(this.whiteBoardClientGUI.frame, "The server might be down. Failed to draw.");
                clientPrinter("Error", "Failed to input text remotely!!!");
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
