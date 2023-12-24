package org.potato.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class FileUtils {
	
	/**
	 * 读取文件内容
	 * @param filePath 文件绝对路径
	 * @param charsetName 文件字符集
	 * @return 文件所有内容
	 * @throws IOException
	 */
	public static String read(String filePath, String charsetName) throws IOException {

		File file = new File(filePath);
		if(!file.exists()) throw new FileNotFoundException();

		StringBuffer fileContent = new StringBuffer();
		BufferedReader br = null;
		try {
			FileInputStream fis = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(fis, charsetName);
			br = new BufferedReader(isr);
			int lineNum = 0;
			String lineContent = null;
			while ((lineContent = br.readLine()) != null) {
				++lineNum;
				if (lineNum > 1) {
					fileContent.append("\r\n");
				}
				fileContent.append(lineContent);
			}
		} finally {
			if(br != null) br.close();
		}
		return fileContent.toString();
	}
	
	/**
	 * 读取文件内容
	 * @param filePath 文件绝对路径
	 * @param charsetName 文件字符集
	 * @param callback 回调接口
	 * @throws IOException
	 */
	public static void read(String filePath, String charsetName, ReadCallback callback) throws IOException {
		
		File file = new File(filePath);
		if(!file.exists()) throw new FileNotFoundException();
		
		BufferedReader br = null;
		try {
			FileInputStream fis = new FileInputStream(file);
			InputStreamReader isr = new InputStreamReader(fis, charsetName);
			br = new BufferedReader(isr);
			String lineContent = null;
			while ((lineContent = br.readLine()) != null) {
				callback.readLine(lineContent);
			}
		} finally {
			if(br != null) br.close();
		}
	}
	
	/**
	 * 写内容到文件
	 * @param filePath 文件绝对路径
	 * @param charsetName 文件字符集
	 * @param isAppend 是否追加
	 * @param content 内容
	 * @throws IOException
	 */
	public static void write(String filePath, String charsetName, boolean isAppend, String content) throws IOException {
		
		File file = new File(filePath);
		if(!file.exists()) throw new FileNotFoundException();
		
		BufferedWriter bw = null;
		try {
			FileOutputStream fos = new FileOutputStream(file, isAppend);
			OutputStreamWriter  osw  = new OutputStreamWriter(fos, charsetName);
			bw = new BufferedWriter(osw);
			bw.write(content);
		} finally {
			if(bw != null) bw.close();
		}
	}

	/**
	 * 复制文件（此复制方法经实践是有问题的，后面需参考在中软增量部署 生成target目录 的copy方法重写下）
	 * @param srcFilePath
	 * @param targetFilePath
	 * @throws IOException
	 */
	public static void copy(String srcFilePath, String targetFilePath) throws IOException {
		
		File srcFile = new File(srcFilePath);
		File targetFile = new File(targetFilePath);
		
		if(!srcFile.exists()) throw new FileNotFoundException("源文件不存在");
		if(!targetFile.getParentFile().exists()) throw new IOException("目标文件保存目录不存在");
		
		FileInputStream fis=new FileInputStream(srcFile);
		FileOutputStream fos=new FileOutputStream(targetFile);
		BufferedInputStream bis=new BufferedInputStream(fis);
		BufferedOutputStream bos=new BufferedOutputStream(fos);
		int temp;
		while((temp = bis.read()) != -1){
			bos.write(temp);
		}
		fis.close();
		fos.close();
	}
	
	/**
	 * 剪切文件
	 * @param srcFilePath
	 * @param targetFilePath
	 * @return
	 * @throws IOException
	 */
	public static boolean cut(String srcFilePath, String targetFilePath) throws IOException {
		
		File srcFile = new File(srcFilePath);
		File targetFile = new File(targetFilePath);
		
		if(!srcFile.exists()) throw new FileNotFoundException("源文件不存在");
		if(!targetFile.getParentFile().exists()) throw new IOException("目标文件保存目录不存在");
		
		return srcFile.renameTo(targetFile);
	}
	
	/**
	 * 回调接口
	 */
	public interface ReadCallback{
		public void readLine(String lineContent);
	}
}