package hanze.nl.tsintegratie;

import javax.xml.ws.spi.http.HttpExchange;
import java.io.IOException;

public class JsonRequestHandler implements RequestHandler {

    public static final String RESPONSE_TYPE_NAME = "json";

    private final TimeServerCommunicator communicator;

    public JsonRequestHandler(TimeServerCommunicator communicator) {
        this.communicator = communicator;
    }

    @Override
    public String handleRequest() {
        try {
            return communicator.getCurrentTime();
        } catch (IOException e) {
            e.printStackTrace();
            return "json";
        }
    }
}
