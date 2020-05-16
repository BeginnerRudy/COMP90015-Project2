package WhiteBoardServer;

import RemoteInterface.IRemoteShape;
import WhiteBoardClient.WhiteBoard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RemoteWhiteBoardShapeServant extends UnicastRemoteObject implements IRemoteShape {

    private WhiteBoard canvas;

    protected RemoteWhiteBoardShapeServant() throws RemoteException {
        super();
        canvas = new WhiteBoard(800, 800);
    }

    @Override
    public WhiteBoard getCanvas(){
        return this.canvas;
    }

    @Override
    public void addMouseListener(MouseListener mouseListener) throws RemoteException {
        this.canvas.addMouseListener(mouseListener);
    }

    @Override
    public boolean requestFocusInWindow() throws RemoteException {
        return this.canvas.requestFocusInWindow();
    }

    @Override
    public void drawLine() {
        this.canvas.drawLine();
//        g.drawLine(10, 10, 1000, 1000);
        // set default color and weight
//        Color col = Color.BLACK;
//        int weight = 1;

        // check and receive input parameters
//        if (colour != null){
//            col = colour;
//        }
//        if (size > 0){
//            weight = size;
//        }

        // setup the g for drawing
//        g.setColor(col);
//        g.setStroke(new BasicStroke(weight));
//
//        // Draw a line
//        synchronized (RemoteWhiteBoard.class){
//            g.drawLine(start.x, start.y, end.x, end.y);
//        }
//
//        // render the line
//        this.canvas.repaint();
//
//        // return the end point
//        return new SerializablePoint(end.x, end.y);
    }
}
