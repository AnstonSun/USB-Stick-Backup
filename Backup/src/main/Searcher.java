package main;

public class Searcher implements Runnable {
	private Backup backup = null;
	
	public Searcher(Backup rf) {
		this.backup = rf;
	}
	
	@Override
	public void run() {
		while (true) {
			backup.searchFile();
		}
	}
}
