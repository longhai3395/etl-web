package cn.jdworks.etl.executor;

import java.util.*;
import java.util.concurrent.TimeUnit;

import cn.jdworks.etl.protocol.TaskLog;
import cn.jdworks.etl.protocol.TaskReport;

import java.io.*;

public class TaskRuntime extends Thread {
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TaskRuntime.class);

	public class StreamWatch extends Thread {
		private InputStream is;
		private List<TaskLog> logs;
		private String type;

		public StreamWatch(InputStream is, String type, List<TaskLog> logs) {
			this.is = is;
			this.type = type;
			this.logs = logs;
		}

		public void run() {
			try {
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String line = null;
				while ((line = br.readLine()) != null) {
					this.logs.add(new TaskLog(new Date(), this.type, line));
				}
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}

	private boolean isRunning = false;
	private Process taskProc;
	private StreamWatch watchOut;
	private StreamWatch watchErr;
	private JmsConnector jmsConnector;

	private int taskId;
	private String command;
	private List<TaskLog> logs;

	private static final String destinationName = "ETL.REPORT";

	public TaskRuntime(int id, String script, String args) throws Exception {
		this.jmsConnector = new JmsConnector(App.connectionURI, destinationName, ConnectorType.Producer, true);

		this.taskId = id;
		this.command = script + " " + id + " " + args;
		this.logs = new ArrayList<TaskLog>();
	}

	public void runTask() {
		try {
			ProcessBuilder pb = new ProcessBuilder(this.command);
			this.taskProc = pb.start();
			this.watchOut = new StreamWatch(this.taskProc.getInputStream(), "STDOUT", this.logs);
			this.watchErr = new StreamWatch(this.taskProc.getErrorStream(), "STDERR", this.logs);
			this.watchOut.start();
			this.watchErr.start();
			this.setRunning(true);
			this.start();
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	public void abortTask() {
		if (isRunning()) {
			try {
				this.setRunning(false);
				this.taskProc.destroyForcibly();
				this.join();
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}

	public void run() {
		while (isRunning()) {
			try {
				if (this.taskProc.waitFor(500, TimeUnit.MILLISECONDS)) {
					// the task ended. send JMS back to broker
					send(this.taskProc.exitValue());
					break;
				}
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}

	private void send(int exitValue) {
		TaskReport report = new TaskReport(this.taskId, exitValue, this.logs);
		if(this.jmsConnector.connect()){
			if(!this.jmsConnector.sendMessage(report)){
				LOG.error(report);
			}
		}else{
			LOG.error(report);
		}
	}

	public synchronized boolean isRunning() {
		return isRunning;
	}

	public synchronized void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}
}
