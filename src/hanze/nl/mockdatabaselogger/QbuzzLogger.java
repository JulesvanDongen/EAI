package hanze.nl.mockdatabaselogger;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import javax.jms.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class QbuzzLogger implements MessageListener {

    private final Connection connection;

    public QbuzzLogger() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:/home/jules/Downloads/EAIOpdracht1/qbuzz.db");
    }

    public static void main(String[] args) {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.INFO);

        try {
            ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();
            QueueConnection queueConnection = activeMQConnectionFactory.createQueueConnection();
            QueueSession queueSession = queueConnection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
            queueConnection.start();

            Queue qbuzzlogger = queueSession.createQueue("QBUZZLOGGER");
            MessageConsumer consumer = queueSession.createConsumer(qbuzzlogger);
            consumer.setMessageListener(new QbuzzLogger());

        } catch (JMSException | SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO qbuzzlog(logrecord) VALUES(?)");
                preparedStatement.setString(1, textMessage.getText());
                preparedStatement.executeUpdate();
            }
        } catch (JMSException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
