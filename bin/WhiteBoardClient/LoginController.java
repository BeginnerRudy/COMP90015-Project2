package WhiteBoardClient;

import RemoteInterface.IRemoteWhiteBoard;
import WhiteBoardServer.SerializableBufferedImage;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class LoginController extends UnicastRemoteObject implements IRemoteClient, MouseListener, MouseMotionListener {
    private static LoginController loginController;
    private String username;
    private boolean isManger = false;
    private MyPoint lastPoint, firstPoint;

    static {
        try {
            loginController = new LoginController();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private LoginFrame loginFrame;
    private WhiteBoardClientGUI whiteBoardClientGUI = new WhiteBoardClientGUI();
    ;
    private IRemoteWhiteBoard remoteWhiteBoard;

    protected LoginController() throws RemoteException {
    }

    public void changeMode(Mode mode) {
        this.whiteBoardClientGUI.canvas.setMode(mode);
        System.out.println(mode); // TODO debug
    }

    public static LoginController getLoginController() {
        return loginController;
    }

    public void saveAs(File out) {

        try {
            ImageIO.write(this.whiteBoardClientGUI.canvas.getWhiteBoard(), "png", out);

//            File outfile = new File("./data/Untitled" + System.currentTimeMillis() + ".png");
//            ImageIO.write(this.whiteBoardClientGUI.canvas.getWhiteBoard(), "png", outfile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void open(File canvas) {
        //TODO could the manger open or create more than one whiteboard at one time ?
        try {
            BufferedImage imageOnDisk = ImageIO.read(canvas);
            this.whiteBoardClientGUI.createCanvas(imageOnDisk, remoteWhiteBoard, this.username);
            this.remoteWhiteBoard.create(new SerializableBufferedImage(imageOnDisk));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void kick(String username) {
        try {

            this.remoteWhiteBoard.kick(username);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        if (this.whiteBoardClientGUI.canvas.getWhiteBoard() != null) {
            try {
//            BufferedImage bi = this.canvas.getWhiteBoard().getType();  // retrieve image
                File outfile = new File("./data/Untitled" + System.currentTimeMillis() + ".png");
                ImageIO.write(this.whiteBoardClientGUI.canvas.getWhiteBoard(), "png", outfile);

//            this.close(manager);
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

    public void quit() {
        try {
            this.remoteWhiteBoard.quit(this.username);
        } catch (RemoteException e) {
            e.printStackTrace();
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

    public void init(LoginFrame loginFrame, IRemoteWhiteBoard remoteWhiteBoard) {
        this.loginFrame = loginFrame;
        this.remoteWhiteBoard = remoteWhiteBoard;
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

                // get user lists from remote
//                System.out.println(this.remoteWhiteBoard.getUserList());

                // add it to the frame
//                ArrayList<String> users_info = this.remoteWhiteBoard.getUserList();
//                for (String user : users_info) {
//                    this.whiteBoardClientGUI.listModel.addElement(user);
//                }

                this.loginFrame.frame.dispose();
                this.whiteBoardClientGUI.frame.setVisible(true);
            }

//            else {
//                JOptionPane.showMessageDialog(null, "The Name has already been taken!");
//            }

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
//            int index = this.whiteBoardClientGUI.userList.getNextMatch(user, 0, Position.Bias.Forward);
//            if (index == -1) {
            this.whiteBoardClientGUI.listModel.addElement(user);
//            }
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

    public void createWhiteBoard() {
        try {
            SerializableBufferedImage canvas = new SerializableBufferedImage(300, 300);
            this.whiteBoardClientGUI.createCanvas(canvas.getWhiteBoard(), remoteWhiteBoard, this.username);
            this.remoteWhiteBoard.create(canvas);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
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

    @Override
    public void createCanvas(SerializableBufferedImage canvas) throws RemoteException {
        this.whiteBoardClientGUI.createCanvas(canvas.getWhiteBoard(), remoteWhiteBoard, this.username);
    }

    @Override
    public void draw(MyPoint start, MyPoint end, Mode mode) throws RemoteException {
        this.whiteBoardClientGUI.canvas.requestFocusInWindow();
//        this.whiteBoardClientGUI.canvas.drawLine(start, end, Color.BLACK, 1);
//        canvas.drawLine(lastPoint, nextPoint, Color.orange, 1);
        this.whiteBoardClientGUI.canvas.lastPoint = start;
        this.whiteBoardClientGUI.canvas.firstPoint = end;
        this.whiteBoardClientGUI.canvas.fixed = true;

        switch (mode) {
            case LINE:
                this.whiteBoardClientGUI.canvas.drawLine(this.whiteBoardClientGUI.canvas.lastPoint, this.whiteBoardClientGUI.canvas.firstPoint, Color.BLACK, 1);
                break;
            case RECTANGLE:
                this.whiteBoardClientGUI.canvas.drawRect(this.whiteBoardClientGUI.canvas.lastPoint, this.whiteBoardClientGUI.canvas.firstPoint);
                break;
            default:
                System.out.println("not support");
        }

        this.whiteBoardClientGUI.canvas.repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
//        System.out.println("clicked");
//        if (lastPoint == null) {
//            lastPoint = new MyPoint(e.getPoint());
//        } else {
//            this.lastPoint = null;
//        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
//        System.out.println("pressed");
        this.whiteBoardClientGUI.canvas.fixed = false;
        this.whiteBoardClientGUI.canvas.lastPoint = new MyPoint(e.getPoint());
//        this.lastPoint = new MyPoint(e.getPoint());
//        this.whiteBoardClientGUI.canvas.repaint();

    }

    @Override
    public void mouseReleased(MouseEvent e) {
//        System.out.println("released");
        try {
//            System.out.println(String.format("Start: (%d, %d)", this.whiteBoardClientGUI.canvas.lastPoint.x, this.whiteBoardClientGUI.canvas.lastPoint.y));
//            System.out.println(String.format("End: (%d, %d)", this.whiteBoardClientGUI.canvas.firstPoint.x, this.whiteBoardClientGUI.canvas.firstPoint.y));
            this.remoteWhiteBoard.drawLine(this.username, this.whiteBoardClientGUI.canvas.lastPoint,
                    this.whiteBoardClientGUI.canvas.firstPoint, this.whiteBoardClientGUI.canvas.getMode());
            this.draw(this.whiteBoardClientGUI.canvas.lastPoint, this.whiteBoardClientGUI.canvas.firstPoint, this.whiteBoardClientGUI.canvas.getMode());
        } catch (RemoteException ee) {
            ee.printStackTrace();
        }
//        this.whiteBoardClientGUI.canvas.fixed = true;
//        this.whiteBoardClientGUI.canvas.repaint();

//        this.whiteBoardClientGUI.canvas.firstPoint = new MyPoint(e.getPoint());
//        this.firstPoint = new MyPoint(e.getPoint());

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
//        System.out.println("dragged");
        this.whiteBoardClientGUI.canvas.firstPoint = new MyPoint(e.getPoint());
        this.whiteBoardClientGUI.canvas.repaint();
//        if (lastPoint == null) {
//            lastPoint = new MyPoint(e.getPoint());
//        } else {
//            try {
//                this.whiteBoardClientGUI.canvas.requestFocusInWindow();
//                MyPoint nextPoint = new MyPoint(e.getPoint());
//                this.drawLine(lastPoint, firstPoint);
//                this.remoteWhiteBoard.drawLine(this.username, lastPoint, nextPoint);
//                this.lastPoint = null;
//            } catch (RemoteException ee) {
//                ee.printStackTrace();
//            }
//        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
//        System.out.println("Mouved");
    }
}
