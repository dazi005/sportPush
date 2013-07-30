package com.zyy.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.os.Environment;

public class FileUtil {

	private String SDPATH;

	public String getSDPATH() {
		return SDPATH;
	}

	public void setSDPATH(String sDPATH) {
		SDPATH = sDPATH;
	}

	public FileUtil() {
		//�õ���ǰ�ⲿ�洢�豸��Ŀ¼
		// /SDCARD
		SDPATH = Environment.getExternalStorageDirectory() + "/";
	}
	/**
	 * ��SD���ϴ����ļ�
	 * 
	 * @throws IOException
	 */
	public File creatSDFile(String fileName,String path) throws IOException {
		File file = new File(SDPATH +path+"/"+ fileName);
		file.createNewFile();
		return file;
	}
	/**
	 * ��SD���ϴ���Ŀ¼
	 * 
	 * @param dirName
	 */
	public File creatSDDir(String dirName) {
		File dir = new File(SDPATH + dirName);
		dir.mkdir();
		return dir;
	}

	/**
	 * �ж�SD���ϵ��ļ����Ƿ����
	 */
	public  boolean isFileExist(String fileName){
		File file = new File(SDPATH + fileName);
		return file.exists();
	}
	/**
	 * ��һ��InputStream���������д�뵽SD����
	 */
	public File write2SDFromInput(String path,String fileName,InputStream input){
		
		File file = null;
		OutputStream output = null;
		try{
			if(!isFileExist(path)){
				creatSDDir(path);
			}			
			file = creatSDFile(fileName,path);
			output = new FileOutputStream(file);
			byte buffer [] = new byte[1024];
			
			System.out.println("is read==="+((input.read(buffer)) == -1));
			while((input.read(buffer)) != -1){
				output.write(buffer);
			}
			output.flush();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		finally{
			try{
				output.close();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		return file;
	}
	
	
	
	
	public void write2SDCard(InputStream inputStream,String path,String filename) throws Exception{
    	boolean debug = false;
    	
    	// ����һ���ļ����Ա�����SD��
    	String savePAth = Environment.getExternalStorageDirectory()
    			+ "/SportsDaily/";
    	File tFile = new File(savePAth);
    	//FileOutputStream fos = new FileOutputStream(tFile);
    	if (!tFile.exists()) {
    		tFile.mkdirs();
    	}

    	File file = new File(savePAth+filename);
    	if(!file.exists()){
    		file.createNewFile();
    	}
    	
    	if (debug) {
    		System.out.println("׼����ʼд��"); //��ӡһ��
    	}
    	// ��ʼд���ļ�
    	int len = 0;
    	int nowSize = 0;
    	byte[] buffer = new byte[1024];
    	ByteArrayOutputStream bos = new ByteArrayOutputStream();

    	while ((len = inputStream.read(buffer)) != -1) {
    		if (debug) {
    			System.out.println("д��"); //��ӡһ��
    		}
    		bos.write(buffer, 0, len);
    		bos.flush();
    		
    	}
    	
    	
    	//д��SD��
    	FileOutputStream fos = new FileOutputStream(Environment.getExternalStorageDirectory()
    			+ "/"+path+"/"+filename);
    	fos.write(bos.toByteArray());
    	fos.flush();
    	fos.close();
    }
	
	
	
}
