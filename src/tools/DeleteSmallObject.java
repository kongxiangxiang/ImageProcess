package tools;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

public class DeleteSmallObject {

	/**
	 * 去除小于指定阈值的小对象
	 */
	public static BufferedImage deleteSmallObject(BufferedImage srcImg, int threhold) {
		
		ImageWithArray imageWithArray = new ImageWithArray(srcImg);
		
		BufferedImage dstImg = new BufferedImage(imageWithArray.getWidth(), imageWithArray.getHeight(),BufferedImage.TYPE_BYTE_BINARY);
		WritableRaster dstImgWR = dstImg.getRaster();
		
		for (int i = 0; i < imageWithArray.getWidth(); i++) {
			for (int j = 0; j < imageWithArray.getHeight(); j++) {
				if (imageWithArray.pixelValueArray[i][j] == 1) {
					
					//获取第一个种子节点
					List<Point> seedPointList = getFirstSeedPoint(imageWithArray, new Point(i,j));
					
					//获取所有种子节点,组成连通域
					//这里每次调用对myImage参数值进行了改变
					seedPointList = getOtherSeedPoint(imageWithArray, seedPointList);
					
					//如果连通域面积大于阈值，则将连通域面积写入目标图像
					if(seedPointList.size() > threhold){
						writeToDstImg(dstImgWR, seedPointList);
					}
				}
			}
		}
		return dstImg;
	}

	/**
	 * 获取第一个种子节点
	 * @param imageWithArray
	 * @param point 
	 * @return
	 */
	private static List<Point> getFirstSeedPoint(ImageWithArray imageWithArray, Point point) {
		
		List<Point> seedPointList = new ArrayList<Point>();
		seedPointList.add(point);
		return seedPointList;
	}


	/**
	 * 获取所有种子节点,组成连通域
	 * @param srcImg
	 * @return
	 */
	private static List<Point> getOtherSeedPoint(ImageWithArray imageWithArray, List<Point> seedPointList) {

		int smallObjectArea = 0;
		
		while (seedPointList.size() > smallObjectArea) {
			
			Point point = seedPointList.get(smallObjectArea);
			smallObjectArea++;
			
			//搜索八邻域像素为1的点
			for (int m = -1; m <= 1; m++) {
				int w = point.x + m;
				if (w < 0 || w >= imageWithArray.getWidth()) {
					continue;
				}
				for (int n = -1; n <= 1; n++) {
					int h = point.y + n;
					if (h < 0 || h >= imageWithArray.getHeight() || (m == 0 && n == 0)) {
						continue;
					}

					if (imageWithArray.pixelValueArray[w][h] == 1) {
						imageWithArray.pixelValueArray[w][h] = 0;
						seedPointList.add(new Point(w, h));
					}
				}
			}
			
		}
		
		return seedPointList;
	}

	/**
	 * 将连通域面积写入目标图像
	 * @param pixelValue
	 * @param dstImgWR
	 * @param seedPointList
	 */
	private static void writeToDstImg(WritableRaster dstImgWR, List<Point> seedPointList) {
		
		int[] pixelValue = new int[1];
		pixelValue[0] = 1;
		
		for (int i = 0; i < seedPointList.size(); i++){
			Point point = seedPointList.get(i);
			dstImgWR.setPixel(point.x, point.y, pixelValue);
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
		int threhold = 500;
		BufferedImage dstImg = deleteSmallObject(srcImg, threhold);
		System.out.println("耗时：" + (System.currentTimeMillis() - time));
		
		try {
			ImageIO.write(dstImg, "tif",new File("deleteObject.tif"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
