import java.io.*; 
import java.net.*;
import java.util.*;


public class ftpserver {

    public static ServerSocket welcomeSocket;    
    public static void main(String[] args) throws IOException{  // TODO: put stuff here
	
		String fromClient;
		String clientCommand;
		byte[] data;
		int port;
		
			
		try{
			welcomeSocket = new ServerSocket(12000);
		}
		catch (IOException ioEx){
			System.out.println("Unable to set up port");
			System.exit(1);
		}
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
			data = new byte[(int)fileList.length + 1];
			for(int i = 0; i < fileList.length; i++){
				FileInputStream fis = new FileInputStream(fileList[i]);
				BufferedInputStream bis = new BufferedInputStream(fis);
				bis.read(data,0,data.length);
			}
	
			dataOutToClient.write(data);
			// TODO: send data to client

			dataSocket.close();
			System.out.println("Data Socket closed");
			}

			

		// TODO: ??????????????????????????????????????????????????
			
			if(clientCommand.equals("retr:")) {
				// TODO: retrive
			}
		}

	}
}
