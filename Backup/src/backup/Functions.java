package backup;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import javax.swing.JOptionPane;

/**
 * 
 * 备份程序与线程调用方法类
 * @author Anston Sun
 * @param count 系统逻辑分区数
 * @param pathfrom 待备份文件路径
 * @param pathto 备份地址
 * @param gaptime 两次备份间隔,默认为10s
 *
 */
public class Functions {
	private int count = 1;
	private int gaptime = 10000;
	private boolean isFileExists = false;
	private File[] dirs;
	private String pathfrom,pathto;
	int[] ifBackuped;

	public Functions(int count, String pathfrom, String pathto) {
		this.count = count;
		this.pathfrom = pathfrom;
		this.pathto = pathto;

	}

	//占位方法，返回未备份的文件夹序号
	private synchronized int takePlace() {
		for (int i = 0; i < ifBackuped.length;i++) {
			if (ifBackuped[i] == 0) {
				ifBackuped[i] = 1;
				return i;
			}
		}
		return -1;
	}
	
	//备份程序
	private void backupDir(File dir) throws IOException {
		//标注备份顺序（可用自行查看最后修改时间代替）
		String path = pathto + "\\" + System.currentTimeMillis();

		Path from = Paths.get(pathfrom);
		Path to = Paths.get(path);
		Path fromForDirs;
		
		//文件列表
		File[] fromdir = new File(pathfrom).listFiles();
		final Path destPath = Paths.get(to.toString(), from.toFile().getName());
		
		if(Files.notExists(from)){
			JOptionPane.showMessageDialog(null, "找不到文件", "U盘备份", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		//初始化占位标签
		ifBackuped = new int[dirs.length];
		for(int i = 0; i < dirs.length;i++) {
			ifBackuped[i] = 0;
		}
		
		for (;;) {
			//占位成功开始复制文件
			int order = takePlace();
			if (order == -1 || order >= fromdir.length) {
				break;
			} else if (fromdir[order].getName().equals("System Volume Information")) {
				//使用walkFileTree遍历并复制文件
				fromForDirs = Paths.get(fromdir[order].getAbsolutePath());
				Files.walkFileTree(fromForDirs, new SimpleFileVisitor<Path>() {
					@Override
					public FileVisitResult visitFile(Path file,
							BasicFileAttributes attrs) throws IOException {
						Path dest = destPath.resolve(from.relativize(file));
						// 如果父路径不存在，则创建
						if (Files.notExists(dest.getParent())) {
							Files.createDirectories(dest.getParent());
						}
						Files.copy(file, dest);
						return FileVisitResult.CONTINUE;
					}
				});
			}
		}
	}
	
	//searcher 线程调用方法，搜索U盘是否插入
	public synchronized void searchFile() {
		if (isFileExists) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		dirs = File.listRoots();
		
		if (dirs.length > count) {
			isFileExists = true;
			JOptionPane.showMessageDialog(null, "开始备份", "U盘备份", JOptionPane.INFORMATION_MESSAGE);
			try {
				Thread.sleep(gaptime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	//executor 线程调用方法，U盘插入后执行备份程序
	public synchronized void backupFile() {
		if (!isFileExists) {
			try {
				Thread.sleep(10);
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
			isFileExists = false;
			JOptionPane.showMessageDialog(null, "备份已完成", "U盘备份", JOptionPane.INFORMATION_MESSAGE);
			try {
				Thread.sleep(gaptime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
