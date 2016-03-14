package cn.jdworks.etl.task;

import cn.jdworks.etl.protocol.TaskBase;

public class FooTask extends TaskBase {

	public FooTask(String[] args) {
		super(args);
	}

	public void run() {
		System.out.println("foo task start.");
		onThreadCreated();
		update();
		System.err.println("error log");
		onThreadReleased();
		update();
		onConnectionCreated("foo");
		System.err.println("error log");
		update();
		onConnectionReleased("foo");
		System.err.println("error log");
		update();
		System.out.println("foo task end.");
	}

	public static void main(String[] args) {
		System.out.println("args:");
		for (int i = 0; i < args.length; i++)
			System.out.println(args[i]);

		FooTask task = new FooTask(args);
		task.run();
		task.close();
	}
}
