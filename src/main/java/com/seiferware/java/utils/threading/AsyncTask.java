package com.seiferware.java.utils.threading;

/**
 * Represents a loop-based task to be run asynchronously. Handles lifecycle and state, and uses callbacks to implement
 * use-specific functionality.
 */
public abstract class AsyncTask implements Runnable {
	private boolean stopped = true;
	private boolean finished = false;
	/**
	 * Whether the task has completed.
	 *
	 * @return {@code true} if the task has been run and completed.
	 */
	public final boolean isFinished() {
		return finished;
	}
	/**
	 * Whether the task is stopped.
	 *
	 * @return {@code true} before the task has started, and after it has completed.
	 */
	public final boolean isStopped() {
		return stopped;
	}
	/**
	 * The only method that must be overridden, {@code onLoop} will be called repeatedly while the task runs.
	 */
	protected abstract void onLoop();
	/**
	 * This method is called when the task is started, before the first call to {@link #onLoop}. Implementations may
	 * override this method to perform startup tasks.
	 */
	protected void onStart() {
	}
	/**
	 * This method is called when the task is completed, after the last call to {@link #onLoop}. Implementations may
	 * override this method to perform cleanup tasks.
	 */
	protected void onStop() {
	}
	/**
	 * An implementation-specific method that can be overridden if there are circumstances under which the task may not
	 * be interrupted.
	 *
	 * @return {@code true} if it is acceptable to interrupt the task.
	 */
	protected boolean requestStop() {
		return true;
	}
	@Override
	public final void run() {
		stopped = false;
		finished = false;
		onStart();
		while(!stopped) {
			onLoop();
			try {
				Thread.sleep(100);
			} catch (Exception ignored) {
			}
		}
		onStop();
		finished = true;
	}
	/**
	 * Begins the task.
	 */
	public final void start() {
		stopped = false;
		finished = false;
		new Thread(this).start();
	}
	/**
	 * Interrupts the task.
	 */
	public final void stop() {
		stopped = requestStop();
	}
	/**
	 * Interrupts the task and waits for it to return.
	 */
	public final void stopAndBlock() {
		stopped = requestStop();
		if(stopped) {
			while(!finished) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException ignored) {
				}
			}
		}
	}
}
