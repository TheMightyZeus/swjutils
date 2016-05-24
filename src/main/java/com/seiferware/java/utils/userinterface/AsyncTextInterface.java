package com.seiferware.java.utils.userinterface;

import com.seiferware.java.utils.event.userinterface.InterfaceClosedEvent;
import com.seiferware.java.utils.event.userinterface.ReceiveTextEvent;
import com.seiferware.java.utils.event.userinterface.UserInterfaceEvent;
import com.seiferware.java.utils.threading.AsyncTask;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A wrapper for text interfaces that awaits input asynchronously and fires events.
 *
 * @see UserInterfaceEvent
 */
public class AsyncTextInterface implements ITextInterface {
	protected ITextInterface inner;
	protected Thread thread;
	protected AsyncTask task;
	/**
	 * Creates the asynchronous wrapper.
	 *
	 * @param inner
	 * 		The interface to wrap.
	 */
	public AsyncTextInterface(@NotNull ITextInterface inner) {
		this.inner = inner;
		task = new LineReader(this, inner);
		thread = new Thread(task);
	}
	@Override
	public void close() {
		inner.close();
		task.stop();
		thread = null;
	}
	@Override
	public boolean hasLineToRead() {
		return false;
	}
	@Override
	public boolean isActive() {
		return inner.isActive() && thread != null && thread.isAlive();
	}
	@Override
	@Nullable
	public String readLine() {
		return null;
	}
	@Override
	public void send(@NotNull String data) {
		inner.send(data);
	}
	@Override
	public void send(@NotNull byte[] data) {
		inner.send(data);
	}
	@Override
	public void sendLine(@NotNull String data) {
		inner.sendLine(data);
	}
	/**
	 * Begins the asynchronous operations.
	 */
	public void start() {
		thread.start();
	}
}

class LineReader extends AsyncTask {
	private AsyncTextInterface owner;
	private ITextInterface in;
	public LineReader(@NotNull AsyncTextInterface owner, @NotNull ITextInterface in) {
		this.owner = owner;
		this.in = in;
	}
	@Override
	public void onLoop() {
		if(in.hasLineToRead()) {
			new ReceiveTextEvent(owner, in.readLine()).fire();
		}
	}
	@Override
	protected void onStop() {
		new InterfaceClosedEvent(owner).fire();
	}
}
