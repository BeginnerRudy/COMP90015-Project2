package WhiteBoardServer;

import Utils.Util;
import org.apache.commons.cli.*;

import java.net.BindException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;

/**
 * This class is responsible for start the server
 */
public class RemoteWhiteBoardServer {
    public static void main(String[] args) {
        // parser
        Options options = new Options();

        Option port_ = new Option("p", "port", true, "The port for rmi registry");
        port_.setRequired(true);
        options.addOption(port_);


        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        try {
            CommandLine cmd = parser.parse(options, args);

            int port = Integer.parseInt(cmd.getOptionValue("port"));

            Registry registry = LocateRegistry.createRegistry(port);
            IRemoteWhiteBoard remoteWhiteBoard = new RemoteWhiteBoardServant();
            registry.bind("WhiteBoard", remoteWhiteBoard);
            Util.serverPrinter("Success", "White Board server ready! ");
//            System.out.println("White Board server ready! ");
        } catch (ParseException e){
            formatter.printHelp("utility-name", options);
        } catch (NumberFormatException e){
            Util.serverPrinter("Error", "Please enter integer for port");
//            System.out.println();
//            e.printStackTrace();
        } catch (ExportException e){
            Util.serverPrinter("Error", "Please choose another port since the port is already in use.");
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}
