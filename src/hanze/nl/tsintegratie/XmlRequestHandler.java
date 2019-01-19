package hanze.nl.tsintegratie;

import com.thoughtworks.xstream.XStream;
import hanze.nl.tijdtools.Tijd;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import javax.xml.bind.JAXB;
import java.io.IOException;

public class XmlRequestHandler implements RequestHandler {

    public static final String RESPONSE_TYPE_NAME = "xml";
    private final TimeServerCommunicator communicator;

    public XmlRequestHandler(TimeServerCommunicator communicator) {
        this.communicator = communicator;
    }

    @Override
    public String handleRequest() throws IOException {
        String currentTime = communicator.getCurrentTime();
        Tijd tijd = new ObjectMapper().readValue(currentTime, Tijd.class);


        XStream xStream = new XStream();
        xStream.alias("Tijd", Tijd.class);
        String s = xStream.toXML(tijd);

        return s;
    }
}
