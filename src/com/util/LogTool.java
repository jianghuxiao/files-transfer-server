package com.util;

public class LogTool {
	public static void printException(Exception e){
		System.out.println(e.getMessage());
		e.printStackTrace();
	}
}
