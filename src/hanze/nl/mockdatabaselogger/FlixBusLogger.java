package hanze.nl.mockdatabaselogger;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import javax.jms.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class FlixBusLogger implements MessageListener {

    private File logfile;

    public FlixBusLogger() throws IOException {
        logfile = File.createTempFile("flixbuslogs", ".tmp");

        System.out.println("Logging to file: " + logfile.getAbsolutePath());
    }

    public static void main(String[] args) {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.INFO);

        try {
            ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();
            QueueConnection queueConnection = activeMQConnectionFactory.createQueueConnection();
            queueConnection.start();

            QueueSession queueSession = queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue flixbusloggerQueue = queueSession.createQueue("FLIXBUSLOGGER");
            MessageConsumer consumer = queueSession.createConsumer(flixbusloggerQueue);
            consumer.setMessageListener(new FlixBusLogger());
        } catch (JMSException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;

                FileWriter fileWriter = new FileWriter(logfile, true);
                fileWriter.write("\n" + textMessage.getText());
                fileWriter.close();
            }
        } catch (JMSException | IOException e) {
            e.printStackTrace();
        }
    }
}
