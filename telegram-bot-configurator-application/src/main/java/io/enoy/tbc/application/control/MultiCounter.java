package io.enoy.tbc.application.control;

import java.util.ArrayList;
import java.util.List;

public class MultiCounter {

	private long lastTime = System.currentTimeMillis();

	private List<TimedTask> timedTasks = new ArrayList<>();

	public void init() {
		lastTime = System.currentTimeMillis();
	}

	public void update() {
		long time = System.currentTimeMillis();
		long delta = time - lastTime;
		lastTime = time;
		update(delta);
	}

	public void update(long delta) {
		timedTasks.forEach(tt->tt.update(delta));
	}

	public TimedTask addTimedTask(Runnable task, long time) {
		TimedTask tt = new TimedTask(task, time);
		return addTimedTask(tt);
	}

	public TimedTask addTimedTask(TimedTask tt) {
		timedTasks.add(tt);
		return tt;
	}

	public boolean removeTimedTask(TimedTask task) {
		return timedTasks.remove(task);
	}

	public class TimedTask {

		private Runnable task;
		private long time;
		private long counter = 0;

		public TimedTask(Runnable task, long time) {
			this.task = task;
			this.time = time;
		}

		public void update(long delta) {
			counter += delta;
			if (counter >= time) {
				counter = 0;
				task.run();
			}
		}
		
		public void setTask(Runnable task) {
			this.task = task;
		}
		
		public Runnable getTask() {
			return task;
		}
	}

}
