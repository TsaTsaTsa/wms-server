package hse.tsantsaridi.wms;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import hse.tsantsaridi.wms.controller.http.HttpServer;

public class App {
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    public static void main(String[] args) {
        int port = args.length > 0
                ? Integer.parseInt(args[0]) : 8080;
        try {
            logger.info("Server start on port {}.", port);
            new HttpServer(port).run();
        } catch (InterruptedException e) {
            logger.error("Server interrupted while running on port {}", port, e);
            Thread.currentThread().interrupt();
            System.exit(1);
        } catch (Exception e) {
            logger.error("An unexpected error occurred ", e);
            System.exit(1);
        }
    }
}