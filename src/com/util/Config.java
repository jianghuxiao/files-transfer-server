package com.util;

public class Config {
	
	public static final int SERVER_PORT = 23056; //server port
	
	public static String getSavePath(){
		return getDeskTopPath();
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
