package hanze.nl.infobord;

import javax.jms.*;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;


//TODO	Implementeer de starter voor de messagelistener:
//		Zet de verbinding op met de messagebroker en start de listener met
//		de juiste selector.

public  class ListenerStarter implements Runnable, ExceptionListener {

    private Connection connection;
    private String selector;
    private QueueListener listener;

    public ListenerStarter(String selector) throws Exception {
        this.selector = selector;
    }

    @Override
    public void run() {
        System.out.println(selector);

        try {
            ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_BROKER_URL);
            connection = connectionFactory.createConnection();
            connection.start();
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

            Topic topic = session.createTopic(selector);
            MessageConsumer consumer = session.createConsumer(topic);

            listener = new QueueListener();
            consumer.setMessageListener(listener);


        } catch (JMSException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onException(JMSException e) {

    }

}