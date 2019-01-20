package hanze.nl.bsintegratie;

import hanze.nl.bussimulator.Bedrijven;
import hanze.nl.bussimulator.Bericht;
import hanze.nl.bussimulator.Bus;
import hanze.nl.bussimulator.ETA;
import hanze.nl.infobord.JSONBericht;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
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
import java.util.HashMap;
import java.util.Map;

public class Application implements MessageListener {

    private Session xmlSession;
    private boolean transacted = false;
    private MessageProducer replyProducer;
    private final Connection xmlConnection;
    private final MessageProducer topicProducer;
    private final Map<String, MessageProducer> producerMap;

    private final ObjectMapper mapper = new ObjectMapper();

    public Application() throws JMSException {
        ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_BROKER_URL);

        // Create queues for the loggers
        producerMap = new HashMap<>();

        QueueConnection queueConnection = connectionFactory.createQueueConnection();
        queueConnection.setClientID("intermediate-logger");
        QueueSession queueSession = queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);

        Queue arrivaloggerQueue = queueSession.createQueue("ARRIVALOGGER");
        MessageProducer arrivaLoggerProducer = queueSession.createProducer(arrivaloggerQueue);
        producerMap.put("ARRIVA", arrivaLoggerProducer);

        Queue flixbusloggerQueue = queueSession.createQueue("FLIXBUSLOGGER");
        MessageProducer flixbusLoggerProducer = queueSession.createProducer(flixbusloggerQueue);
        producerMap.put("FLIXBUS", flixbusLoggerProducer);

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

            // Log het bericht
            MessageProducer logProducer = producerMap.get(bericht.getBedrijf());
            if (logProducer != null) {
                System.out.println("Logging: " + textMessage.getText());
                logProducer.send(message);
            }

            // Transformeer het bericht naar iets bruikbaars voor de InfoBord klasse
            for (ETA eta : bericht.getETAs()) {
                JSONBericht jsonBericht = new JSONBericht(
                        bericht.getTijd(),
                        eta.getAankomsttijd(),
                        bericht.getLijnNaam(),
                        bericht.getBusID(),
                        bericht.getBedrijf(),
                        eta.getHalteNaam()
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
