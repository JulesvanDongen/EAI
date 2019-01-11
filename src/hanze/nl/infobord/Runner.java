package hanze.nl.infobord;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class Runner {
	
	public static void thread(Runnable runnable, boolean daemon) {
		Thread brokerThread = new Thread(runnable);
		brokerThread.setDaemon(daemon);
		brokerThread.start();
	}

    public static void main(String[] args) throws Exception {
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.INFO);

		InfoBord infoBord = InfoBord.getInfoBord();
		infoBord.setRegels();

		thread(new ListenerStarter("busqueue-json"),false);
    }
}
