package tools;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;


public class ImageTools {

	/**
	 * 将Gray类型的图像转为二值图像
	 * 
	 * @param grayImage
	 *            原图像
	 * @return 转换后的二值图像
	 */
	public BufferedImage gray2Binary(BufferedImage grayImage) {

		int width = grayImage.getWidth();
		int height = grayImage.getHeight();

		BufferedImage binaryImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);

		Raster grayRa = grayImage.getData();
		WritableRaster binaryWR = binaryImage.getRaster();

		int[] gray = new int[1];
		int[] binary = new int[1];

		// int threhold = findThreshold(oImg);
		int threhold = 200;

		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {

				if (grayRa.getPixel(i, j, gray)[0] < threhold) {
					binary[0] = 0;
				} else {
					binary[0] = 1;
				}

				binaryWR.setPixel(i, j, binary);
			}
		}

		return binaryImage;
	}

	/**
	 * 将RGB类型的图像转为灰度图像
	 * 
	 * @param oImg
	 *            原图像
	 * @return 转换后的二值图像
	 */
	public BufferedImage rpg2Gray(BufferedImage image) throws IOException {

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

	/**
	 * 二值化图像
	 * 
	 * @param srcImage
	 *            原图像
	 * @return 转换后的二值图像
	 */
	public BufferedImage binaryzation(BufferedImage srcImage) throws IOException {

		BufferedImage binaryImage = null;

		// 如果图像是灰度图像
		if (srcImage.getType() == BufferedImage.TYPE_BYTE_GRAY) {
			binaryImage = gray2Binary(srcImage);
		}
		// 如果图像是RGB图像
		else if (srcImage.getType() == BufferedImage.TYPE_3BYTE_BGR) {
			binaryImage = rpg2Gray(srcImage);
			binaryImage = gray2Binary(binaryImage);
		} else {
			binaryImage = srcImage;
		}
		return binaryImage;
	}

	/**
	 * 对影像进行滤波处理，采用中值滤波算法
	 * 
	 * @param image
	 *            待处理的影像数据
	 * @return 处理完的影像数据
	 * @throws Exception
	 */
	public BufferedImage filter(BufferedImage image) throws Exception {

		int width = image.getWidth();
		int height = image.getHeight();
		int[] gray = new int[1];

		Raster ra = image.getData();
		BufferedImage nImg = new BufferedImage(width, height, image.getType());
		WritableRaster nRa = nImg.getRaster();

		for (int i = 1; i < height - 1; i++) {
			for (int j = 1; j < width - 1; j++) {
				nRa.setPixel(j, i, ra.getPixel(j, i, gray));
			}
		}

		// 如果某点像素值为黑色，即像素值为0，其周围四邻域内如果一个像素值为0，则该点像素值不变，否则变为1
		int[][] fourNeighbour = { { 1, 0 }, { -1, 0 }, { 0, -1 }, { 0, 1 } };

		boolean mark = true;

		for (int i = 1; i < height - 1; i++) {
			for (int j = 1; j < width - 1; j++) {

				if (ra.getPixel(j, i, gray)[0] == 0) {
					for (int k = 0; k < fourNeighbour.length; k++) {

						if (ra.getPixel(j + fourNeighbour[k][1], i + fourNeighbour[k][0], gray)[0] == 0) {
							mark = false;
							break;
						}

						if (mark) {
							gray[0] = 1;// 变为背景颜色，白色
							nRa.setPixel(j, i, gray);
						} else {
							mark = true;
						}

					}
				}

			}

		}
		return nImg;
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
	 * 缩放图像
	 * 
	 * @param degree
	 *            旋转角度
	 * @return
	 */
	public static BufferedImage resizeImage(final BufferedImage bufferedimage,
            final int w, final int h) {
        int type = bufferedimage.getColorModel().getTransparency();
        BufferedImage img;
        Graphics2D graphics2d;
        (graphics2d = (img = new BufferedImage(w, h, type)).createGraphics()).setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        graphics2d.drawImage(bufferedimage, 0, 0, w, h, 0, 0, bufferedimage.getWidth(), bufferedimage.getHeight(), null);
        graphics2d.dispose();
        return img;
    }


	/**
	 * 水平方向膨胀处理
	 * 
	 * @param image
	 *            待处理图像
	 * @return 处理完的图像
	 */
	public BufferedImage hDilate(BufferedImage image, double dilteRatio) {

		int gray[] = new int[1];
		int width = image.getWidth();
		int height = image.getHeight();

		int dilteRadius = (int) (dilteRatio * width);

		Raster ra = image.getData();
		BufferedImage nImg = new BufferedImage(width, height, image.getType());
		WritableRaster nRa = nImg.getRaster();

		for (int i = 0; i < height; i++) {
			for (int j = 0 + dilteRadius; j < width - dilteRadius; j++) {
				gray = ra.getPixel(j, i, gray);
				if (gray[0] == 1) {
					for (int k = j - dilteRadius; k < j + dilteRadius; k++) {
						nRa.setPixel(k, i, gray);
					}
					j = j + dilteRadius;
				}
			}
		}

		return nImg;
	}

	/**
	 * 垂直方向膨胀处理
	 * 
	 * @param image
	 *            待处理图像
	 * @return 处理后的图像
	 */
	public BufferedImage vDilate(BufferedImage image, double dilteRatio) {
		int gray[] = new int[1];
		int width = image.getWidth();
		int height = image.getHeight();

		int dilteRadius = (int) (dilteRatio * height);

		Raster ra = image.getData();
		BufferedImage nImg = new BufferedImage(width, height, image.getType());
		WritableRaster nRa = nImg.getRaster();

		for (int j = 0; j < width; j++) {
			for (int i = dilteRadius; i < height - dilteRadius; i++) {
				gray = ra.getPixel(j, i, gray);
				if (gray[0] == 1) {
					for (int k = i - dilteRadius; k < i + dilteRadius; k++) {
						nRa.setPixel(j, k, gray);
					}
					i = i + dilteRadius;
				}
			}
		}

		return nImg;
	}

	/**
	 * 缩放图像
	 * 
	 * @param image
	 *            待处理图像
	 * @return 处理后的图像
	 */
	public BufferedImage zoom(BufferedImage image, int width, int height, int sourceX, int sourceY, int sourceWidth, int sourceHeight, int targetX, int targetY, int targetWidth,
			int targetHeight) {
		BufferedImage nImg = new BufferedImage(width, height, image.getType());

		ImageFilter filter = new CropImageFilter(sourceX, sourceY, sourceWidth, sourceHeight);
		ImageProducer producer = new FilteredImageSource(image.getSource(), filter);
		Image img = Toolkit.getDefaultToolkit().createImage(producer);

		Graphics g = nImg.getGraphics();
		g.drawImage(img, targetX, targetY, targetWidth, targetHeight, null);

		return nImg;
	}



	/**
	 * 从字节数组中获取影像数据
	 * 
	 * @param data
	 *            字节数组
	 * @return 影像数据
	 * @throws Exception
	 */
	public BufferedImage readImageFromByteArr(byte[] data) throws IOException {
		ByteArrayInputStream in = new ByteArrayInputStream(data);

		BufferedImage image = ImageIO.read(in);

		in.close();

		return image;
	}


	/**
	 * 对实例图像做水平方向的差分
	 * 
	 * @param image
	 *            实例图像
	 * @return 水平方向的差分后的图像
	 */
	public BufferedImage hDifference(BufferedImage image) {

		int width = image.getWidth();
		int height = image.getHeight();
		Raster ra = image.getData();
		int[] gray = new int[1];

		BufferedImage nImg = new BufferedImage(width, height, image.getType());

		Graphics g = nImg.getGraphics();
		g.drawImage(image, 0, 0, null);
		g.dispose();
		WritableRaster nRa = nImg.getRaster();

		for (int i = 1; i < height - 1; i++) {
			for (int j = 1; j < width - 1; j++) {

				gray[0] = Math.abs(ra.getPixel(j, i + 1, gray)[0] - ra.getPixel(j, i, gray)[0]);
				nRa.setPixel(j, i, gray);
			}
		}

		return nImg;
	}

	/**
	 * 对实例图像做垂直方向的差分
	 * 
	 * @param image
	 *            实例图像
	 * @return 垂直方向的差分后的图像
	 */
	public BufferedImage vDifference(BufferedImage image) {
		int width = image.getWidth();
		int height = image.getHeight();
		Raster ra = image.getData();

		int[] gray = new int[1];

		BufferedImage nImg = new BufferedImage(width, height, image.getType());

		Graphics g = nImg.getGraphics();
		g.drawImage(image, 0, 0, null);
		g.dispose();
		WritableRaster nRa = nImg.getRaster();

		for (int i = 1; i < height - 1; i++) {
			for (int j = 1; j < width - 1; j++) {

				gray[0] = Math.abs(ra.getPixel(j + 1, i, gray)[0] - ra.getPixel(j, i, gray)[0]);
				nRa.setPixel(j, i, gray);
			}
		}

		return nImg;
	}

	/**
	 * 求二值化图像的阈值
	 * 
	 * @param image
	 *            实例图像
	 * @return 用来二值化图像的阈值
	 */
	public int findThreshold(BufferedImage image) {

		int width = image.getWidth();
		int height = image.getHeight();
		int[] gray = new int[1];
		Raster ra = image.getData();

		double n0 = 0;
		double n1 = 0;
		double u0 = 0;
		double u1 = 0;
		double w0 = 0;
		double w1 = 0;
		double[] g = new double[25];

		double area = width * height;

		for (int T = 10; T < 250; T = T + 10) {

			n0 = 0;
			n1 = 0;
			u0 = 0;
			u1 = 0;

			for (int i = 0; i < height - 1; i++) {
				for (int j = 0; j < width - 1; j++) {
					gray = ra.getPixel(j, i, gray);
					if (gray[0] <= T) {
						u0 = u0 + gray[0];
						n0 = n0 + 1;
					} else {
						u1 = u1 + gray[0];
					}

				}
			}

			n1 = area - n0;

			w0 = n0 / (area);
			w1 = n1 / (area);
			u0 = u0 / (n0);
			u1 = u1 / (n1);

			g[T / 10] = w1 * w0 * (u1 - u0) * (u1 - u0);

		}

		double maxVariance = 0;
		int threshold = 0;

		for (int i = 1; i < g.length; i++) {
			if (g[i] > maxVariance) {
				maxVariance = g[i];
				threshold = i;
			}

		}
		return threshold * 10;
	}

}
