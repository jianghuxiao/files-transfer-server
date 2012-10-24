import java.net.ServerSocket;
import java.net.Socket;


public class App {
	public static void main(String[] args){
		try{
			ServerSocket ss = new ServerSocket(23056);
			while(true){
				Socket socket = ss.accept();
				SocketManager.launchReceiveThread(socket);
			}
		}catch(Exception ex){
			ex.fillInStackTrace();
			ex.getStackTrace();
		}
	}	
}
