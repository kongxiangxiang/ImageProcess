package tools;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.awt.geom.Point2D;

import javax.imageio.ImageIO;

public class CountAngleUseHough {

	/**
	 * 利用Hough计算角度
	 * 
	 * @param srcImg
	 *            原始图像
	 * @return
	 * @throws IOException
	 */
	public double countAngleUseHough(BufferedImage binaryImg) throws IOException {

		int width = binaryImg.getWidth();
		int height = binaryImg.getHeight();

		Raster binaryRst = binaryImg.getData();
		int[] pixelValue = new int[1];

		// 防止边界污染
		int startOfWidth = width / 100;
		int endOfWidth = width - startOfWidth;
		int startOfHeight = height / 100;
		int endOfHeight = height - startOfHeight;

		// 采点步长
		int stepOfWidth = width / 100;
		int stepOfHeight = height / 100;

		// 记录四边求的的角度
		List<Double> angleList = new Vector<Double>();

		// 提取上侧的点
		countAngleOfUp(binaryRst, pixelValue, startOfWidth, endOfWidth, startOfHeight, endOfHeight, stepOfWidth, angleList);

		// 提取下侧的点
		countAngleOfDown(binaryRst, pixelValue, startOfWidth, endOfWidth, startOfHeight, endOfHeight, stepOfWidth, angleList);

		// 提取左侧的点
		countAngleOfLeft(binaryRst, pixelValue, startOfWidth, endOfWidth, startOfHeight, endOfHeight, stepOfHeight, angleList);

		// 提取右侧的点
		countAngleOfRigth(binaryRst, pixelValue, startOfWidth, endOfWidth, startOfHeight, endOfHeight, stepOfHeight, angleList);

		for (int i = 0; i < angleList.size(); i++) {
			System.out.println(angleList.get(i));
		}

		return clusterAngles(angleList);
	}

	/**
	 * @param binaryRa
	 * @param pixelValue
	 * @param startOfWidth
	 * @param endOfWidth
	 * @param startOfHeight
	 * @param endOfHeight
	 * @param stepOfHeight
	 * @param angleList
	 */
	private void countAngleOfRigth(Raster binaryRa, int[] pixelValue, int startOfWidth, int endOfWidth, int startOfHeight, int endOfHeight, int stepOfHeight,
			List<Double> angleList) {
		List<Point> pointsOfRight = new Vector<Point>();

		for (int i = startOfHeight; i < endOfHeight; i += stepOfHeight) {
			for (int j = endOfWidth; j > startOfWidth; j--) {
				if (binaryRa.getPixel(j, i, pixelValue)[0] == 1) {
					pointsOfRight.add(new Point(j, i));
					break;
				}
			}
		}

		double angleOfRight = Hough(pointsOfRight) - 90;
		angleList.add(countMinAngle(angleOfRight));
	}

	/**
	 * @param binaryRa
	 * @param pixelValue
	 * @param startOfWidth
	 * @param endOfWidth
	 * @param startOfHeight
	 * @param endOfHeight
	 * @param stepOfHeight
	 * @param angleList
	 */
	private void countAngleOfLeft(Raster binaryRa, int[] pixelValue, int startOfWidth, int endOfWidth, int startOfHeight, int endOfHeight, int stepOfHeight,
			List<Double> angleList) {
		List<Point> pointsOfLeft = new Vector<Point>();

		for (int i = startOfHeight; i < endOfHeight; i += stepOfHeight) {
			for (int j = startOfWidth; j < endOfWidth; j++) {
				if (binaryRa.getPixel(j, i, pixelValue)[0] == 1) {
					pointsOfLeft.add(new Point(j, i));
					break;
				}
			}
		}

		double angleOfLeft = Hough(pointsOfLeft) - 90;
		angleList.add(countMinAngle(angleOfLeft));
	}

	/**
	 * @param binaryRa
	 * @param pixelValue
	 * @param startOfWidth
	 * @param endOfWidth
	 * @param startOfHeight
	 * @param endOfHeight
	 * @param stepOfWidth
	 * @param angleList
	 */
	private void countAngleOfDown(Raster binaryRa, int[] pixelValue, int startOfWidth, int endOfWidth, int startOfHeight, int endOfHeight, int stepOfWidth,
			List<Double> angleList) {
		List<Point> pointsOfBottom = new Vector<Point>();

		for (int j = startOfWidth; j < endOfWidth; j += stepOfWidth) {
			for (int i = endOfHeight; i > startOfHeight; i--) {
				if (binaryRa.getPixel(j, i, pixelValue)[0] == 1) {
					pointsOfBottom.add(new Point(j, i));
					break;
				}
			}
		}

		double angleOfBottom = Hough(pointsOfBottom) - 90;
		angleList.add(countMinAngle(angleOfBottom));
	}

