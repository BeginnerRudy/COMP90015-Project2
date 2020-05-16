package WhiteBoardServer;

import RemoteInterface.IRemoteShape;
import WhiteBoardClient.IRemoteClient;
import WhiteBoardClient.WhiteBoard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseListener;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RemoteWhiteBoardShapeServant extends UnicastRemoteObject implements IRemoteShape {

    private SerializableBufferedImage canvas;
    private IRemoteClient remoteClient;

    protected RemoteWhiteBoardShapeServant() throws RemoteException {
        super();
        canvas = new SerializableBufferedImage(800, 800);
        Graphics2D g = (Graphics2D) this.canvas.getWhiteBoard().getGraphics();
        g.setColor(Color.BLACK);
        g.setStroke(new BasicStroke(2));
        g.drawLine(10, 10, 100, 100);
    }

    @Override
    public SerializableBufferedImage getCanvas(){
        return this.canvas;
    }

//    @Override
//    public void addMouseListener(MouseListener mouseListener) throws RemoteException {
//        this.canvas.addMouseListener(mouseListener);
//    }

    @Override
    public void sendRemoteClient(IRemoteClient remoteClient) throws RemoteException {
        this.remoteClient = remoteClient;
    }

//    @Override
//    public boolean requestFocusInWindow() throws RemoteException {
//        return this.canvas.requestFocusInWindow();
//    }

    @Override
    public void drawLine() throws RemoteException {
        Graphics2D g = (Graphics2D) this.canvas.getWhiteBoard().getGraphics();
        g.drawLine(10, 10, 1000, 1000);
        remoteClient.say();
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
