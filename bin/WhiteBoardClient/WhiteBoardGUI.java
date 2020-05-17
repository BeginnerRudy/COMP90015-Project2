//package WhiteBoardClient;
//
//import RemoteInterface.IRemoteShape;
//import WhiteBoardServer.RemoteWhiteBoardShapeServant;
//
//import javax.swing.*;
//import java.awt.*;
//import java.rmi.RemoteException;
//import javax.swing.SpringLayout;
//
//public class WhiteBoardGUI extends JFrame {
//
//    /** The layout manager for the components. */
////    private final SpringLayout layout;
//    WhiteBoard canvas;
//    WhiteBoardController whiteBoardController;
//    JScrollPane scroller;
//
//
//    public WhiteBoardGUI(IRemoteShape canvas) throws RemoteException {
////        // Set the look and feel to the system style if possible.
////        String sysFeel = UIManager.getSystemLookAndFeelClassName();
////        String crossFeel = UIManager.getSystemLookAndFeelClassName();
////        try {
////            UIManager.setLookAndFeel(sysFeel);
////        } catch (ClassNotFoundException
////                | InstantiationException
////                | IllegalAccessException
////                | UnsupportedLookAndFeelException ex) {
////            System.err.println("Couldn't load system look and feel. "
////                    + "Reverting to cross-platform.");
////            try {
////                UIManager.setLookAndFeel(crossFeel);
////            } catch (ClassNotFoundException
////                    | InstantiationException
////                    | IllegalAccessException
////                    | UnsupportedLookAndFeelException innerEx) {
////                System.err.println("Couldn't load cross-platform look and "
////                        + "feel.");
////            }
////        }
//
////        this.layout = new SpringLayout();
////        contentPane.setLayout(layout);
//
////        canvas = new WhiteBoard(800, 800);
////        canvas.getCanvas();
//
//
////        this.menu = new WhiteboardMenu(this);
////        this.setJMenuBar(this.menu);
////        this.controls = new WhiteboardControls(canvas);
//
//        Container contentPane = this.getContentPane();
//        this.canvas = new WhiteBoard(canvas.getCanvas().getWhiteBoard());
//        whiteBoardController = new WhiteBoardController(this.canvas);
//
//        this.scroller = new JScrollPane(this.canvas);
//        this.scroller.setBackground(Color.LIGHT_GRAY);
//
//        // Add the canvas and controls to the main GUI. Canvas above controls.
//        contentPane.add(whiteBoardController);
//        contentPane.add(scroller);
////        this.layout.putConstraint(SpringLayout.WEST, whiteBoardController, 5,
////                SpringLayout.WEST, contentPane);
////        this.layout.putConstraint(SpringLayout.SOUTH, whiteBoardController, -5,
////                SpringLayout.SOUTH, contentPane);
////        this.layout.putConstraint(SpringLayout.EAST, whiteBoardController, -5,
////                SpringLayout.EAST, contentPane);
//
////        this.layout.putConstraint(SpringLayout.NORTH, scroller, 5,
////                SpringLayout.NORTH, contentPane);
////        this.layout.putConstraint(SpringLayout.WEST, scroller, 5,
////                SpringLayout.WEST, contentPane);
////        this.layout.putConstraint(SpringLayout.SOUTH, scroller, -5,
////                SpringLayout.NORTH, whiteBoardController);
////        this.layout.putConstraint(SpringLayout.EAST, scroller, -5,
////                SpringLayout.EAST, contentPane);
//
//        // Set up the rest of the JFrame.
//        this.setContentPane(contentPane);
//        this.setTitle("Distributed Whiteboard");
//        this.setSize(828, 893);
//        this.setLocationByPlatform(true);
//        this.setResizable(true);
//        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        this.setVisible(true);
//    }
//
//
////    public static void main(String[] args) {
////        new WhiteBoardGUI();
////    }
//}