	/**
	 * @param binaryRa
	 * @param pixelValue
	 * @param startOfWidth
	 * @param endOfWidth
	 * @param startOfHeight
	 * @param endOfHeight
	 * @param stepOfWidth
	 * @param angleList
	 */
	private void countAngleOfUp(Raster binaryRa, int[] pixelValue, int startOfWidth, int endOfWidth, int startOfHeight, int endOfHeight, int stepOfWidth,
			List<Double> angleList) {
		List<Point> pointsOfTop = new Vector<Point>();

		for (int j = startOfWidth; j < endOfWidth; j += stepOfWidth) {
			for (int i = startOfHeight; i < endOfHeight; i++) {
				if (binaryRa.getPixel(j, i, pixelValue)[0] == 1) {
					pointsOfTop.add(new Point(j, i));
					break;
				}
			}
		}

		// 极坐标角度和直角坐标角度的转换
		double angleOfTop = Hough(pointsOfTop) - 90;
		// 旋转最小的角度
		angleList.add(countMinAngle(angleOfTop));
	}

	/**
	 * 利用Hough计算角度
	 * 
	 * @param points
	 *            坐标点队列
	 * @return
	 */
	public double Hough(List<Point> points) {

		Map<Point2D.Float, Integer> polarCountMap = new HashMap<Point2D.Float, Integer>();

		double countMaxAngle = 0f;
		int countMax = 0;
		float stepLength = 0.2f;

		for (int i = 0; i < points.size(); i++) {
			for (float polarAngle = 0; polarAngle < 180f; polarAngle += stepLength) {

				// 整数化的极坐标值
				int polarValue = countPolarValue(points.get(i), polarAngle);

				// 计算出现次数最多的极坐标点
				int count = countMaxPolar(polarCountMap, polarAngle, polarValue);

				if (countMax < count) {
					countMax = count;
					countMaxAngle = polarAngle;
				}

			}
		}
		return countMaxAngle;
	}

	/**
	 * 计算对应角度的极坐标值
	 * 
	 * @param point
	 *            直角坐标点
	 * @param polarAngle
	 *            角度
	 * @return
	 */
	private int countPolarValue(Point point, double polarAngle) {

		// 角度转化成弧度
		double radians = Math.toRadians(polarAngle);
		
		int polarValue = (int) (point.x * Math.cos(radians) + point.y * Math.sin(radians) + 0.5);

		return polarValue;
	}

	/**
	 * 计算出现次数最多的极坐标点
	 * 
	 * @param polarCountMap
	 *            极坐标与出现次数的Map表
	 * @param polarAngle
	 *            角度
	 * @param polarValue
	 *            极坐标值
	 * @return
	 */
	private int countMaxPolar(Map<Point2D.Float, Integer> polarCountMap, float polarAngle, int polarValue) {

		Point2D.Float polar = new Point2D.Float(polarAngle, polarValue);
		Integer count = 0;

		if ((polarCountMap.get(polar) != null)) {
			count = polarCountMap.get(polar);
		}

		polarCountMap.put(polar, ++count);

		return count;
	}

	/**
	 * 计算最小的旋转角度
	 * 
	 * @param angle
	 *            直角坐标角度
	 * @return
	 */
	private double countMinAngle(double angle) {

		// 旋转最小的角度
		if (angle > 45) {
			angle = (90 - angle);
		} else if (angle < -45) {
			angle = -(90 + angle);
		} else {
			angle = -angle;
		}

		return angle;
	}

	/**
	 * 利用聚类的方法计算最终角度
	 * 
	 * @param angleList
	 *            角度队列
	 * @return
	 */
	public double clusterAngles(List<Double> angleList) {

		double angleAvg = 0;

		for (int i = 0; i < angleList.size(); i++) {
			angleAvg += angleList.get(i);
		}
		angleAvg = angleAvg / angleList.size();

		// 返回最后两个角度的平均值
		if (angleList.size() == 2) {
			return angleAvg;
		}

		double distanceMax = 0;// 与平均值最大间距
		int deleteIndex = 0;// 要删除的角度索引

		for (int i = 0; i < angleList.size(); i++) {
			if (Math.abs(angleList.get(i) - angleAvg) > distanceMax) {
				distanceMax = Math.abs(angleList.get(i) - angleAvg);
				deleteIndex = i;
			}
		}

		angleList.remove(deleteIndex);

		// 最大距离小于0.1时，停止递归
		if (distanceMax < 0.1) {
			return angleAvg;
		}

		return clusterAngles(angleList);

	}

