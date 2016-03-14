package cn.jdworks.etl.protocol;

import java.io.*;

public class TaskAssignment implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6309268407673029365L;
	private int id;
	private String scriptName;
	private String scriptArgs;

	public TaskAssignment() {
		super();
	}

	public TaskAssignment(int id, String scriptName, String scriptArgs) {
		super();
		this.id = id;
		this.setScriptName(scriptName);
		this.setScriptArgs(scriptArgs);
	}

	public static TaskAssignment fromBytes(byte[] bytes) {
		try {
			ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
			ObjectInputStream ois = new ObjectInputStream(bais);
			TaskAssignment stats = (TaskAssignment) ois.readObject();
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

	/**
	 * Set the value of id.
	 **/
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Get the value of id.
	 **/
	public int getId() {
		return id;
	}

	public String getScriptName() {
		return scriptName;
	}

	public void setScriptName(String scriptName) {
		this.scriptName = scriptName;
	}

	public String getScriptArgs() {
		return scriptArgs;
	}

	public void setScriptArgs(String scriptArgs) {
		this.scriptArgs = scriptArgs;
	}


}
