package cn.jdworks.etl.executor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.jms.JMSException;

import cn.jdworks.etl.protocol.TaskAssignment;

public class TaskReceiver extends Thread {
	private JmsConnector jmsConnector;
	private String destinationName;

	private boolean isRunning;
	private List<TaskRuntime> tasks;

	public TaskReceiver(UUID uuid) throws Exception {
		
		this.tasks = new ArrayList<TaskRuntime>();
		this.destinationName = "ETL.EXECUTOR." + uuid.toString();
		
		this.jmsConnector = new JmsConnector(App.connectionURI, this.destinationName, ConnectorType.Consumer);
	}

	public void startReceiver() {
		this.jmsConnector.start();
	}

	public void stopReceiver() {
		try {
			this.stopAllTasks();
			this.setRunning(false);
			this.join();
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.jmsConnector.stop();
	}

	private void stopAllTasks() {
		for (TaskRuntime task : this.tasks) {
			task.abortTask();
		}
		this.tasks.clear();
	}

	@Override
	public void run() {
		while (this.isRunning()) {
			TaskAssignment obj = this.jmsConnector.receiveMessage(300);
			try {
				TaskRuntime task = new TaskRuntime(obj.getId(), obj.getScriptName(), obj.getScriptArgs());
				this.addTask(task);
				task.runTask();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public synchronized void onException(JMSException ex) {
		ex.printStackTrace();
	}

	public synchronized boolean isRunning() {
		return isRunning;
	}

	public synchronized void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	public synchronized void addTask(TaskRuntime task) {
		this.tasks.add(task);
	}

	public synchronized void removeTask(int index) {
		if (this.tasks.get(index) != null)
			this.tasks.remove(index);
	}

}
