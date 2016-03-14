package cn.jdworks.etl.protocol;

import java.io.Serializable;
import java.util.Hashtable;
import java.util.UUID;

import cn.jdworks.etl.protocol.TaskStat;

public class ExecutorHeartbeat implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3643183573364211417L;
	private UUID id;
	private Hashtable<Integer, TaskStat> taskStats;

	public ExecutorHeartbeat() {
		super();
	}

	public ExecutorHeartbeat(UUID id, Hashtable<Integer, TaskStat> taskStats) {
		super();
		this.setId(id);
		this.taskStats = taskStats;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	/**
	 * Set the value of tasks.
	 **/
	public void setTaskStats(Hashtable<Integer, TaskStat> taskStats) {
		this.taskStats = taskStats;
	}

	/**
	 * Get the value of tasks.
	 **/
	public Hashtable<Integer, TaskStat> getTaskStats() {
		return taskStats;
	}
}