	/**
	 * 旋转图像为指定角度
	 * 
	 * @param degree
	 *            旋转角度
	 * @return
	 */
	public BufferedImage rotateImage(BufferedImage srcImg, final double degree) {

		int w = srcImg.getWidth();
		int h = srcImg.getHeight();
		// int type = srcImg.getColorModel().getTransparency();
		int type = srcImg.getType();
		
		BufferedImage img;
		
		Graphics2D graphics2d;
		(graphics2d = (img = new BufferedImage(w, h, type)).createGraphics()).setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
		graphics2d.rotate(Math.toRadians(degree), w / 2, h / 2);
		graphics2d.drawImage(srcImg, 0, 0, null);
		graphics2d.dispose();

		return img;
	}

	/**
	 * 利用sobel算子求图像边缘
	 * 
	 * @param srcImage
	 *            源图像
	 * @return
	 */
	public BufferedImage Sobel(BufferedImage srcImage) {

		int height = srcImage.getHeight();
		int width = srcImage.getWidth();

		Raster srcRa = srcImage.getData();
		int[] gray = new int[1];

		float[][] buffer = new float[height][width];

		float DWidth = 0;
		float DHeight = 0;

		for (int i = 0 + 1; i < height - 1; i++) {
			for (int j = 0 + 1; j < width - 1; j++) {

				DHeight = srcRa.getPixel(j - 1, i + 1, gray)[0] + 2 * srcRa.getPixel(j, i + 1, gray)[0] + srcRa.getPixel(j + 1, i + 1, gray)[0]
						- srcRa.getPixel(j - 1, i - 1, gray)[0] - 2 * srcRa.getPixel(j, i - 1, gray)[0] - srcRa.getPixel(j + 1, i - 1, gray)[0];

				DWidth = srcRa.getPixel(j + 1, i - 1, gray)[0] + 2 * srcRa.getPixel(j + 1, i, gray)[0] + srcRa.getPixel(j + 1, i + 1, gray)[0]
						- srcRa.getPixel(j - 1, i - 1, gray)[0] - 2 * srcRa.getPixel(j - 1, i, gray)[0] - srcRa.getPixel(j - 1, i + 1, gray)[0];

				buffer[i][j] = (Math.abs(DWidth) + Math.abs(DHeight));
			}
		}

		double maxPixels = buffer[0][0];
		double minPixels = buffer[0][0];

		for (int i = 0 + 1; i < height - 1; i++) {
			for (int j = 0 + 1; j < width - 1; j++) {
				if (buffer[i][j] > maxPixels) {
					maxPixels = buffer[i][j];
				}
				if (buffer[i][j] < minPixels) {
					minPixels = buffer[i][j];
				}
			}
		}

		double threshold = 0.17;// 这个参数怎么来的啊
		double normalization = 0;

		BufferedImage dstImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
		WritableRaster dstWR = dstImage.getRaster();

		for (int i = 0 + 1; i < height - 1; i++) {
			for (int j = 0 + 1; j < width - 1; j++) {
				normalization = buffer[i][j] / (maxPixels - minPixels);
				if (normalization > threshold) {
					gray[0] = 1;
				} else {
					gray[0] = 0;
				}
				dstWR.setPixel(j, i, gray);
			}
		}

		return dstImage;
	}

	/**
	 * 灰度化图像
	 * 
	 * @param image
	 *            待处理图像
	 * @return 处理完的图像
	 */
	public BufferedImage rpg2Gray(BufferedImage image) {

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

	public static void main(String[] args) throws IOException {

		CountAngleUseHough test = new CountAngleUseHough();
		long time = System.currentTimeMillis();
		// String srcFilename = "110301.jpg";
		String srcFilename = "(3).JPG";

		BufferedImage srcImg = null;
		try {
			srcImg = ImageIO.read(new File(srcFilename));
		} catch (Exception e) {
			e.printStackTrace();
		}

		BufferedImage grayImg = null;
		BufferedImage binaryImg = null;

		// 提取图像边界
		if (srcImg.getType() == BufferedImage.TYPE_3BYTE_BGR || srcImg.getType() == BufferedImage.TYPE_INT_RGB) {
			grayImg = test.rpg2Gray(srcImg);
			binaryImg = test.Sobel(grayImg);
		} else {
			grayImg = srcImg;
			binaryImg = test.Sobel(grayImg);
		}

		// 旋转最小的角度
		double angle = test.countAngleUseHough(binaryImg);
		System.out.println("最终角度：" + angle);

		ImageIO.write(test.rotateImage(srcImg, angle), "tif", new File("rotateImage" + ".tif"));

		System.out.println("耗时" + (System.currentTimeMillis() - time));

	}

}
