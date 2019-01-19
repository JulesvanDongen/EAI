package hanze.nl.tsintegratie;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;

public class GetHttpHandler implements HttpHandler {

    private HashMap<String, RequestHandler> handlers;

    public GetHttpHandler() throws IOException {
        TimeServerCommunicator timeServerCommunicator = new TimeServerCommunicator();

        handlers = new HashMap<>();
        handlers.put(JsonRequestHandler.RESPONSE_TYPE_NAME, new JsonRequestHandler(timeServerCommunicator));
        handlers.put(XmlRequestHandler.RESPONSE_TYPE_NAME, new XmlRequestHandler(timeServerCommunicator));
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {

        System.out.println(httpExchange.getRequestMethod());
        String[] split = httpExchange.getRequestURI().getQuery().split("=");

        if (split[0].equals("responseType")) {
            RequestHandler requestHandler = handlers.get(split[1]);
            String s = requestHandler.handleRequest();

            System.out.println(s);

//            httpExchange.getResponseHeaders().add("content-type", "application/json");
            httpExchange.sendResponseHeaders(200, s.getBytes().length);
            httpExchange.getResponseBody().write(s.getBytes());
        }

        httpExchange.getRequestBody().close();
        httpExchange.getResponseBody().close();
    }
}
