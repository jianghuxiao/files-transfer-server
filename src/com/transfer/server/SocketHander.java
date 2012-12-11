package com.transfer.server;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URLEncoder;

import com.util.Config;


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
						
						System.out.println("===========Close Socket");
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
			ex.fillInStackTrace();

			System.out.println("server: " + ex.getMessage());
		}
	}
	
	/**
	 * read Socket stream And write stream to file
	 * @param in
	 * @param fileOut
	 * @throws IOException
	 */
	private static void readStream(DataInputStream in){
		try{
			boolean isSuccess = false;
			
			//read file information
			System.out.println("================ Start");

			String fileInfo = in.readUTF();
			System.out.println("fileInfo : " + fileInfo);
			String[] fileInfoArr = fileInfo.split(",");
			if(fileInfoArr.length < 3){
				System.out.println("==================================fileInfo : " + fileInfo);
				return;
			}
					
			String filename = fileInfoArr[1];
			long filesize = Long.parseLong(fileInfoArr[2]);
	
			String savePath = Config.getSavePath() + "/roy/";
			File folder = new File(savePath);
			if(!folder.exists())
				folder.mkdirs();
					
			File file = new File(savePath + filename);
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
				int totalBytes = 0;
				int readLen = bytes.length;
				if(filesize - totalBytes < readLen)
					readLen = (int)(filesize - totalBytes);
				while ((readCompletedCount = in.read(bytes, 0, readLen)) > 0) 
				{
					totalBytes += readCompletedCount;
					fileOut.write(bytes,0,readCompletedCount);
					fileOut.flush();
					
					if(filesize - totalBytes < readLen)
						readLen = (int)(filesize - totalBytes);
					
					if(totalBytes == filesize)
						break;
				}
			
				fileOut.close();
				isSuccess = true;
				
				readStream(in);
			}catch(Exception e){
				System.out.println("Error1: " + e.getMessage());
				
				fileOut.close();
				
				if(!isSuccess)
					file.delete();
			}
		}catch(Exception ex){
			ex.fillInStackTrace();
			System.out.println("Error2: " + ex.getMessage());
		}
	}

}
