package tools;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;

public class CompressImage {
//	public static void main(String[] args)
//    {
//        if(compressPic("002099201107130824_C0001_02.jpg", "Compress.jpg"))
//        {
//            System.out.println("压缩成功！"); 
//        }
//        else
//        {
//            System.out.println("压缩失败！"); 
//        }
//    }
    

    @SuppressWarnings("static-access")
	public  boolean compressPic(String srcFilePath, String descFilePath)
    {
        File file = null;
        BufferedImage src = null;
        FileOutputStream out = null;
        ImageWriter imgWrier;
        ImageWriteParam imgWriteParams;

        // 指定写图片的方式为 png
        imgWrier = ImageIO.getImageWritersByFormatName("jpg").next();
        imgWriteParams = new javax.imageio.plugins.jpeg.JPEGImageWriteParam(null);
        // 要使用压缩，必须指定压缩方式为MODE_EXPLICIT
        imgWriteParams.setCompressionMode(imgWriteParams.MODE_EXPLICIT);
        // 这里指定压缩的程度，参数qality是取值0~1范围内，
        imgWriteParams.setCompressionQuality((float)0.4);
        imgWriteParams.setProgressiveMode(imgWriteParams.MODE_DISABLED);
        ColorModel colorModel = ColorModel.getRGBdefault();
        // 指定压缩时使用的色彩模式
        imgWriteParams.setDestinationType(new javax.imageio.ImageTypeSpecifier(colorModel, colorModel
                .createCompatibleSampleModel(16, 16)));

        try
        {
            if(srcFilePath == null)
            {
                return false;
            }
            else
            {
                file = new File(srcFilePath);
                src = ImageIO.read(file);
//                System.out.println(src.getType()); 
//                if(src.getType() == BufferedImage.TYPE_3BYTE_BGR){
//                	System.out.println("进行转化"); 
//                	src = rpg2Gray(src);
//                }
//                
//				InputStream in = null;
//				byte[] b  = null;
//				ByteArrayOutputStream outByte = new ByteArrayOutputStream();
//				
//				ImageIO.write(src, "jpg",outByte);
//				b = outByte.toByteArray();
//			    in = new  ByteArrayInputStream(b);				
                
                //直接写入
//                ImageIO.write(src, "jpg",new File("NoCompress"  +  ".jpg"));
                out = new FileOutputStream(descFilePath);

                imgWrier.reset();
                // 必须先指定 out值，才能调用write方法, ImageOutputStream可以通过任何 OutputStream构造
                imgWrier.setOutput(ImageIO.createImageOutputStream(out));

                // 调用write方法，就可以向输入流写图片
                imgWrier.write(null, new IIOImage(src, null, null), imgWriteParams);

                out.flush();
                out.close();
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    
    /**
	 * 将RGB类型的图像转为灰度图像
	 * 
	 * @param oImg
	 *            原图像
	 * @return 转换后的二值图像
	 */
	public static BufferedImage rpg2Gray(BufferedImage image) throws IOException {

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
}
