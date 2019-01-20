package hanze.nl.bussimulator;

import com.sun.jndi.ldap.Ber;
import com.thoughtworks.xstream.XStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;

@XmlRootElement
public class Bericht {

	@XmlElement
	String lijnNaam;

	@XmlElement
	public
	String eindpunt;

	@XmlElement
	String bedrijf;

	@XmlElement
	String busID;

	@XmlElement
	int tijd;

	@XmlElement
	public
	ArrayList<ETA> ETAs;

	Bericht() {
		this.lijnNaam="undefined";
		this.bedrijf="undefined";
		this.eindpunt="undefined";
		this.busID="-1";
		this.tijd=-1;
		this.ETAs=new ArrayList<ETA>();
	}
	
	public Bericht(String lijnNaam, String bedrijf, String busID, int tijd){
		this.lijnNaam=lijnNaam;
		this.bedrijf=bedrijf;
		this.eindpunt="";
		this.busID=busID;
		this.tijd=tijd;
		this.ETAs=new ArrayList<ETA>();
	}

	public String toXML() throws JAXBException {
//		JAXBContext context = JAXBContext.newInstance(this.getClass());
//
//		Marshaller marshaller  = context.createMarshaller();
//
//		StringWriter sw = new StringWriter();
//		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
//		marshaller.marshal(this, sw);
//
//		return sw.toString();

		XStream xStream = new XStream();
		xStream.alias("Bericht", Bericht.class);
		xStream.alias("ETA", ETA.class);
		String s = xStream.toXML(this);
		return s;
	}

	public String getLijnNaam() {
		return lijnNaam;
	}

	public String getEindpunt() {
		return eindpunt;
	}

	public String getBedrijf() {
		return bedrijf;
	}

	public String getBusID() {
		return busID;
	}

	public int getTijd() {
		return tijd;
	}

	public ArrayList<ETA> getETAs() {
		return ETAs;
	}

	public static Bericht fromXML(String xml) throws JAXBException {
//		JAXBContext jaxbContext = JAXBContext.newInstance(Bericht.class);
//		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
//		StringReader stringReader = new StringReader(xml);
//		return (Bericht) unmarshaller.unmarshal(stringReader);

		XStream xStream = new XStream();
		xStream.alias("Bericht", Bericht.class);
		xStream.alias("ETA", ETA.class);
		return (Bericht) xStream.fromXML(xml);
	}
}
