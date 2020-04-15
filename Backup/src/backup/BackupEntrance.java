package backup;

import java.io.File;

public class BackupEntrance {
	static int count = File.listRoots().length;
	static String pathfrom = "G:\\";
	static String pathto = "E:\\U≈Ã±∏∑›";
	public static void main(String[] args) {
		System.out.println(count);
		Functions b = new Functions(count, pathfrom, pathto);
		Thread s = new Thread(new Searcher(b));
		Thread e1 = new Thread(new Executor(b));
		Thread e2 = new Thread(new Executor(b));
		s.start();
		e1.start();
		e2.start();
	}

}
