package WhiteBoardClient;

import RemoteInterface.IRemoteWhiteBoard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.rmi.RemoteException;

public class WhiteBoardController extends JPanel
        implements MouseListener,      // Listen for mouse clicks.
        MouseMotionListener, // Listen for mouse movements.
        KeyListener {
    private WhiteBoard canvas;
    private IRemoteWhiteBoard remoteWhiteBoard;
    //    private WhiteBoard canvas
    // Points for drawing lines.
    private Point lastPoint, firstPoint;
    private String username;

    public WhiteBoardController(WhiteBoard canvas, IRemoteWhiteBoard remoteWhiteBoard, String username){
        this.canvas = canvas;
        this.remoteWhiteBoard = remoteWhiteBoard;
        this.username = username;
//        try {
//            remoteShape.addMouseListener(this);
////            this.addMouseMotionListener(this);
//        }catch (RemoteException e){
//            e.printStackTrace();
//        }

        this.canvas.addMouseListener(this);
//        remoteShape.addKeyListener(this);
    }

    private void drawLine(Point nextPoint){
        if (lastPoint == null) {
            lastPoint = nextPoint;
            return;
        }

//        if (firstPoint == null) // Store the first point passed if none is held.
//            firstPoint = nextPoint;

//        WhiteboardMessage msg = new WhiteboardMessage(lastPoint, nextPoint,
//                colour, lineWeight);
        try{
            remoteWhiteBoard.drawLine(this.username, new MyPoint(lastPoint.x, lastPoint.y), new MyPoint(nextPoint.x, nextPoint.y));
        }catch (RemoteException e){
            e.printStackTrace();
        }
        lastPoint = canvas.drawLine(lastPoint, nextPoint, Color.orange, 1);

//        msg.addUniqueID();
//        Client.getInstance().broadCastMessage(msg);
//        Server.messages.put(msg.getUniqueID(), msg);
//        remoteShape.drawLine();
    }

    @Override
    public void mouseClicked(MouseEvent e) {

        canvas.requestFocusInWindow();
        Point newPoint = e.getPoint();
//        lastPoint = newPoint;
        drawLine(newPoint);
//        lastPoint = null;
        System.out.println("clicked !!");
    }

    @Override
    public void mousePressed(MouseEvent e) {
//        System.out.println("pressed");
    }

    @Override
    public void mouseReleased(MouseEvent e) {

//        System.out.println("released");
    }

    @Override
    public void mouseEntered(MouseEvent e) {

        System.out.println("entered");
    }

    @Override
    public void mouseExited(MouseEvent e) {

        System.out.println("exited");
    }

    @Override
    public void mouseDragged(MouseEvent e) {

//        System.out.println("dragged");
    }

    @Override
    public void mouseMoved(MouseEvent e) {
//        System.out.println("moved");
    }

    @Override
    public void keyTyped(KeyEvent e) {
//        System.out.println("key pressed");
    }

    @Override
    public void keyPressed(KeyEvent e) {

    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
