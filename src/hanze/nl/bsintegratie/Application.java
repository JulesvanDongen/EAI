package hanze.nl.bsintegratie;

import hanze.nl.bussimulator.Bericht;
import hanze.nl.bussimulator.Bus;
import hanze.nl.bussimulator.ETA;
import hanze.nl.infobord.JSONBericht;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.broker.region.DestinationFactory;
import org.apache.activemq.broker.region.DestinationFactoryImpl;
import org.apache.activemq.camel.CamelDestination;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import javax.jms.*;
import javax.xml.bind.JAXBException;
import java.io.IOException;

public class Application implements MessageListener {

    private Session xmlSession;
    private boolean  transacted = false;
    private MessageProducer replyProducer;
    private final Connection xmlConnection;
    private final MessageProducer topicProducer;

    private final ObjectMapper mapper = new ObjectMapper();

    public Application() throws JMSException {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_BROKER_URL);

        // Set up topic connection for sending to topic
        TopicConnection topicConnection = connectionFactory.createTopicConnection();
        topicConnection.setClientID("intermediate");
        TopicSession topicSession = topicConnection.createTopicSession(false, Session.AUTO_ACKNOWLEDGE);

        Topic topic = topicSession.createTopic("busqueue-json");

        topicProducer = topicSession.createProducer(topic);

        // Set up message listening
        xmlConnection = connectionFactory.createConnection();
        xmlConnection.start();
        this.xmlSession = xmlConnection.createSession(this.transacted, Session.AUTO_ACKNOWLEDGE);
        Destination busQueue = this.xmlSession.createQueue(Bus.queueName);

        //Setup a message producer to respond to messages from clients, we will get the destination
        //to send to from the JMSReplyTo header field from a Message
        this.replyProducer = this.xmlSession.createProducer(null);
        this.replyProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

        //Set up a consumer to consume messages off of the admin queue
        MessageConsumer consumer = this.xmlSession.createConsumer(busQueue);
        consumer.setMessageListener(this);

    }

    public static void main(String[] args) {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.INFO);

        try {
            Application application = new Application();
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessage(Message message) {
        TextMessage textMessage = (TextMessage) message;
        try {
            Bericht bericht = Bericht.fromXML(textMessage.getText());

            for (ETA eta : bericht.getETAs()) {
                JSONBericht jsonBericht = new JSONBericht(
                        bericht.getTijd(),
                        eta.getAankomsttijd(),
                        bericht.getLijnNaam(),
                        bericht.getBusID(),
                        bericht.getBedrijf(),
                        bericht.getEindpunt()
                );
                String jsonString = mapper.writeValueAsString(jsonBericht);
                TextMessage jsonMessage = new ActiveMQTextMessage();
                jsonMessage.setText(jsonString);
                topicProducer.send(jsonMessage);
            }
        } catch (JMSException e) {
            e.printStackTrace();
        } catch (JAXBException e) {
            e.printStackTrace();
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
