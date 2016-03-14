package cn.jdworks.etl.protocol;

import java.io.InputStream;
import java.net.*;
import java.util.Properties;

import cn.jdworks.etl.protocol.TaskStat;

public class TaskBase {

	private TaskStat stats;
	private int udpPort = 5050;
	private DatagramSocket socket;
	private InetAddress addr;

	public TaskBase(String[] args) {
		this.stats = new TaskStat();
		try {
			int id = Integer.parseInt(args[0]);
			this.stats.setId(id);
			this.addr = InetAddress.getByName("127.0.0.1");
			this.socket = new DatagramSocket();
			InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("config.properties");
			Properties p = new Properties();
			p.load(inputStream);
			this.udpPort = Integer.parseInt(p.getProperty("udpPort"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void close() {
		try {
			this.socket.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void update() {
		try {
			byte[] buffer = this.stats.toBytes();
			DatagramPacket packet = new DatagramPacket(buffer, buffer.length, addr, udpPort);
			socket.send(packet);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void onThreadCreated() {
		this.stats.setThreadNum(this.stats.getThreadNum() + 1);
	}

	public void onThreadReleased() {
		this.stats.setThreadNum(this.stats.getThreadNum() - 1);
	}

	public void onConnectionCreated(String dbName) {
		Integer num = this.stats.getDbConns().get(dbName);
		if (num == null) {
			num = 1;
		} else {
			num++;
		}
		this.stats.getDbConns().put(dbName, num);
	}

	public void onConnectionReleased(String dbName) {
		Integer num = this.stats.getDbConns().get(dbName);
		if (num != null) {
			num--;
			if (num > 0) {
				this.stats.getDbConns().put(dbName, num);
			} else {
				this.stats.getDbConns().remove(dbName);
			}
		}
	}
}
