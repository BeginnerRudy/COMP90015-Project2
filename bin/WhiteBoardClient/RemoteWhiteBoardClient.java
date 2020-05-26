package WhiteBoardClient;

import WhiteBoardClient.GUI.WhiteBoardLoginFrame;

public class RemoteWhiteBoardClient {
    public static void main(String[] args) {
        try {
            WhiteBoardLoginFrame whiteBoardLoginFrame = new WhiteBoardLoginFrame();
            whiteBoardLoginFrame.frame.setVisible(true);

            ClientController.getClientController().init(whiteBoardLoginFrame);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


