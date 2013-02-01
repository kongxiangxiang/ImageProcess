package tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;

public class ExactImageBySizeAndTread {

	//最大线程数
	private int maxThreadNum = 5;
	
	//已处理完的文件夹
	private int finishedDir = 0;
	
	// 线程池执行类
	private ExecutorService executor;
	
	/**
	 * 初始化线程池信息
	 */
	private void initThreadPool() {
		if (executor == null) {
			executor = Executors.newFixedThreadPool(maxThreadNum);
		}
	}

//	/**
//	 * 根据图像头文件判断一个文件是否为图片文件(GIF,PNG,JPG)
//	 * 
//	 * @param srcFileName
//	 * @return
//	 */
//	public static boolean isImageByFileHeader(String srcFileName) {
//
//		FileInputStream imgFile = null;
//		byte[] b = new byte[10];
//		int l = -1;
//
//		try {
//			imgFile = new FileInputStream(srcFileName);
//			l = imgFile.read(b);
//			imgFile.close();
//		} catch (Exception e) {
//			return false;
//		}
//
//		if (l == 10) {
//			byte b0 = b[0];
//			byte b1 = b[1];
//			byte b2 = b[2];
//			byte b3 = b[3];
//			byte b6 = b[6];
//			byte b7 = b[7];
//			byte b8 = b[8];
//			byte b9 = b[9];
//			if (b0 == (byte) 'G' && b1 == (byte) 'I' && b2 == (byte) 'F') {
//				return true;
//			} else if (b1 == (byte) 'P' && b2 == (byte) 'N' && b3 == (byte) 'G') {
//				return true;
//			} else if (b6 == (byte) 'J' && b7 == (byte) 'F' && b8 == (byte) 'I' && b9 == (byte) 'F') {// jpg
//				return true;
//			} else {
//				return false;
//			}
//		} else {
//			return false;
//		}
//	}

	/**
	 * 根据图像后缀名判断一个文件是否为图片文件(GIF,PNG,JPG,TIF)
	 * 
	 * @param srcFileName
	 * @return
	 */

	public boolean isImageByFilename(String srcFileName) {

		// 可以判断的图像格式
		final String[] IMAGE_FORMAT = { "GIF", "PNG", "JPG", "TIF", "gif", "png", "jpg", "tif", "BMP", "bmp" };

		String suffix = srcFileName.substring(srcFileName.lastIndexOf(".") + 1, srcFileName.length());

		for (int i = 0; i < IMAGE_FORMAT.length; i++) {
			if (suffix.equals(IMAGE_FORMAT[i])) {
				return true;
			}
		}

		return false;

	}

	/**
	 * 计算文件大小
	 */
	public long getFileSizes(File file) throws Exception {

		long size = 0;
		if (file.exists()) {
			FileInputStream fileIS = null;

			// 把文件放入流中来计算大小
			fileIS = new FileInputStream(file);
			size = fileIS.available();
		} else {
			file.createNewFile();
			System.out.println("文件不存在");
		}
		return size;

	}

	/**
	 * 提取每个子文件中容量最大的图片
	 * 
	 * @throws IOException
	 */
	public File exactImageBySize(File dir) throws IOException {
		
		File[] fileList = obtainFiles(dir.getPath());

		//存储最大图片的信息
		long maxSize = 0;
		int maxSizeIndex = 0;
						
		for (int i = 0; i < fileList.length; i++) {

				if (isImageByFilename(fileList[i].getPath()) == false) {
					continue;
				}

				// 读取源图像
//				BufferedImage srcImg = null;

				try {					
//					srcImg = ImageIO.read(new File(fileList[i].getPath()));
					if (getFileSizes(fileList[i]) > maxSize) {
						maxSize = getFileSizes(fileList[i]);
						maxSizeIndex = i;
					}
					// System.out.println(getFileSizes(array[i]));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			System.out.println(fileList[maxSizeIndex].getPath());
			return new File(fileList[maxSizeIndex].getPath());

	}

	
	/**
	 * 获取文件列表
	 */
	public File[] obtainFiles(String path){
		
        File file = new File(path);	
        
		if(!file.exists()){
			System.out.println("要检测的文件夹不存在！ ");
			return null;
		}	
		File[] fileList = file.listFiles();
		
		return fileList;
	}
	
	/**
	 * 创建目标文件夹
	 */
	public void createDstDir(String dstPath){
		
		File dstDir = null;
		try {
			dstDir = new File(dstPath);

			if (!(dstDir.exists()) && !(dstDir.isDirectory())) {
				boolean createOk = dstDir.mkdirs();
				if (createOk) {
					System.out.println("创建文件夹成功！ ");
				} else {
					System.out.println("创建文件夹失败！ ");
					return ;
				}
			} else {
				System.out.println(dstDir.getName() + " 文件夹已经存在 ");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 执行
	 */
	public boolean execute(String path) throws Exception {
		
		initThreadPool();
		
		File[] fileList = obtainFiles(path);
		
		//获取子文件夹
		List<File> dirList = new Vector<File>();
		for (int i = 0; i < fileList.length; i++) {
			if (fileList[i].isDirectory()) {
				dirList.add(fileList[i]);
			}
		}
		
		if(dirList.size() == 0){
			System.out.println("没有找到子文件夹！ ");
			return false;
		}
		

		final String resultPath = path + "_result";
		//创建目标文件夹
	    createDstDir(resultPath);	
		
		for (int i = 0; i < dirList.size(); i++) {	
			
				final File dir = fileList[i];
				
				executor.execute(new Thread() {
					public void run() {
						try {
						
						File maxFile = exactImageBySize(dir);
							
						String dirName = maxFile.getPath().substring(maxFile.getPath().indexOf("("),maxFile.getPath().lastIndexOf(")") + 1);
						
						ImageIO.write(ImageIO.read(maxFile), "jpg", new File(resultPath + "/" + dirName + ".jpg"));
						
						maxFile.delete();
						
						finishedDir++;
							 
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				});
		}		
		
		while (finishedDir < dirList.size()) {
			Thread.sleep(1000);
		}
		
		if(finishedDir == dirList.size()){
			System.out.println("共处理 " + dirList.size() + "个子文件夹");
			return true;
		}else{
			return false;
		}
		
	}

	public static void main(String[] args) throws Exception {     
		
		String path = args[0];
		boolean exactOk = true;

		ExactImageBySizeAndTread test = new ExactImageBySizeAndTread();

		long time = System.currentTimeMillis();
		
		exactOk = test.execute(path);
		
		if(exactOk == true){
			System.out.println("提取图像成功！ ");
		}else{
			System.out.println("提取图像失败！ ");
		}
		
		System.out.println("提取时间约为：" + (float)(System.currentTimeMillis() - time)/60000 + "分");
	}
}
