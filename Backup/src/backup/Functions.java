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
 * ���ݳ������̵߳��÷�����
 * @author Anston Sun
 * @param count ϵͳ�߼�������
 * @param pathfrom �������ļ�·��
 * @param pathto ���ݵ�ַ
 * @param gaptime ���α��ݼ��,Ĭ��Ϊ10s
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

	//ռλ����������δ���ݵ��ļ������
	private synchronized int takePlace() {
		for (int i = 0; i < ifBackuped.length;i++) {
			if (ifBackuped[i] == 0) {
				ifBackuped[i] = 1;
				return i;
			}
		}
		return -1;
	}
	
	//���ݳ���
	private void backupDir(File dir) throws IOException {
		//��ע����˳�򣨿������в鿴����޸�ʱ����棩
		String path = pathto + "\\" + System.currentTimeMillis();

		Path from = Paths.get(pathfrom);
		Path to = Paths.get(path);
		Path fromForDirs;
		
		//�ļ��б�
		File[] fromdir = new File(pathfrom).listFiles();
		final Path destPath = Paths.get(to.toString(), from.toFile().getName());
		
		if(Files.notExists(from)){
			JOptionPane.showMessageDialog(null, "�Ҳ����ļ�", "U�̱���", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		//��ʼ��ռλ��ǩ
		ifBackuped = new int[dirs.length];
		for(int i = 0; i < dirs.length;i++) {
			ifBackuped[i] = 0;
		}
		
		for (;;) {
			//ռλ�ɹ���ʼ�����ļ�
			int order = takePlace();
			if (order == -1 || order >= fromdir.length) {
				break;
			} else if (fromdir[order].getName().equals("System Volume Information")) {
				//ʹ��walkFileTree�����������ļ�
				fromForDirs = Paths.get(fromdir[order].getAbsolutePath());
				Files.walkFileTree(fromForDirs, new SimpleFileVisitor<Path>() {
					@Override
					public FileVisitResult visitFile(Path file,
							BasicFileAttributes attrs) throws IOException {
						Path dest = destPath.resolve(from.relativize(file));
						// �����·�������ڣ��򴴽�
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
	
	//searcher �̵߳��÷���������U���Ƿ����
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
			JOptionPane.showMessageDialog(null, "��ʼ����", "U�̱���", JOptionPane.INFORMATION_MESSAGE);
			try {
				Thread.sleep(gaptime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	//executor �̵߳��÷�����U�̲����ִ�б��ݳ���
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
			JOptionPane.showMessageDialog(null, "���������", "U�̱���", JOptionPane.INFORMATION_MESSAGE);
			try {
				Thread.sleep(gaptime);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
