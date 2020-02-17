package main;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import javax.swing.JOptionPane;

public class Backup {
	private int count = 1;
	private boolean flag = false;
	private File[] dirs;
	private String pathfrom;

	public Backup(int count,String pathfrom) {
		this.count = count;
		this.pathfrom = pathfrom;
	}

	private void backupDir(File dir) throws IOException {
		String path = pathfrom + "\\" + System.currentTimeMillis();
		Path from = Paths.get("G:\\");
		Path to = Paths.get(path);
		final Path destPath = Paths.get(to.toString(), from.toFile().getName());
		
		if(Files.notExists(from)){
			System.out.println("源文件不存在");
		}
		
		Files.walkFileTree(from, new SimpleFileVisitor<Path>() {
			@Override
			public FileVisitResult visitFile(Path file,
					BasicFileAttributes attrs) throws IOException {
				Path dest = destPath.resolve(from.relativize(file));
				// 如果说父路径不存在，则创建
				if (Files.notExists(dest.getParent())) {
					Files.createDirectories(dest.getParent());
				}
				Files.copy(file, dest);
		   return FileVisitResult.CONTINUE;
		    }
		});
	}
	
	public synchronized void searchFile() {
		if (flag) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		dirs = File.listRoots();

		if (dirs.length > count) {
			flag = true;
			JOptionPane.showMessageDialog(null, "开始备份", "U盘备份", JOptionPane.INFORMATION_MESSAGE);
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public synchronized void backupFile() {
		if (!flag) {
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		if (dirs.length > count) {
			for (int i = count; i < dirs.length; i++) {
					try {
						backupDir(dirs[i]);
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
			flag = false;
			JOptionPane.showMessageDialog(null, "备份已完成", "U盘备份", JOptionPane.INFORMATION_MESSAGE);
			try {
				Thread.sleep(60000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
