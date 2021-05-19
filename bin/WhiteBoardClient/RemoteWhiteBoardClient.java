package WhiteBoardClient;

import WhiteBoardClient.GUI.WhiteBoardLoginFrame;

/**
 * This class executes the client program and it is the entry point of the client.
 */
public class RemoteWhiteBoardClient {
    public static void main(String[] args) {
        try {
            WhiteBoardLoginFrame whiteBoardLoginFrame = new WhiteBoardLoginFrame();
            whiteBoardLoginFrame.frame.setVisible(true);
            // start the client now.
            ClientController.getClientController().init(whiteBoardLoginFrame);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


