package cn.jdworks.etl.executor;

import java.util.*;

import java.io.*;
import java.net.*;

import cn.jdworks.etl.protocol.ExecutorHeartbeat;
import cn.jdworks.etl.protocol.TaskStat;

public class TaskStatManager extends Thread {

	private UUID uuid;
	private Timer timer;

	private DatagramSocket socket;
	private DatagramPacket packet;
	private static final int RECV_MAX = 60000;

	private static final String destinationName = "ETL.EXECUTOR.HEARTBEAT";
	private boolean isRunning = false;

	private JmsConnector jmsConnector;

	public TaskStatManager() throws Exception {
		this.jmsConnector = new JmsConnector(App.connectionURI, destinationName, ConnectorType.Producer, false);

		this.taskStats = new Hashtable<Integer, TaskStat>();

		this.timer = new Timer();
		try {
			this.socket = new DatagramSocket(App.udpPort);
			this.packet = new DatagramPacket(new byte[RECV_MAX], RECV_MAX);
			this.socket.setSoTimeout(200);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	public void startManager() {
		// start JMS connector
		this.jmsConnector.start();

		// start send timer
		this.uuid = UUID.randomUUID();
		this.timer.schedule(new HeartbeatSendTimerTask(this), 0, 2000);

		// start UDP receiver
		this.setRunning(true);
		this.start();
	}

	public void stopManager() {
		this.timer.cancel();
		this.jmsConnector.stop();

		this.setRunning(false);
		try {
			this.join();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public UUID getUuid() {
		return uuid;
	}

	public synchronized boolean isRunning() {
		return isRunning;
	}

	public synchronized void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	private Hashtable<Integer, TaskStat> taskStats;

	public synchronized Hashtable<Integer, TaskStat> getTaskStats() {
		return taskStats;
	}

	private synchronized void setTaskStat(TaskStat stat) {
		this.taskStats.put(stat.getId(), stat);
	}

	@Override
	public void run() {
		while (this.isRunning()) {
			try {
				this.socket.receive(this.packet);
				TaskStat stat = TaskStat.fromBytes(this.packet.getData());
				this.setTaskStat(stat);
			} catch (SocketTimeoutException e) {
				continue;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	class HeartbeatSendTimerTask extends TimerTask {
		private TaskStatManager manager;

		public HeartbeatSendTimerTask(TaskStatManager mgr) {
			super();
			this.manager = mgr;
		}

		@Override
		public void run() {
			this.manager.send();
		}
	}

	private void send() {
		ExecutorHeartbeat msg = new ExecutorHeartbeat(this.uuid, this.getTaskStats());
		this.jmsConnector.sendMessage(msg);
	}
}
