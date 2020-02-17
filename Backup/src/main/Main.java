package main;

public class Main {
	static int count = 5;
	static String pathfrom = "E:\\U≈Ã±∏∑›";
	public static void main(String[] args) {
		Backup b = new Backup(count,pathfrom);
		Thread t1 = new Thread(new Searcher(b));
		Thread t2 = new Thread(new Executor(b));
		t1.start();
		t2.start();
	}

}
