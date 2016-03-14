package cn.jdworks.etl.backend.biz;

import java.net.URI;

import javax.jms.JMSException;
import javax.servlet.ServletContext;

import org.apache.activemq.broker.BrokerFactory;
import org.apache.activemq.broker.BrokerService;

public class ExecutorManager extends Thread {

	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ExecutorManager.class);

	private BrokerService broker;

	private static final String connectionURI = "tcp://localhost:9100";

	private boolean isRunning;

	public ExecutorManager() {
	}

	public void startExecutorManager() {
		if (startBroker()) {
			LOG.info("broker start.");
		}
	}

	public void stopExecutorManager() {
		this.stopBroker();
		LOG.info("broker stop.");
	}

	@Override
	public void run() {
		while (this.isRunning()) {

		}
	}

	private boolean startBroker() {
		try {
			// This systemproperty is used if we dont want to
			// have persistence messages as a default
			System.setProperty("activemq.persistenceAdapter", "org.apache.activemq.store.vm.VMPersistenceAdapter");

			broker = BrokerFactory.createBroker(new URI("broker://()/localhost"));
			broker.setBrokerName("DefaultBroker");
			broker.addConnector(connectionURI);
			broker.setUseShutdownHook(false);
			broker.start();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			this.broker = null;
			return false;
		}
	}

	private void stopBroker() {
		if (this.broker != null) {
			try {
				this.broker.stop();
				this.broker.waitUntilStopped();
				this.broker = null;
			} catch (Exception e) {
				e.printStackTrace();
				this.broker = null;
			}
		}
	}

	public synchronized boolean isRunning() {
		return isRunning;
	}

	public synchronized void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}
}
