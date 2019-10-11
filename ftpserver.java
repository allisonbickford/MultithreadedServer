import java.io.*; 
import java.net.*;
import java.util.*;


class FTPServer {

    public static void main(String[] args){  // TODO: put stuff here
	
		String fromClient;
		String clientCommand;
		byte[] data;
		int port;
		
		
		

		ServerSocket welcomeSocket = new ServerSocket(12000);
		String frstln;
		
		while(true) {
			Socket connectionSocket = welcomeSocket.accept();
					
			DataOutputStream  outToClient = new DataOutputStream(connectionSocket.getOutputStream());
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
		
			fromClient = inFromClient.readLine();

			StringTokenizer tokens = new StringTokenizer(fromClient);
			frstln = tokens.nextToken();
			port = Integer.parseInt(frstln);
			clientCommand = tokens.nextToken();

			if(clientCommand.equals("list:")) { 

			Socket dataSocket = new Socket(connectionSocket.getInetAddress(), port);
			DataOutputStream  dataOutToClient = new DataOutputStream(dataSocket.getOutputStream()); 
			
			File folder = new File(".");
			File[] fileList = folder.listFiles();
			BufferedReader br = new BufferedReader();
			dataOutToClient.write(data);
			// TODO: send data to client

			dataSocket.close();
			System.out.println("Data Socket closed");
			}

			

		// TODO: ??????????????????????????????????????????????????
			
			if(clientCommand.equals("retr:")) {
				Socket dataSocket = new Socket(connectionSocket.getInetAddress(), port);
				DataOutputStream  dataOutToClient = new DataOutputStream(dataSocket.getOutputStream()); 
				
				File myFile = new File(tokens.nextToken());
				data = new byte[(int) myFile.length() + 1];
				FileInputStream fis = new FileInputStream(myFile);
				BufferedInputStream bis = new BufferedInputStream(fis);
				bis.read(data, 0, data.length);
				
	
				outToClient.write(data, 0, data.length);
				outToClient.flush();
				
				dataSocket.close();
				System.out.println("Data Socket closed");
			}
		}

	}
}
