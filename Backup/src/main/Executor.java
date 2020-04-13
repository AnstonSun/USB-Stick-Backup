package main;

public class Executor implements Runnable {
	private Backup backup = null;
	
	public Executor(Backup rf) {
		this.backup = rf;
	}
	
	@Override
	public void run() {
		while (true) {
			backup.backupFile();
		}

	}

}
