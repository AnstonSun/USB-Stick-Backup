package backup;

public class Searcher implements Runnable {
	private Functions backup = null;
	
	public Searcher(Functions bu) {
		this.backup = bu;
	}
	
	@Override
	public void run() {
		while (true) {
			backup.searchFile();
		}
	}
}
