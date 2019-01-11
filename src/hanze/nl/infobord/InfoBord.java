package hanze.nl.infobord;

import java.io.IOException;
import java.util.HashMap;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import java.awt.Insets;


import org.codehaus.jackson.map.ObjectMapper;

public class InfoBord {

	private static HashMap<String,Integer> laatsteBericht = new HashMap<String,Integer>();
	private static HashMap<String,JSONBericht> infoBordRegels = new HashMap<String,JSONBericht>();
	private static InfoBord infobord;
	private static int hashValue;
	private JFrame scherm;
	private JLabel regel1;
	private JLabel regel2;
	private JLabel regel3;
	private JLabel regel4;
	
	private InfoBord(){
		this.scherm = new JFrame("InfoBord");
		JPanel panel = new JPanel();
		BoxLayout boxlayout = new BoxLayout(panel, BoxLayout.Y_AXIS);
		panel.setLayout(boxlayout);
		panel.setBorder(new EmptyBorder(new Insets(10, 20, 10, 20)));
		this.regel1=new JLabel("-regel1-");
		this.regel2=new JLabel("-regel2-");
		this.regel3=new JLabel("-regel3-");
		this.regel4=new JLabel("-regel4-");
		panel.add(regel1);
		panel.add(regel2);
		panel.add(regel3);
		panel.add(regel4);
		hashValue=0;
		scherm.add(panel);
		scherm.pack();
		scherm.setVisible(true);
	}
	
	public static InfoBord getInfoBord(){
		if(infobord==null){
			infobord=new InfoBord();
		}
		return infobord;
	}
	
	public void setRegels(){
		String[] infoTekst={"--1--","--2--","--3--","--4--","leeg"};
		int[] aankomsttijden=new int[5];
		int totaalTijden=0;
		int aantalRegels = 0;
		if(!infoBordRegels.isEmpty()){
			for(String busID: infoBordRegels.keySet()){
				JSONBericht regel = infoBordRegels.get(busID);
				int dezeTijd=regel.getAankomsttijd();
				String dezeTekst=regel.getInfoRegel();
				int plaats=aantalRegels;
				for(int i=aantalRegels;i>0;i--){
					if(dezeTijd<aankomsttijden[i-1]){
						aankomsttijden[i]=aankomsttijden[i-1];
						infoTekst[i]=infoTekst[i-1];
						plaats=i-1;
					}
				}
				aankomsttijden[plaats]=dezeTijd;
				infoTekst[plaats]=dezeTekst;
				if(aantalRegels<4){
					aantalRegels++;
				}
			}
		}
		if(checkRepaint(aantalRegels, aankomsttijden)){
			repaintInfoBord(infoTekst);
		}
	}
	
	private boolean checkRepaint(int aantalRegels, int[] aankomsttijden){
		int totaalTijden=0;
		for(int i=0; i<aantalRegels;i++){
			totaalTijden+=aankomsttijden[i];
		}
		if(hashValue!=totaalTijden){
			hashValue=totaalTijden;
			return true;
		}
		return false;
	}

	private void repaintInfoBord(String[] infoTekst){
		regel1.setText(infoTekst[0]);
		regel2.setText(infoTekst[1]);
		regel3.setText(infoTekst[2]);
		regel4.setText(infoTekst[3]);
		scherm.repaint();		
	}
	
	public static void verwerkBericht(String incoming){
        try {
			JSONBericht bericht = new ObjectMapper().readValue(incoming, JSONBericht.class);
			String busID = bericht.getBusID();
			Integer tijd = bericht.getTijd();
			if (!laatsteBericht.containsKey(busID) || laatsteBericht.get(busID)<=tijd){
				laatsteBericht.put(busID, tijd);
				if (bericht.getAankomsttijd()==0){
					infoBordRegels.remove(busID);
				} else {
					infoBordRegels.put(busID, bericht);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
