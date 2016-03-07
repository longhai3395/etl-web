package cn.jdworks.etl.protocol;

import java.io.Serializable;
import java.util.Hashtable;
import cn.jdworks.etl.protocol.TaskStats;

public class ExecutorHeartbeat implements Serializable{
    private String ipAddr;
    private double cpuLoad;
    private double memLoad;
    private Hashtable<Integer, TaskStats> tasks;
 
    public ExecutorHeartbeat() {
       super();
    }
 
    public ExecutorHeartbeat(String ipAddr, double cpuLoad, double memLoad, Hashtable<Integer, TaskStats> tasks) {
        super();
        this.ipAddr = ipAddr;
        this.cpuLoad = cpuLoad;
	this.memLoad = memLoad;
	this.tasks = tasks;
    }


    /**
     * Set the value of ipAddr.
     **/
    public void setIpAddr(String ipAddr) {
	this.ipAddr = ipAddr;
    }

    /**
     * Get the value of ipAddr.
     **/
    public String getIpAddr() {
	return ipAddr;
    }
    /**
     * Set the value of cpuLoad.
     **/
    public void setCpuLoad(double cpuLoad) {
	this.cpuLoad = cpuLoad;
    }

    /**
     * Get the value of cpuLoad.
     **/
    public double getCpuLoad() {
	return cpuLoad;
    }
    /**
     * Set the value of memLoad.
     **/
    public void setMemLoad(double memLoad) {
	this.memLoad = memLoad;
    }

    /**
     * Get the value of memLoad.
     **/
    public double getMemLoad() {
	return memLoad;
    }

    /**
     * Set the value of tasks.
     **/
    public void setTasks(Hashtable<Integer, TaskStats>  tasks) {
	this.tasks = tasks;
    }

    /**
     * Get the value of tasks.
     **/
    public Hashtable<Integer, TaskStats> getTasks() {
	return tasks;
    }

}
