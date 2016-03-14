package cn.jdworks.etl.executor;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class App {
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(App.class);
	public static String connectionURI;
	public static int udpPort;
	
	public static void main(String[] args) throws Exception {
		InputStream inputStream = App.class.getResourceAsStream("server.properties");
		Properties p = new Properties();
		try {
			p.load(inputStream);
			connectionURI = p.getProperty("connectionURI");
			udpPort = Integer.parseInt(p.getProperty("udpPort"));
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		TaskStatManager manager = new TaskStatManager();
		manager.startManager();
		TaskReceiver receiver = new TaskReceiver(manager.getUuid());
		receiver.startReceiver();
		
		LOG.info("Executor is running...press any keys to EXIT.");  
        try {
            BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in));  
            stdin.readLine();  
        } catch (Exception e) {
        }
        receiver.stopReceiver();
        manager.stopManager();
        LOG.info("Executor stopped.");  
	}
	
}
