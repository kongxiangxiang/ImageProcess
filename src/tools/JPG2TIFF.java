package tools;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class JPG2TIFF {
	
	/**
	 * 将其他RGB类型的位图转为灰度图像
	 * 
	 * @param oImg
	 *            原图像
	 * @return 转换后的灰度图像
	 */
	public static BufferedImage rpg2Gray(BufferedImage image) throws IOException {

		int[] gray = new int[1];

		BufferedImage grayImg = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
		WritableRaster nRa = grayImg.getRaster();
		
		for (int i = 0; i < image.getHeight(); i++) {
			for (int j = 0; j < image.getWidth(); j++) {

				int pixel = image.getRGB(j, i);
				int r = (pixel & 0xff0000) >> 16;
				int g = (pixel & 0xff00) >> 8;
				int b = (pixel & 0xff);

				gray[0] = (30 * r + 59 * g + 11 * b) / 100;

				nRa.setPixel(j, i, gray);

			}
		}
		
		return grayImg;
	}
	
	/**
	 * 将其他RGB类型的位图转为二值图像
	 * 
	 * @param oImg
	 *            原图像
	 * @return 转换后的二值图像
	 */
	private static BufferedImage gray2Binary(BufferedImage oImg) {
		int width = oImg.getWidth();
		int height = oImg.getHeight();

		BufferedImage nImg = new BufferedImage(width, height,
				BufferedImage.TYPE_BYTE_BINARY);

		Raster ora = oImg.getData();
		WritableRaster nra = nImg.getRaster();

		int[] ogray = new int[3];
		int[] ngray = new int[1];

		int threhold = 140;
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {
				ogray = ora.getPixel(i, j, ogray);

				if (ogray[0] < threhold && ogray[1] < threhold && ogray[2] < threhold) {
					ngray[0] = 0;
				} else {
					ngray[0] = 1;
				}

				nra.setPixel(i, j, ngray);
			}
		}

		return nImg;
	}

	/**
	 * 二值化图像
	 * 
	 * @param srcImage
	 *         原图像
	 * @return 转换后的二值图像
	 */
	private static BufferedImage binaryzation(BufferedImage srcImage) throws IOException {
		
		BufferedImage binaryImage = null;
		
		//如果图像是灰度图像
		if (srcImage.getType() == BufferedImage.TYPE_BYTE_GRAY) {															
			binaryImage = gray2Binary(srcImage);
		}
		//如果图像是RGB图像
		else if (srcImage.getType() == BufferedImage.TYPE_3BYTE_BGR) {								
			binaryImage = rpg2Gray(srcImage);
			binaryImage = gray2Binary(binaryImage);
		}
		else{
			binaryImage = srcImage;
		}
		return binaryImage;
	}
	
	  private static void transferImage(String path) throws IOException{   

		File file = new File(path);
		File[] array = file.listFiles();

		for (int i = 0; i < array.length/20; i++) {
			if (array[i].isFile()) {

				//读取源图像
				BufferedImage image = null;

//				System.out.println(array[i].getPath());
				try {
					image = ImageIO.read(new File(array[i].getPath()));
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				BufferedImage binaryImage = null;
				//二值化
				binaryImage = binaryzation(image);

				String[] pathImg = new String[10];
				pathImg = array[i].getPath().split("\\.");
				
				ImageIO.write(binaryImage, "tif",new File(pathImg[0] + ".tif"));

			} else if (array[i].isDirectory()) {
				transferImage(array[i].getPath());
			}
		}
	}

	public static void main(String[] args) throws IOException {
		
		 //要转化的图像路径
		 String path = "D:\\My Documents\\MyEclipse\\TestForIP\\tools";   
		 transferImage(path); 

	}

}
