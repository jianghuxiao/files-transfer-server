package com.transfer.server;

import java.net.ServerSocket;
import java.net.Socket;

import com.util.Config;
import com.util.LogTool;

/**
 * server
 * @author Roy_Luo
 *
 */
public class Server {
	/**
	 * start listener
	 */
	public static void start(){
		try{
			ServerSocket ss = new ServerSocket(Config.SERVER_PORT);
			while(true){
				Socket socket = ss.accept();
				
				SocketHander sh = new SocketHander();
				sh.dataHandler(socket);
			}
		}catch(Exception ex){
			LogTool.printException(ex);
		}
	}
	
}
