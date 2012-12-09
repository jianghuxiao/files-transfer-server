package com.transfer.server;

import java.net.ServerSocket;
import java.net.Socket;

import com.util.Config;

public class Server {
	/**
	 * start listener
	 */
	public static void start(){
		try{
			
			ServerSocket ss = new ServerSocket(Config.SERVER_PORT);
			while(true){
				Socket socket = ss.accept();
				SocketHander.dataHandler(socket);
			}
			
		}catch(Exception ex){
			ex.fillInStackTrace();
			ex.getStackTrace();
		}
	}
}
