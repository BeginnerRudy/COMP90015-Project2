package WhiteBoardClient;

import Utils.Mode;
import Utils.MyPoint;
import Utils.Util;
import WhiteBoardClient.GUI.WhiteBoardLoginFrame;
import WhiteBoardClient.GUI.WhiteBoardClientGUI;
import WhiteBoardServer.IRemoteWhiteBoard;
import Utils.SerializableBufferedImage;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

import static Utils.Util.WHITEBOARD_HEIGHT;
import static Utils.Util.WHITEBOARD_WIDTH;

public class ClientController extends UnicastRemoteObject implements IRemoteClient, MouseListener, MouseMotionListener, KeyListener {
    private static ClientController clientController;
    private String username;
    private boolean isManger = false;
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

    public void init(WhiteBoardLoginFrame whiteBoardLoginFrame, IRemoteWhiteBoard remoteWhiteBoard) {
        this.whiteBoardLoginFrame = whiteBoardLoginFrame;
        this.remoteWhiteBoard = remoteWhiteBoard;
    }


    /*========================================Advanced features========================================*/
    public void saveAs(File out) {

        try {
            ImageIO.write(this.whiteBoardClientGUI.canvas.getWhiteBoard(), "png", out);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createCanvas(SerializableBufferedImage canvas) throws RemoteException {
        this.whiteBoardClientGUI.createCanvas(canvas.getWhiteBoard());
    }

    public void createWhiteBoard() {
        try {
            SerializableBufferedImage canvas = new SerializableBufferedImage(WHITEBOARD_WIDTH, WHITEBOARD_HEIGHT);
            this.whiteBoardClientGUI.createCanvas(canvas.getWhiteBoard());
            this.remoteWhiteBoard.create(canvas);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void open(File canvas) {
        //TODO could the manger open or create more than one whiteboard at one time ?
        try {
            BufferedImage imageOnDisk = ImageIO.read(canvas);
            this.whiteBoardClientGUI.createCanvas(imageOnDisk);
            this.remoteWhiteBoard.create(new SerializableBufferedImage(imageOnDisk));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        if (this.whiteBoardClientGUI.canvas.getWhiteBoard() != null) {
            try {
                File outfile = new File("./data/Untitled" + System.currentTimeMillis() + ".png");
                ImageIO.write(this.whiteBoardClientGUI.canvas.getWhiteBoard(), "png", outfile);

            } catch (IOException ee) {
                // handle exception
                ee.printStackTrace();
            }
        } else {
            try {
                this.say("No whiteboard to save, please create one first");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    public void close() {
        try {
            this.remoteWhiteBoard.close(this.username);
            this.closeGUI();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    /*========================================WhiteBoard Management========================================*/
    public void quit() {
        try {
            // TODO quit not functionally correct.
            this.remoteWhiteBoard.quit(this.username);
            this.closeGUI();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void kick(String username) {
        try {
            // Manager cannot kick itself
            this.remoteWhiteBoard.kick(username);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public boolean join(String username) {
        try {

            // communicate with the Remote White board
            // if could join
            if (this.remoteWhiteBoard.join(username, this)) {
                this.username = username;

                // show manager options
                if (isManger) {
                    this.whiteBoardClientGUI.closeWhiteBoardButton.setVisible(true);
                    this.whiteBoardClientGUI.kickButton.setVisible(true);
                    this.whiteBoardClientGUI.createBtn.setVisible(true);
                    this.whiteBoardClientGUI.mb.setVisible(true);
                }

                this.whiteBoardLoginFrame.frame.dispose();
                this.whiteBoardClientGUI.frame.setVisible(true);
            }

            return true;
        } catch (RemoteException e) {
            e.printStackTrace();
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
        this.isManger = true;
    }

    @Override
    public void closeGUI() throws RemoteException {

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
            } catch (RemoteException ee){
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