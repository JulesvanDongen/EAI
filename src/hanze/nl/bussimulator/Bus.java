package hanze.nl.bussimulator;

import com.thoughtworks.xstream.XStream;
import hanze.nl.bussimulator.Halte.Positie;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import javax.xml.bind.JAXBException;

public class Bus{

	private Bedrijven bedrijf;
	private Lijnen lijn;
	private int halteNummer;
	private int totVolgendeHalte;
	private int richting;
	private boolean bijHalte;
	private String busID;
	public static final String queueName = "busqueue-xml";

	Bus(Lijnen lijn, Bedrijven bedrijf, int richting){
		this.lijn=lijn;
		this.bedrijf=bedrijf;
		this.richting=richting;
		this.halteNummer = -1;
		this.totVolgendeHalte = 0;
		this.bijHalte = false;
		this.busID = "Niet gestart";
	}
	
	public void setbusID(int starttijd){
		this.busID=starttijd+lijn.name()+ "R" + richting;
	}
	
	public void naarVolgendeHalte(){
		Positie volgendeHalte = lijn.getHalte(halteNummer+richting).getPositie();
		totVolgendeHalte = lijn.getHalte(halteNummer).afstand(volgendeHalte);
	}
	
	public boolean halteBereikt(){
		halteNummer+=richting;
		bijHalte=true;
		if ((halteNummer>=lijn.getLengte()-1) || (halteNummer == 0)) {
			System.out.printf("Bus %s heeft eindpunt (halte %s, richting %d) bereikt.%n", 
					lijn.name(), lijn.getHalte(halteNummer), lijn.getRichting(halteNummer));
			return true;
		}
		else {
			System.out.printf("Bus %s heeft halte %s, richting %d bereikt.%n", 
					lijn.name(), lijn.getHalte(halteNummer), lijn.getRichting(halteNummer));		
			naarVolgendeHalte();
		}		
		return false;
	}
	
	public void start() {
		halteNummer = (richting==1) ? 0 : lijn.getLengte()-1;
		System.out.printf("Bus %s is vertrokken van halte %s in richting %d.%n", 
				lijn.name(), lijn.getHalte(halteNummer), lijn.getRichting(halteNummer));		
		naarVolgendeHalte();
	}
	
	public boolean move(){
		boolean eindpuntBereikt = false;
		bijHalte=false;
		if (halteNummer == -1) {
			start();
		}
		else {
			totVolgendeHalte--;
			if (totVolgendeHalte==0){
				eindpuntBereikt=halteBereikt();
			}
		}
		return eindpuntBereikt;
	}
	
	public void sendETAs(int nu){
		int i=0;
		Bericht bericht = new Bericht(lijn.name(),bedrijf.name(),busID,nu);
		if (bijHalte) {
			ETA eta = new ETA(lijn.getHalte(halteNummer).name(),lijn.getRichting(halteNummer),0);
			bericht.ETAs.add(eta);
		}
		Positie eerstVolgende=lijn.getHalte(halteNummer+richting).getPositie();
		int tijdNaarHalte=totVolgendeHalte+nu;
		for (i = halteNummer+richting ; !(i>=lijn.getLengte()) && !(i < 0); i=i+richting ){
			tijdNaarHalte+= lijn.getHalte(i).afstand(eerstVolgende);
			ETA eta = new ETA(lijn.getHalte(i).name(), lijn.getRichting(i),tijdNaarHalte);
			System.out.println(bericht.lijnNaam + " naar halte" + eta.halteNaam + " t=" + tijdNaarHalte);
			bericht.ETAs.add(eta);
			eerstVolgende=lijn.getHalte(i).getPositie();
		}
		bericht.eindpunt=lijn.getHalte(i-richting).name();
		sendBericht(bericht);
	}
	
	public void sendLastETA(int nu){
		Bericht bericht = new Bericht(lijn.name(),bedrijf.name(),busID,nu);
		String eindpunt = lijn.getHalte(halteNummer).name();
		ETA eta = new ETA(eindpunt,lijn.getRichting(halteNummer),0);
		bericht.ETAs.add(eta);
		bericht.eindpunt = eindpunt;
		sendBericht(bericht);
	}

	public void sendBericht(Bericht bericht){
		try {
			String xml = bericht.toXML();

			ActiveMQConnectionFactory factory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_BROKER_URL);
			Connection connection = factory.createConnection();
			connection.start();

			Session session = connection.createSession(false,  Session.AUTO_ACKNOWLEDGE);
			Destination destination = session.createQueue(queueName);
			MessageProducer messageProducer = session.createProducer(destination);

			messageProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

			TextMessage message = session.createTextMessage(xml);

			messageProducer.send(message);

			connection.close();
		} catch (JMSException e) {
			System.err.println("Exception thrown:");
			System.err.println(e.getMessage());
			System.err.println(e.getClass());
		} catch (JAXBException e) {
			System.err.println("Exception thrown:");
			System.err.println(e.getMessage());
			System.err.println(e.getClass());
		}

	}
}
