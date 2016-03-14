package cn.jdworks.etl.protocol;

import java.util.ArrayList;
import java.util.List;
import java.io.*;

public class TaskReport implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4039171707127216978L;
	private int id;
	private int exitValue;
	private List<TaskLog> logs;

	public TaskReport() {
		super();
		this.setLogs(new ArrayList<TaskLog>());
	}

	public TaskReport(int id, int exitValue, List<TaskLog> logs) {
		super();
		this.setId(id);
		this.setExitValue(exitValue);
		this.setLogs(logs);
	}

	public static TaskReport fromBytes(byte[] bytes) {
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
			ObjectInputStream ois = new ObjectInputStream(bais);
			TaskReport stats = (TaskReport) ois.readObject();
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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getExitValue() {
		return exitValue;
	}

	public void setExitValue(int exitValue) {
		this.exitValue = exitValue;
	}

	public List<TaskLog> getLogs() {
		return logs;
	}

	public void setLogs(List<TaskLog> logs) {
		this.logs = logs;
	}


}
