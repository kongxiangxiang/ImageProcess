package tools;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ResizeImage {

	/**
	 * 对等比缩放效果比较好，非等比的效果一般
	 */
	public static BufferedImage resizeImage(BufferedImage srcImg, int dstWidth, int dstHeight) {

		BufferedImage dstImg = new BufferedImage(dstWidth, dstHeight, srcImg.getType());
		
//		firstMap(srcImg, dstImg);

		valueInterpolation(srcImg, dstImg);

		return dstImg;
	}

	/**
	 * 进行第一次映射，不用了，算法统一比较好
	 * @param srcImg
	 * @param dstImg
	 */
//	private static void firstMap(BufferedImage srcImg, BufferedImage dstImg) {
//		
//		WritableRaster dstImgWR = dstImg.getRaster();
//		
//		double dstWidth = dstImg.getWidth();
//		double dstHeight = dstImg.getHeight();
//		double srcWidth = srcImg.getWidth();
//		double srcHeight = srcImg.getHeight();
//		
//		double widthVaryRadio = dstWidth / srcWidth;
//		double heightVaryRadio = dstHeight / srcHeight;
//		
//		Raster srcImgRst = srcImg.getData();
//
//		for (int i = 0; i < srcWidth; i = i + 1) {
//			for (int j = 0; j < srcHeight; j = j + 1) {
//
//				final Point srcPoint = new Point(i, j);
//
//				Point dstPoint = new Point();
//				dstPoint.x = (int) (srcPoint.x * widthVaryRadio);
//				dstPoint.y = (int) (srcPoint.y * heightVaryRadio);
//				
//
//				int[] pixelValue = new int[1];
//				pixelValue = srcImgRst.getPixel(srcPoint.x, srcPoint.y, pixelValue);
//				dstImgWR.setPixel(dstPoint.x, dstPoint.y, pixelValue);
//
//			}
//		}
//	}

	/**
	 * 进行插值
	 * @param bufferImgRst
	 * @param dstImgWR
	 */
	private static BufferedImage valueInterpolation(BufferedImage srcImg, BufferedImage dstImg) {

		double dstWidth = dstImg.getWidth();
		double dstHeight = dstImg.getHeight();
		double srcWitdh = srcImg.getWidth();
		double srcHeigth = srcImg.getHeight();

		double widthVaryRadio = dstWidth / srcWitdh;
		double heightVaryRadio = dstHeight / srcHeigth;

		Raster dstImgRst = dstImg.getData();
		WritableRaster dstImgWR = dstImg.getRaster();

		Raster srcImgRst = srcImg.getData();

		for (int i = 0; i < dstWidth; i = i + 1) {
			for (int j = 0; j < dstHeight; j = j + 1) {

				int[] pixelValue = new int[1];
				pixelValue = dstImgRst.getPixel(i, j, pixelValue);
				
//				if (pixelValue[0] == 0) {

					Point dstPoint = new Point(i, j);
					Point srcPoint = new Point();
					
					double srcX = dstPoint.x / widthVaryRadio;
					double srcY = dstPoint.y / heightVaryRadio;
					
					srcPoint.x = (int) (srcX);
					srcPoint.y = (int) (srcY);
					
					Point2D.Double increment = new Point2D.Double();
					increment.x = srcX - srcPoint.x;
					increment.y = srcY - srcPoint.y;

					
					if (srcPoint.x >= 0 && srcPoint.x  < srcWitdh - 1 && srcPoint.y  >= 0 && srcPoint.y < srcHeigth - 1) {
						
						//取平均值
						pixelValue[0] = (int) countDstPixelValue(srcImgRst, srcPoint, increment);
						
						if(srcImg.getType() == BufferedImage.TYPE_BYTE_BINARY && pixelValue[0] > 1){
							pixelValue[0] = 1;
						}
						
						dstImgWR.setPixel(i, j, pixelValue);
					}
					
//				} else {
					dstImgWR.setPixel(i, j, pixelValue);
//				}
			}
		}

		return dstImg;
	}

	/**
	 * @param srcImgRst
	 * @param srcPoint
	 * @return
	 */
	private static double countDstPixelValue(Raster srcImgRst, Point srcPoint, Point2D.Double increment) {
		
		int[] pixelValue = new int[1];
		double dstPixelValue = 0;
		//双线插值算法，注意权重问题
		//f(i+u,j+v)   =   (1-u)(1-v)f(i,j)   +   (1-u)vf(i,j+1)   +   u(1-v)f(i+1,j)   +   uvf(i+1,j+1)
		dstPixelValue +=  (1 - increment.x) * (1 - increment.y) * srcImgRst.getPixel(srcPoint.x, srcPoint.y, pixelValue)[0];
		dstPixelValue +=  (increment.x) * (1 - increment.y) * srcImgRst.getPixel(srcPoint.x + 1, srcPoint.y, pixelValue)[0];
		dstPixelValue +=  (1 - increment.x) * (increment.y) *srcImgRst.getPixel(srcPoint.x, srcPoint.y + 1, pixelValue)[0];
		dstPixelValue +=  (increment.x) * (increment.y) *srcImgRst.getPixel(srcPoint.x + 1, srcPoint.y + 1, pixelValue)[0];
		return dstPixelValue;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String srcFilename = "binaryImage.tif";

		BufferedImage srcImg = null;
		try {
			srcImg = ImageIO.read(new File(srcFilename));
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (srcImg.getType() != BufferedImage.TYPE_BYTE_BINARY) {
			System.out.println("源图像不是二值图像");
			return;
		}

		long time = System.currentTimeMillis();
//		 int dstWitdh = 3360;
//		 int dstHeight = 4700;
		// int dstWitdh = 1680;
		// int dstHeight = 2350;
//		int dstWitdh = 840;
//		int dstHeight = 1175;
		 int dstWidth = (int) (1680 * 1.5);
		 int dstHeight = (int) (2350 * 1.5);
		BufferedImage dstImg = resizeImage(srcImg, dstWidth, dstHeight);
		
//		 ImageTools tools = new ImageTools();
//		 BufferedImage dstImg = tools.resizeImage(srcImg, dstWitdh, dstHeight);
		System.out.println("耗时：" + (System.currentTimeMillis() - time));

		try {
			ImageIO.write(dstImg, "tif", new File("resizeImage.tif"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
