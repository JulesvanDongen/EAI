package hanze.nl.tsintegratie;

import javax.xml.ws.spi.http.HttpExchange;
import java.io.IOException;

public interface RequestHandler {
    public abstract String handleRequest() throws IOException;
}
