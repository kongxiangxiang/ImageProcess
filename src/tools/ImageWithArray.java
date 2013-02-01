package tools;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageWithArray{
	
	private int width;
	private int height;
	//存放图像像素
	public short[][] pixelValueArray;

	public ImageWithArray(BufferedImage srcImg) {
		
		this.width = srcImg.getWidth();
		this.height = srcImg.getHeight();
		
		Raster srcImgRst = srcImg.getData();
		pixelValueArray = new short[srcImg.getWidth()][srcImg.getHeight()];
		
		int[] pixelValue = new int[1];
		for (int i = 0; i < srcImg.getWidth(); i++) {
			for (int j = 0; j < srcImg.getHeight(); j++) {
				pixelValue = srcImgRst.getPixel(i, j, pixelValue);
				pixelValueArray[i][j] = (short) pixelValue[0];
			  }
		}
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	public static void main(String[] args) throws IOException {
		
		 //要转化的图像路径
		 String path = "1.bmp";   
		 BufferedImage image = ImageIO.read(new File(path)); 
		 
		 //image = JPG2TIFF.rpg2Gray(image);
		 
		 ImageWithArray s = new ImageWithArray(image);
		 
		 for(int i = 0; i < 20; i++){
		     for(int j = 0; j < 20; j++){
				 System.out.print(s.pixelValueArray[i][j] + " ");
			 }
			 System.out.println();
		 }

	}

	
}
