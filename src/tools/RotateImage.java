package tools;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class RotateImage {

	/**
	 * 顺时针旋转图像
	 */
	public static BufferedImage rotateImage(BufferedImage srcImg, double rotateRadian) {

		Raster srcImgRst = srcImg.getData();
		
		BufferedImage dstImg = new BufferedImage(srcImg.getWidth(), srcImg.getHeight(), srcImg.getType());
		WritableRaster dstImgWR = dstImg.getRaster();
		
		final Point basePoint = new Point(srcImg.getWidth() / 2, srcImg.getHeight() / 2);
        int stepOfWidth = 1;
		for (int i = 0; i < srcImg.getWidth(); i = i + stepOfWidth) {
			for (int j = 0; j < srcImg.getHeight(); j = j + 1) {

				final Point srcPoint = new Point(i, j);
				if (srcPoint.x == basePoint.x || srcPoint.y == basePoint.y) {
					continue;
				}

				//找寻源图像中对应在目标图像中的坐标
				Point dstPoint = findDstPoint(basePoint, srcPoint, rotateRadian);
//
//				//写入目标图像
				writeToDstImg(srcImgRst, srcPoint, dstImgWR, dstPoint, stepOfWidth);
			}
		}

		//最近领域插值
		valueInterpolation(dstImg);

		return dstImg;
	}

	/**
	 * 最近领域插值
	 * @param dstImgWR
	 * @param imageWithArray
	 */
	private static void valueInterpolation(BufferedImage dstImg) {
		
		Raster dstImgRst = dstImg.getData();
		WritableRaster dstImgWR = dstImg.getRaster();
		
		for (int i = 0; i < dstImgRst.getWidth(); i++) {
			for (int j = 0; j < dstImgRst.getHeight(); j++) {

				int[] pixelValue = new int[1];
				pixelValue = dstImgRst.getPixel(i, j, pixelValue);

				//进行插值
				if (pixelValue[0] == 0) {
					if (i - 1 >= 0 && i < dstImgRst.getWidth() - 1 && j - 1 >= 0 && j < dstImgRst.getHeight() - 1) {
						int[] pixelValueLeft = new int[1];
						int[] pixelValueRight = new int[1];
						pixelValueLeft = dstImgRst.getPixel(i - 1, j, pixelValue);
						pixelValueRight = dstImgRst.getPixel(i + 1, j, pixelValue);

						pixelValue[0] = (pixelValueLeft[0] + pixelValueRight[0]) / 2;
						dstImgWR.setPixel(i, j, pixelValue);
					} else {
						pixelValue[0] = 0;
						dstImgWR.setPixel(i, j, pixelValue);
					}
					
				}
			}
		}
	}

	/**
	 * 找寻源图像中对应在目标图像中的坐标
	 */
	private static Point findDstPoint(Point basePoint, Point srcPoint, double rotateRadian) {

		Point finalPoint = new Point();

		double xDistance = srcPoint.x - basePoint.x;
		double yDistance = srcPoint.y - basePoint.y;
		double distance = Math.sqrt(xDistance * xDistance + yDistance * yDistance);

		double srcRadian = Math.atan(Math.abs(yDistance / xDistance));

		// 第三象限
		if (srcPoint.x < basePoint.x && srcPoint.y > basePoint.y) {
			double finalRadian = srcRadian - rotateRadian;
			finalPoint.x = basePoint.x - (int) (distance * Math.cos(finalRadian));
			finalPoint.y = basePoint.y + (int) (distance * Math.sin(finalRadian));

		} // 第二象限
		else if (srcPoint.x < basePoint.x && srcPoint.y < basePoint.y) {
			double finalRadian = srcRadian + rotateRadian;
			finalPoint.x = basePoint.x - (int) (distance * Math.cos(finalRadian));
			finalPoint.y = basePoint.y - (int) (distance * Math.sin(finalRadian));

		}// 第一象限
		else if (srcPoint.x > basePoint.x && srcPoint.y < basePoint.y) {
			double finalRadian = srcRadian - rotateRadian;
			finalPoint.x = basePoint.x + (int) (distance * Math.cos(finalRadian));
			finalPoint.y = basePoint.y - (int) (distance * Math.sin(finalRadian));

		}// 第四象限
		else if (srcPoint.x > basePoint.x && srcPoint.y > basePoint.y) {
			double finalRadian = srcRadian + rotateRadian;
			finalPoint.x = basePoint.x + (int) (distance * Math.cos(finalRadian));
			finalPoint.y = basePoint.y + (int) (distance * Math.sin(finalRadian));

		}

		return finalPoint;
	}

	/**
	 * 写入目标图像
	 * @param imageWithArray
	 * @param srcPoint
	 * @param dstImgWR
	 * @param dstPoint
	 */
	private static void writeToDstImg(Raster srcImgRst, Point srcPoint, WritableRaster dstImgWR, Point dstPoint, int stepOfWidth) {

		boolean isInsideOfWidth = (dstPoint.x >= 0) && (dstPoint.x + stepOfWidth - 1 < srcImgRst.getWidth());
		boolean isInsideOfHeight = (dstPoint.y >= 0) && (dstPoint.y + stepOfWidth - 1 < srcImgRst.getHeight());

		if (isInsideOfWidth && isInsideOfHeight) {
			int[] pixelValue = new int[1];
			pixelValue = srcImgRst.getPixel(srcPoint.x, srcPoint.y, pixelValue);
			
			dstImgWR.setPixel(dstPoint.x, dstPoint.y, pixelValue);
			dstImgWR.setPixel(dstPoint.x + stepOfWidth - 1 , dstPoint.y, pixelValue);
		}
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
		double rotateAngle = 5;
		BufferedImage dstImg = rotateImage(srcImg, Math.toRadians(rotateAngle));
		// 比这种方式慢0.2秒
//		 ImageTools tools = new ImageTools();
//		 BufferedImage dstImg = tools.rotateImage(srcImg, rotateAngle);
		System.out.println("耗时：" + (System.currentTimeMillis() - time));

		try {
			ImageIO.write(dstImg, "tif", new File("rotatedImage.tif"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
