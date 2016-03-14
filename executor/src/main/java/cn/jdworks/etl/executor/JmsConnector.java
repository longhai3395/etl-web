package cn.jdworks.etl.executor;

import java.io.Serializable;
import java.util.Timer;
import java.util.TimerTask;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;


enum ConnectorType {
	Producer, Consumer, Both
}


public class JmsConnector extends TimerTask implements ExceptionListener {

	private Timer timer;

	private String connectionURI;
	private String destinationName;

	private ActiveMQConnectionFactory connectionFactory;
	private Connection connection;
	private Session session;
	private Destination destination;
	private MessageProducer producer;
	private MessageConsumer consumer;
	private ConnectorType type;
	private boolean isPersistent;
	private boolean isConnected;

	public JmsConnector(String connectionURI, String destinationName, ConnectorType type) throws Exception {
		this(connectionURI, destinationName, type, false);
	}

	public JmsConnector(String connectionURI, String destinationName, ConnectorType type, boolean isPersistent) {
		this.connectionURI = connectionURI;
		this.type = type;
		this.isConnected = false;
		this.destinationName = destinationName;
		this.isPersistent = isPersistent;
	}

	public void start() {
		this.timer = new Timer();
		this.timer.schedule(this, 0, 3000);
	}

	public void stop() {
		this.timer.cancel();
		this.disconnect();
	}

	@Override
	public void run() {
		if (!this.isConnected)
			this.connect();
	}

	public boolean connect() {
		// create JMS connection
		try {
			// Create a ConnectionFactory
			connectionFactory = new ActiveMQConnectionFactory(connectionURI);
			connectionFactory.setTrustAllPackages(true);

			// Create a Connection
			connection = connectionFactory.createConnection();
			connection.start();
			connection.setExceptionListener(this);

			// Create a Session
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

			// Create the destination (Topic or Queue)
			destination = session.createQueue(destinationName);

			// Create a MessageProducer from the Session to the Topic or Queue
			if (type == ConnectorType.Producer || type == ConnectorType.Both) {
				producer = session.createProducer(destination);
				if (this.isPersistent)
					producer.setDeliveryMode(DeliveryMode.PERSISTENT);
				else
					producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			}
			if (type == ConnectorType.Consumer || type == ConnectorType.Both) {
				consumer = session.createConsumer(destination);
			}
			this.isConnected = true;
			return true;
		} catch (Exception e) {
			this.disconnect();
			e.printStackTrace();
			return false;
		}
	}

	private void disconnect() {
		this.isConnected = false;
		try {
			if (this.consumer != null)
				this.consumer.close();
		} catch (Exception e) {
		} finally {
			this.consumer = null;
		}
		try {
			if (this.producer != null)
				this.producer.close();
		} catch (Exception e) {
		} finally {
			this.producer = null;
		}
		this.destination = null;
		try {
			if (this.session != null)
				this.session.close();
		} catch (Exception e) {
		} finally {
			this.session = null;
		}
		try {
			if (this.connection != null)
				this.connection.close();
		} catch (Exception e) {
		} finally {
			this.connection = null;
		}
	}

	public boolean sendMessage(Serializable obj){
		try{
			ObjectMessage msg = this.session.createObjectMessage(obj);
			this.producer.send(msg);
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public <T> T receiveMessage(int ms) {
		try {
			Message message = this.consumer.receive(ms);
			if (message instanceof ObjectMessage) {
				ObjectMessage objMsg = (ObjectMessage) message;
				@SuppressWarnings("unchecked")
				T obj = (T) objMsg.getObject();
				return obj;
			}else
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public void onException(JMSException e) {
		e.printStackTrace();
		disconnect();
	}

}
