package com.transfer.server;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import com.util.Config;
import com.util.LogTool;

/**
 * socket handler
 * @author Roy_Luo
 *
 */
public class SocketHander {
	/**
	 * launch receive file thread
	 * @param socket
	 */
	public void dataHandler(final Socket socket){
		Runnable runnable = new Runnable(){
			
			private DataInputStream in = null;
			private DataOutputStream out = null;
				
			public void run() {
					// TODO Auto-generated method stub
				try{
					initStream(socket, in, out);
					handleStream(in);
						
					socket.close();
				}catch(Exception ex){
					try {
						socket.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						LogTool.printException(ex);
					}
					
					LogTool.printException(ex);
				}
			}
		};
			
		(new Thread(runnable)).start();
	}
	
	/**
	 * init stream
	 * @param in
	 * @param out
	 * @throws IOException 
	 */
	private void initStream(final Socket socket, DataInputStream in, DataOutputStream out) throws IOException{
		in = new DataInputStream(socket.getInputStream());
		out = new DataOutputStream(socket.getOutputStream());
	}
	
	/**
	 * read Socket stream And write stream to file
	 * @param in
	 * @param fileOut
	 * @throws IOException
	 */
	private void handleStream(DataInputStream in){
		try{
			String info = in.readUTF();
			HeadInfo headInfo = parseHeadInfo(info);
			if(headInfo == null)
				return;
	
			File file = initSaveFolderAndFile(headInfo.filename);
			if(file == null)
				return;
			
			OutputStream fileOut = null;
			try{
				fileOut = new FileOutputStream(file);
				readStreamAndWriteFile(in, fileOut, headInfo);

				handleStream(in);
			}catch(Exception e){
				LogTool.printException(e);
				
				fileOut.close();
				file.delete();
			}
		}catch(Exception ex){
			LogTool.printException(ex);
		}
	}
	
	private class HeadInfo{
		public String filename = null;
		public long filesize = 0;
		public String message = null;
	}
	
	/**
	 * parse head info
	 * @param headInfo
	 */
	private HeadInfo parseHeadInfo(String headInfo){
		HeadInfo info = new HeadInfo();
		
		System.out.println("fileInfo : " + headInfo);
		String[] fileInfoArr = headInfo.split(",");
		if(fileInfoArr.length < 3)
			return null;
				
		info.filename = fileInfoArr[0];
		info.filesize = Long.parseLong(fileInfoArr[1]);
		info.message = fileInfoArr[2];
		
		return info;
	}
	
	/**
	 * init save folder and file
	 * @param filename
	 * @throws IOException 
	 */
	private File initSaveFolderAndFile(String filename) throws IOException{
		String savePath = Config.getSavePath() + "/roy/";
		File folder = new File(savePath);
		if(!folder.exists())
			folder.mkdirs();
				
		File file = new File(savePath + filename);
		if(!file.exists()){
			if(!file.createNewFile())
				return null;
		}
		
		return file;
	}
	
	/**
	 * read stream and write file
	 * @param file
	 * @param headInfo
	 * @throws IOException 
	 */
	private void readStreamAndWriteFile(DataInputStream in, OutputStream fileOut, HeadInfo headInfo) throws IOException{

		int bufferCapacity = 1024; //default: 1k; >10M : 1M; > 100M : 10M
		int off = bufferCapacity;
		int readSize;
		int readTotalSize = 0;
        long fileSize = headInfo.filesize;
		
		if(fileSize > 1024*1024*100)
        	bufferCapacity *= 1024*10;
        else if(fileSize > 1024*1024*10)
        	bufferCapacity *= 1024;
		
		byte[] bytes = new byte[bufferCapacity];

		if(fileSize - readTotalSize < off)
			off = (int)(fileSize - readTotalSize);
		
		while ((readSize = in.read(bytes, 0, off)) > 0) 
		{
			readTotalSize += readSize;
			fileOut.write(bytes,0,readSize);
			fileOut.flush();
			
			if(fileSize - readTotalSize < off)
				off = (int)(fileSize - readTotalSize);
			
			if(readTotalSize == fileSize)
				break;
		}
	
		fileOut.close();
	}

}
