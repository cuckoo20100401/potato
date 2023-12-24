package org.potato.util;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

public class ImageUtils {
	
	/**
	 * 截图
	 * @param srcPath 原图片路径
	 * @param targetPath 目标图片路径
	 * @param x 截取的x坐标
	 * @param y 截取的y坐标
	 * @param width 截取宽度
	 * @param height 截取高度
	 * @throws IOException
	 */
	public static void cut(String srcPath, String targetPath, int x, int y, int width, int height) throws IOException {
		FileInputStream is = null;
		ImageInputStream iis = null;
		try {
			if (!new File(srcPath).exists()) throw new IOException("文件["+srcPath+"]不存在");
			is = new FileInputStream(srcPath);
			String extFileName = srcPath.substring(srcPath.lastIndexOf(".") + 1);
			Iterator<ImageReader> it = ImageIO.getImageReadersByFormatName(extFileName);
			ImageReader reader = it.next();
			iis = ImageIO.createImageInputStream(is);
			reader.setInput(iis, true);
			ImageReadParam param = reader.getDefaultReadParam();
			Rectangle rect = new Rectangle(x, y, width, height);
			param.setSourceRegion(rect);
			BufferedImage bi = reader.read(0, param);
			ImageIO.write(bi, extFileName, new File(targetPath));
			new File(srcPath).delete();
		} finally {
			if (is != null) is.close();
			if (iis != null) iis.close();
		}
	}
	
	/**
	 * 截图
	 * @param is 原图片输入流
	 * @param targetPath 目标图片路径
	 * @param x 截取的x坐标
	 * @param y 截取的y坐标
	 * @param width 截取宽度
	 * @param height 截取高度
	 * @throws IOException
	 */
	public static void cut(InputStream is, String targetPath, int x, int y, int width, int height) throws IOException {
		ImageInputStream iis = null;
		try {
			if (is == null) throw new IOException("源图片输入流不能为空");
			String extFileName = targetPath.substring(targetPath.lastIndexOf(".") + 1);
			Iterator<ImageReader> it = ImageIO.getImageReadersByFormatName(extFileName);
			ImageReader reader = it.next();
			iis = ImageIO.createImageInputStream(is);
			reader.setInput(iis, true);
			ImageReadParam param = reader.getDefaultReadParam();
			Rectangle rect = new Rectangle(x, y, width, height);
			param.setSourceRegion(rect);
			BufferedImage bi = reader.read(0, param);
			File targetFile = new File(targetPath);
			if(!targetFile.exists()) targetFile.mkdirs();
			ImageIO.write(bi, extFileName, targetFile);
		} finally {
			if (is != null) is.close();
			if (iis != null) iis.close();
		}
	}
}