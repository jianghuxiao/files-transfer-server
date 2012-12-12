package com.util;

public class Config {
	
	public static final int RECEIVER_PORT = 23056; //server port
	public static final int COMMAND_PORT = 23057; //server port
	
	public static final String DELIMITER = ",";
	
	/**
	 * get save path
	 * @return
	 */
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
