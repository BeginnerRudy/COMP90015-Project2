package WhiteBoardClient;

import RemoteInterface.IRemoteWhiteBoard;
import WhiteBoardServer.SerializableBufferedImage;

import javax.swing.*;
import javax.swing.text.Position;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class LoginController extends UnicastRemoteObject implements IRemoteClient {
    private static LoginController loginController;
    private String username;
    private boolean isManger = false;

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

    public static LoginController getLoginController() {
        return loginController;
    }

    public void kick(String username) {
        try {

            this.remoteWhiteBoard.kick(username);
        }catch (RemoteException e){
            e.printStackTrace();
        }
    }

    public void save(){
        try {
            this.remoteWhiteBoard.save(username);
        }catch (RemoteException e){
            e.printStackTrace();
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

    public void createWhiteBoard(){
        try {
            this.whiteBoardClientGUI.createCanvas(this.remoteWhiteBoard.create().getWhiteBoard(), remoteWhiteBoard, this.username);
        }catch (RemoteException e){
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
    public void drawLine(MyPoint start, MyPoint end) throws RemoteException {
//        this.whiteBoardClientGUI.canvas.requestFocusInWindow();
        this.whiteBoardClientGUI.canvas.drawLine(new Point(start.x, start.y), new Point(end.x, end.y), Color.orange, 1);
//        canvas.drawLine(lastPoint, nextPoint, Color.orange, 1);
    }
}
