package hanze.nl.bussimulator;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ETA {
	@XmlElement
	String halteNaam;

	@XmlElement
	int richting;

	@XmlElement
	int aankomsttijd;

	ETA() {
		halteNaam="undefined";
		richting=0;
		aankomsttijd=0;
	}

	public String getHalteNaam() {
		return halteNaam;
	}

	public int getRichting() {
		return richting;
	}

	public int getAankomsttijd() {
		return aankomsttijd;
	}

	ETA(String halteNaam, int richting, int aankomsttijd){
		this.halteNaam=halteNaam;
		this.richting=richting;
		this.aankomsttijd=aankomsttijd;
	}
}
