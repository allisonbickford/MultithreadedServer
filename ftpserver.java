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
				int byteSize = 0;
				for(int i = 0; i < fileList.length; i++){
					byteSize += (fileList[i].length() +1);
				}
				data = new byte[byteSize];
				
				int offset = 0;
				
				for(int i = 0; i < fileList.length; i++){
					FileInputStream fis = new FileInputStream(fileList[i]);
					BufferedInputStream bis = new BufferedInputStream(fis);
					if(i == 0){
						offset = 0;
					}
					else{
						offset = (int)fileList[i-1].length();
					}
					bis.read(data,offset,data.length);
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
