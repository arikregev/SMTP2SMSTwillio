package Main;

import Server.Configuration;
import Server.Server;
import Server.StopServer;

public class Main {
    public static void main(String[] args) {
        System.out.println("Twilio SMTP2SMS Server developed by Arik Regev.");
        if(args.length != 1) {
            System.out.println("Argument Error: please pass a valid path to config file!");
            return;
        }
        Configuration conf = new Configuration(args[0]);
        Server s = new Server(conf.readFile());
        new StopServer(conf.getNumericValue("StopServerPort"),s).start();
        s.start();
    }
}
