package cn.jdworks.etl.protocol;

import java.util.Date;
import java.io.*;

public class TaskLog implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5273025048320273391L;
	private Date time;
	private String type;
	private String log;

	public TaskLog() {
		super();
	}

	public TaskLog(Date time, String type, String log) {
		super();
		this.setLog(log);
		this.setTime(time);
		this.setType(type);
	}

	public static TaskLog fromBytes(byte[] bytes) {
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
			ObjectInputStream ois = new ObjectInputStream(bais);
			TaskLog stats = (TaskLog) ois.readObject();
			bais.close();
			ois.close();
			return stats;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public byte[] toBytes() {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(this);
			byte[] bytes = baos.toByteArray();
			baos.close();
			oos.close();
			return bytes;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getLog() {
		return log;
	}

	public void setLog(String log) {
		this.log = log;
	}

}
