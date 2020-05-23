package WhiteBoardServer;

import org.apache.commons.cli.*;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

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
            System.out.println("White Board server ready! ");
        } catch (ParseException e){
            formatter.printHelp("utility-name", options);
        } catch (NumberFormatException e){
            System.out.println("Please enter integer for port");
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
