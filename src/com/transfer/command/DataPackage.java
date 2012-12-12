package com.transfer.command;

import com.util.Config;

public class DataPackage {
	public static String createResponseInfo(int cmd, String message){
		if(message == null)
			message = "null";
		
		return cmd + Config.DELIMITER + message;
	}
}
