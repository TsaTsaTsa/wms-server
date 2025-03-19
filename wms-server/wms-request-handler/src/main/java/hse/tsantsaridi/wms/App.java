package hse.tsantsaridi.wms;

import java.util.logging.Level;
import java.util.logging.Logger;

import hse.tsantsaridi.wms.controller.http.HttpServer;

public class App {
    private static final Logger logger = Logger.getLogger(App.class.getName());

    public static void main(String[] args) {
        int port = args.length > 0
                ? Integer.parseInt(args[0]) : 8080;
        try {
            new HttpServer(port).run();
        } catch (InterruptedException e) {
            logger.log(Level.SEVERE, "Server interrupted while running on port " + port, e);
            Thread.currentThread().interrupt();
            System.exit(1);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "An unexpected error occurred", e);
            System.exit(1);
        }
    }
}