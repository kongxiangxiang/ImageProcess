package tools;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class EnhanceImageContrast {
	
	  /**
	 * 将RGB类型的图像转为灰度图像
	 * 
	 * @param oImg
	 *            原图像
	 * @return 转换后的二值图像
	 */
	public static  BufferedImage rpg2Gray(BufferedImage image) throws IOException {

		int[] gray = new int[1];

		BufferedImage grayImg = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
		WritableRaster grayWR = grayImg.getRaster();

		for (int i = 0; i < image.getHeight(); i++) {
			for (int j = 0; j < image.getWidth(); j++) {

				int pixel = image.getRGB(j, i);
				int r = (pixel & 0xff0000) >> 16;
				int g = (pixel & 0xff00) >> 8;
				int b = (pixel & 0xff);

				gray[0] = (30 * r + 59 * g + 11 * b) / 100;

				grayWR.setPixel(j, i, gray);

			}
		}

		return grayImg;
	}

    public static BufferedImage enhanceImageContrast(BufferedImage srcImg){
    	
		int width = srcImg.getWidth();
		int height = srcImg.getHeight();
		int[] pixelValue = new int[1];
		
		Raster srcRa = srcImg.getData();
		
		BufferedImage dstImg = new BufferedImage(srcImg.getWidth(), srcImg.getHeight(),srcImg.getType());
		WritableRaster dstWR = dstImg.getRaster();
    		
		int[][] template = {{1,1,1},{1,-9,1},{1,1,1}};
		
		for (int i = 1; i < height - 1; i++) {
			for (int j = 1; j < width - 1; j++) {
				
				pixelValue[0] = 0;
				for (int m = -1; m <= 1; m++) {
					for (int n = -1; n <= 1; n++) {
						pixelValue[0] += srcRa.getPixel(j + m, i + n, pixelValue)[0] * template[m + 1][n + 1];
					}
				}
				
				if(pixelValue[0] > 255){
					pixelValue[0] = 255;
				}
				
				if(pixelValue[0] < 0){
					pixelValue[0] = 0;
				}
				
				dstWR.setPixel(j, i, pixelValue);
				
			}
		}
		
		Raster dstRa = dstImg.getData();
		
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				
				pixelValue[0] = srcRa.getPixel(j, i, pixelValue)[0] - dstRa.getPixel(j, i, pixelValue)[0];
				
				if(pixelValue[0] > 255){
					pixelValue[0] = 255;
				}
				
				if(pixelValue[0] < 0){
					pixelValue[0] = 0;
				}
				
				dstWR.setPixel(j, i, pixelValue);
				
			   }
			}
		
		return dstImg;
		
    }
    
	public static void main(String[] args) throws IOException {
		
	    String srcFilename = "(1).tif";
	    	
		BufferedImage srcImg = null;
		try {
			srcImg = ImageIO.read(new File(srcFilename));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(srcImg.getType() == BufferedImage.TYPE_3BYTE_BGR){
			srcImg = rpg2Gray(srcImg);
		}
		
		BufferedImage dstImg = null;
		dstImg = enhanceImageContrast(srcImg);
		
	    ImageIO.write(dstImg, "jpg", new File("result" +  ".jpg"));
		
		

	}

}
