package backup;

public class Executor implements Runnable {
	private Functions backup = null;
	
	public Executor(Functions bu) {
		this.backup = bu;
	}
	
	@Override
	public void run() {
		while (true) {
			backup.backupFile();
		}

	}

}