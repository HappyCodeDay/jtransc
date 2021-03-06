package com.jtransc.thread;

import com.jtransc.JTranscSystem;
import com.jtransc.annotation.JTranscMethodBody;

public class JTranscThreading {
	static public Impl impl = new Impl(null);

	static public class Impl {
		Impl parent;

		public Impl(Impl parent) {
			this.parent = parent;
		}

		public boolean isSupported() {
			if (parent != null) return parent.isSupported();
			return _isSupported();
		}

		public void start(Thread thread) {
			if (parent != null) {
				parent.start(thread);
				return;
			}
			System.err.println("WARNING: Threads not supported! Executing thread code in the parent's thread!");
			thread.run();
		}

		@JTranscMethodBody(target = "js", value = "return false;")
		@JTranscMethodBody(target = "d", value = "return true;")
		private boolean _isSupported() {
			return !JTranscSystem.isJTransc();
		}

		public boolean isAlive(Thread thread) {
			if (parent != null) return parent.isAlive(thread);
			//return thread._isAlive;
			return false;
		}
	}
}
