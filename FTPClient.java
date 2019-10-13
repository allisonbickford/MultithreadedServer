import java.io.*; 
import java.net.*;
import java.util.*;
import java.text.*;
import java.lang.*;
import javax.swing.*;

class FTPClient { 

    public static void main(String argv[]) throws Exception { 

        System.out.println("Welcome to the server! To connect, enter \"connect 127.0.0.1 12000\"");

        String sentence; 
        String modifiedSentence=""; 
        boolean isOpen = true;
        int number = 1;
        boolean notEnd = true;
		String statusCode;
		boolean clientgo = true;
	    String fileName="";
	
	BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in)); 
        sentence = inFromUser.readLine();
        StringTokenizer tokens = new StringTokenizer(sentence);


        String serverName = null;
        int port = 12000;
		int nextPort = 1668;


	if(sentence.startsWith("connect")) {
                tokens.nextToken(); // ignore connect
                serverName = tokens.nextToken(); // pass the connect command
                port = Integer.parseInt(tokens.nextToken());
                System.out.println("You are connected to " + serverName);
        }

	Socket ControlSocket = new Socket(serverName, port);
        DataOutputStream outToServer = new DataOutputStream(ControlSocket.getOutputStream()); 
		DataInputStream inFromServer = new DataInputStream(new BufferedInputStream (ControlSocket.getInputStream()));
          
	while(isOpen && clientgo) {      
	 
    			sentence = inFromUser.readLine();
				modifiedSentence= "";
           if(sentence.equals("list:")) {
				  nextPort =  port + 2;
				 outToServer.writeBytes (nextPort + " " + sentence + " " + '\n');
                ServerSocket welcomeData = new ServerSocket(nextPort);
                Socket dataSocket = welcomeData.accept(); 

				try(
                DataInputStream inData = new DataInputStream(new BufferedInputStream (dataSocket.getInputStream()))
               ){
			   System.out.println("\n-----List of Files-------\n");
			   while(notEnd) {
                    modifiedSentence = inData.readUTF();
					System.out.println(modifiedSentence);
                    if (inData.available() == 0) {
                        notEnd = false;
                    }
				 }
				 inData.close();
				}
				catch(IOException e){
				System.out.println("No files :( or didnt work idk:"+ e);
				}
				
                welcomeData.close();
                dataSocket.close();

                System.out.println("\nWhat would you like to do next: \n retr: file.txt ||stor: file.txt  || close");
                } else if (sentence.startsWith("retr:")) {

				nextPort = port +2;
				outToServer.writeBytes (nextPort + " " + sentence + " " + '\n');
                
                ServerSocket welcomeData = new ServerSocket(nextPort);
                Socket dataSocket = welcomeData.accept(); 

				try(
                DataInputStream inData = new DataInputStream(new BufferedInputStream (dataSocket.getInputStream()))
               ){
			  // TODO: retrive file
                    fileName = tokens.nextToken();
    
                    // Prepend a "." so that file request is within the current directory.
                    fileName = '.' + fileName;
    
                    // Open the requested file.
                    FileInputStream fis = null;
    
                    boolean fileExists = true ;
                    try {
                        fis = new FileInputStream(fileName);
                    } catch (FileNotFoundException e) {
                        fileExists = false ;
                    }
                    // Debug info for private use
                    String line = null;
                    while ((line = inFromServer.readLine()).length() != 0) {
                        System.out.println(line);
                    }
                    if (fileExists) {
                        System.out.println(fileName);
                    } else {
                        System.out.println("We don't have that file!");
                    }
                    welcomeData.close();
                dataSocket.close();
				 
				}
				catch(IOException e){
				System.out.println(e);
				}
                    
                } else if (sentence.startsWith("stor:")) {
				String str = sentence;
				String[] splitStr = str.split("\\s+");
				fileName = splitStr[1];
				nextPort = port +2;
				outToServer.writeBytes (nextPort + " " + sentence + " " + '\n');
                
                ServerSocket welcomeData = new ServerSocket(nextPort);
                Socket dataSocket = welcomeData.accept(); 

				DataOutputStream outData = new DataOutputStream(new BufferedOutputStream (dataSocket.getOutputStream()));

				File file = new File(fileName);
				FileInputStream fis = new FileInputStream(fileName);
				BufferedInputStream bis = new BufferedInputStream(fis);

				byte[] fileData = new byte[(int)file.length()];
				bis.read(fileData,0,(int)file.length());

				outData.write(fileData);

				bis.close();
				fis.close();
				outData.close();
				welcomeData.close();
				dataSocket.close();
				
				System.out.println("\nWhat would you like to do next: \n list: || retr: file.txt ||stor: file.txt  || close");
                    // TODO: store file
                } else if (sentence.equals("close")) {

                    ControlSocket.close();
                    isOpen = false;
                }
        }
}
}
	