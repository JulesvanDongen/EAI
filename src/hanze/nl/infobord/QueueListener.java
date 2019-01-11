package hanze.nl.infobord;

import java.io.IOException;
import java.util.Enumeration;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;


//TODO 	implementeer de messagelistener die het bericht ophaald en
//		doorstuurd naar verwerkBericht van het infoBord.
//		Ook moet setRegels aangeroepen worden.

public class QueueListener implements MessageListener {

    private InfoBord bord;

    @Override
    public void onMessage(Message message) {
        try {
            if (message instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message;
                System.out.println(textMessage.getText());
                InfoBord.verwerkBericht(textMessage.getText());
                InfoBord.getInfoBord().setRegels();
            }
        } catch (JMSException e) {
            e.printStackTrace();
        }
    }
}

