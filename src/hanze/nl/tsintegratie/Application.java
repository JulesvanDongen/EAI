package hanze.nl.tsintegratie;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Application {

    public static void main(String[] args) {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.INFO);

        try {
            HttpServer httpServer = HttpServer.create(new InetSocketAddress(8081), 4);
            httpServer.createContext("/TijdServer", new GetHttpHandler());
            httpServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
