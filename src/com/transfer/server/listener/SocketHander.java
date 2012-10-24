package com.transfer.server.listener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;


public class SocketHander {
	/**
	 * launch receive file thread
	 * @param socket
	 */
	public static  void dataHandler(final Socket socket){
		try{
			Runnable runnable = new Runnable(){
				private DataInputStream in = null;
				private DataOutputStream out = null;
				
				public void run() {
					// TODO Auto-generated method stub
					try{
						in = new DataInputStream(socket.getInputStream());
						out = new DataOutputStream(socket.getOutputStream());
							
						readStream(in);
						
						socket.close();
					
					}catch(Exception ex){
						ex.fillInStackTrace();
						try {
							socket.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
						}
					}
				}
			};
			
			(new Thread(runnable)).start();
		}catch(Exception ex){
			System.out.println("server: " + ex.fillInStackTrace());
			System.out.println("server: " + ex.getMessage());
		}
	}
	
	/**
	 * read Socket stream And write stream to file
	 * @param in
	 * @param fileOut
	 * @throws IOException
	 */
	private static void readStream(DataInputStream in) throws IOException{
		boolean isSuccess = false;
		
		//read file information
		String fileInfo = in.readUTF();
		String[] fileInfoArr = fileInfo.split(",");
		if(fileInfoArr.length < 3)
			return;
				
		String filename = fileInfoArr[1];
		double filesize = Float.parseFloat(fileInfoArr[2]);
				
		System.out.println("filename : " + filename);
				
		File folder = new File("/home/roy_luo/Roy/photo/Roy_Luo");
		if(!folder.exists())
			folder.mkdirs();
				
		File file = new File("/home/roy_luo/Roy/photo/Roy_Luo/" + filename);
		if(!file.exists()){
			if(!file.createNewFile())
				return;
		}
		
		OutputStream fileOut = null;
		
		try{
			fileOut = new FileOutputStream(file);
			
			//read file
			int readCompletedCount;
			int MaxOpacity = 1024; //default: 1k; >10M : 1M; > 100M : 10M
			if(filesize > 1024*1024*100)
	        	MaxOpacity *= 1024*10;
	        else if(filesize > 1024*1024*10)
	        	MaxOpacity *= 1024;
			
			byte[] bytes = new byte[MaxOpacity];
			int readCount = 0;
			double totalBytes = 0.0;
	
			while ((readCompletedCount = in.read(bytes, readCount, MaxOpacity - readCount)) != -1) {
				readCount += readCompletedCount;
				totalBytes+=readCompletedCount;
				if(readCount == MaxOpacity){
					fileOut.write(bytes);
					fileOut.flush();
					
					System.out.println(bytes);
					
					readCount = 0;
				}
				
				if(readCount < MaxOpacity && totalBytes == filesize){
					System.out.println(bytes);
				}
				
				if(totalBytes >= filesize)
					break;
			}
		
			fileOut.close();

			isSuccess = true;
			
			readStream(in);
			
		}catch(Exception e){
			fileOut.close();
			
			if(!isSuccess)
				file.delete();
		}
	}
	
	/*
	 * get deskTop path 
	 * @return
	 */
	private static String getDeskTopPath(){
		javax.swing.filechooser.FileSystemView fsv = javax.swing.filechooser.FileSystemView.getFileSystemView();
		return fsv.getHomeDirectory().getAbsolutePath(); 
	}
}
