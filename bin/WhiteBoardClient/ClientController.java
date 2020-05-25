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
import java.util.ArrayList;

import static Utils.Util.WHITEBOARD_HEIGHT;
import static Utils.Util.WHITEBOARD_WIDTH;

public class ClientController extends UnicastRemoteObject implements IRemoteClient, MouseListener, MouseMotionListener, KeyListener {
    private static ClientController clientController;
    private String username;

    private boolean isManager = false;
    private boolean isClosed = true;

    private boolean isClosedByServer = false;

    private WhiteBoardLoginFrame whiteBoardLoginFrame;

    private WhiteBoardClientGUI whiteBoardClientGUI = new WhiteBoardClientGUI();

    private IRemoteWhiteBoard remoteWhiteBoard;

    static {
        try {
            clientController = new ClientController();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    protected ClientController() throws RemoteException {
    }

    public static ClientController getClientController() {
        return clientController;
    }

    public void init(WhiteBoardLoginFrame whiteBoardLoginFrame) {
        this.whiteBoardLoginFrame = whiteBoardLoginFrame;
    }


    /*========================================Advanced features========================================*/

    public boolean isClosedByServer() {
        return isClosedByServer;
    }
    public boolean isManager() {
        return isManager;
    }

    public void saveAs(File out) {
        if (this.whiteBoardClientGUI.canvas != null) {

            try {
                ImageIO.write(this.whiteBoardClientGUI.canvas.getWhiteBoard(), "png", out);
                JOptionPane.showMessageDialog(this.whiteBoardClientGUI.frame, "Successful!");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

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

    public void createWhiteBoard() {
        try {
            if (this.isClosedByServer) {
                JOptionPane.showMessageDialog(this.whiteBoardClientGUI.frame, "Invalid! The whiteboard has closed by server!");
            } else if (this.whiteBoardClientGUI.canvas == null) {
                SerializableBufferedImage canvas = new SerializableBufferedImage(WHITEBOARD_WIDTH, WHITEBOARD_HEIGHT);
                this.whiteBoardClientGUI.createCanvas(canvas.getWhiteBoard());
                this.isClosed = false;
                this.remoteWhiteBoard.create(canvas);
            } else {
                JOptionPane.showMessageDialog(this.whiteBoardClientGUI.frame, "Invalid! The whiteboard has already exists!");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void open(File canvas) {
        //TODO could the manger open or create more than one whiteboard at one time ?
        try {
            if (this.isClosedByServer) {
                JOptionPane.showMessageDialog(this.whiteBoardClientGUI.frame, "Invalid! The whiteboard has closed by server!");
            } else if (this.whiteBoardClientGUI.canvas == null) {
                BufferedImage imageOnDisk = ImageIO.read(canvas);
                this.whiteBoardClientGUI.createCanvas(imageOnDisk);
                this.isClosed = false;
                this.remoteWhiteBoard.create(new SerializableBufferedImage(imageOnDisk));
            } else {
                JOptionPane.showMessageDialog(this.whiteBoardClientGUI.frame, "Invalid! The whiteboard has already exists!");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        if (this.whiteBoardClientGUI.canvas != null) {
            try {
                File outfile = new File("./data/Untitled" + ".png");
                ImageIO.write(this.whiteBoardClientGUI.canvas.getWhiteBoard(), "png", outfile);
                JOptionPane.showMessageDialog(this.whiteBoardClientGUI.frame, "Saved as Untitled.png.");
            } catch (IOException ee) {
                // handle exception
                ee.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this.whiteBoardClientGUI.frame, "Please create a whiteboard first!");
        }
    }

    public void close() {
        try {
            if (this.isClosedByServer) {
                JOptionPane.showMessageDialog(this.whiteBoardClientGUI.frame, "Invalid! The whiteboard has closed by server! Please click quit to exit.");
            } else {
                this.remoteWhiteBoard.close(this.username, CloseType.MANAGER_CLOSE);
                this.closeGUI(CloseType.SELF_CLOSE);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    /*========================================WhiteBoard Management========================================*/
    public void quit() {
        try {
            if (isManager && !this.isClosedByServer) {
                this.close();
            } else {

                if (this.whiteBoardClientGUI.canvas == null || !this.isClosed && !this.isClosedByServer) {

                    this.remoteWhiteBoard.quit(this.username);
                    System.out.println("quit");
                }
                System.out.println(isClosed);
                System.out.println(isClosedByServer);
                this.closeGUI(CloseType.SELF_CLOSE);
                System.out.println("closed!!!!");
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void kick(String username) {
        if (this.isClosedByServer) {
            JOptionPane.showMessageDialog(this.whiteBoardClientGUI.frame, "Invalid! The whiteboard has closed by server!");
        } else if (!username.equals(this.username)) {
            try {
                this.remoteWhiteBoard.kick(username);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else {
            // Manager cannot kick itself
            JOptionPane.showMessageDialog(this.whiteBoardClientGUI.frame, "You cannot kick yourself!");
        }

    }

    public boolean join(String username, String host, int port) {
        try {
            // TODO host check
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
            }else {
                switch (res){
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
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this.whiteBoardLoginFrame.frame, "The host or port maybe not correct, cannot find the RMI in the registry!");
        }
        return false;
    }

    @Override
    public void say(String msg) throws RemoteException {
        System.out.println(msg);

    }

    @Override
    public void updateUserList(ArrayList<String> user_names) throws RemoteException {
        // add it to the frame
        ArrayList<String> users_info = this.remoteWhiteBoard.getUserList();
        for (String user : users_info) {
            this.whiteBoardClientGUI.listModel.addElement(user);
        }
    }

    @Override
    public void addUser(String username) throws RemoteException {
        this.whiteBoardClientGUI.listModel.addElement(username);
    }

    @Override
    public void removeUser(String username) throws RemoteException {
        this.whiteBoardClientGUI.listModel.removeElement(username);
    }

    @Override
    public void setToBeManager() throws RemoteException {
        this.isManager = true;
    }

    @Override
    public void closeGUI(CloseType closeType) throws RemoteException {
        if (closeType.equals(CloseType.KICKED)) {
            JOptionPane.showMessageDialog(this.whiteBoardClientGUI.frame, "You have been kicked out by the manager!");
            this.closing();
        } else if (closeType.equals(CloseType.MANAGER_CLOSE)) {
            JOptionPane.showMessageDialog(this.whiteBoardClientGUI.frame, "The manager has closed the whiteboard!");
            this.isClosed = true;
            // The whiteboard is closed, so no more action on the white board
            if (this.whiteBoardClientGUI.canvas != null){
                this.whiteBoardClientGUI.canvas.removeMouseListener(this);
                this.whiteBoardClientGUI.canvas.removeMouseMotionListener(this);
                this.whiteBoardClientGUI.canvas.removeKeyListener(this);
            }
        } else if (closeType.equals(CloseType.SERVER_CLOSE)) {
            this.isClosedByServer = true;
            JOptionPane.showMessageDialog(this.whiteBoardClientGUI.frame, "The SERVER has closed the whiteboard!");
            // The whiteboard is closed, so no more action on the white board
            if (this.whiteBoardClientGUI.canvas != null){
                this.whiteBoardClientGUI.canvas.removeMouseListener(this);
                this.whiteBoardClientGUI.canvas.removeMouseMotionListener(this);
                this.whiteBoardClientGUI.canvas.removeKeyListener(this);
            }
        } else if (closeType.equals(CloseType.SELF_CLOSE)) {
//            JOptionPane.showMessageDialog(this.whiteBoardLoginFrame.frame, "The port is not correct, cannot find the RMI in the registry!");
            this.closing();

        }

    }

    private void closing() {
        new Thread(() -> {
            System.out.print("Shutting down...");
            System.out.println("done");
            System.exit(0);
        }).start();
    }

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
    public void changeMode(Mode mode) {
        this.whiteBoardClientGUI.canvas.setMode(mode);
        System.out.println(mode); // TODO debug
    }

    @Override
    public void drawString(MyPoint start, Character c) throws RemoteException {
        this.whiteBoardClientGUI.canvas.setRemoteDraw(true);
        this.whiteBoardClientGUI.canvas.drawString(start, c);
    }

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
        System.out.println("Pressed");
        this.whiteBoardClientGUI.canvas.fixed = false;
        this.whiteBoardClientGUI.canvas.lastPoint = new MyPoint(e.getPoint());
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        System.out.println("dragged");
        this.whiteBoardClientGUI.canvas.firstPoint = new MyPoint(e.getPoint());
        if (this.whiteBoardClientGUI.canvas.getMode() != Mode.FREEHAND) {
            this.whiteBoardClientGUI.canvas.repaint();
        } else {
            try {
                this.remoteWhiteBoard.drawShape(this.username, this.whiteBoardClientGUI.canvas.lastPoint, this.whiteBoardClientGUI.canvas.firstPoint, this.whiteBoardClientGUI.canvas.getMode());
                this.whiteBoardClientGUI.canvas.drawLine(this.whiteBoardClientGUI.canvas.lastPoint, this.whiteBoardClientGUI.canvas.firstPoint, Color.BLACK, 1);
            } catch (RemoteException ee) {
                ee.printStackTrace();
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        System.out.println("released");
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
            ee.printStackTrace();
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        System.out.println("Clicked");
        if (this.whiteBoardClientGUI.canvas.getMode() == Mode.TEXT) {
            this.whiteBoardClientGUI.canvas.lastPoint = new MyPoint(e.getPoint());
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        System.out.println("typed");
        if (this.whiteBoardClientGUI.canvas.getMode().equals(Mode.TEXT)) {
            try {
                this.remoteWhiteBoard.drawString(this.username, this.whiteBoardClientGUI.canvas.lastPoint, e.getKeyChar());
                this.whiteBoardClientGUI.canvas.drawString(e.getKeyChar());
            } catch (RemoteException ee) {
                ee.printStackTrace();
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
