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
    private Point lastPoint, firstPoint;
    private String username;

    // Drawing mode selector.
    private final JComboBox<Mode> modeSelect;

    public WhiteBoardController(WhiteBoard canvas, IRemoteWhiteBoard remoteWhiteBoard, String username){
        this.canvas = canvas;
        this.remoteWhiteBoard = remoteWhiteBoard;
        this.username = username;
        this.canvas.addMouseListener(this);

        this.modeSelect = new JComboBox<>(Mode.values());
        this.modeSelect.setEditable(false);
        this.modeSelect.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("action!!!");
                modeSelect.showPopup();
            }
        });
        this.add(modeSelect);
    }

    private void drawLine(Point nextPoint){
        if (lastPoint == null) {
            lastPoint = nextPoint;
            return;
        }

        try{
            remoteWhiteBoard.drawLine(this.username, new MyPoint(lastPoint.x, lastPoint.y), new MyPoint(nextPoint.x, nextPoint.y), this.canvas.getMode());
        }catch (RemoteException e){
            e.printStackTrace();
        }
//        canvas.drawLine(lastPoint, nextPoint, Color.orange, 1);
    }

    @Override
    public void mouseClicked(MouseEvent e) {

        canvas.requestFocusInWindow();
        Point newPoint = e.getPoint();
        drawLine(newPoint);
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
