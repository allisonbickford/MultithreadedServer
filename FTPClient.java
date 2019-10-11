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
        String modifiedSentence; 
        boolean isOpen = true;
        int number = 1;
        boolean notEnd = true;
	String statusCode;
	boolean clientgo = true;
	    
	
	BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in)); 
        sentence = inFromUser.readLine();
        StringTokenizer tokens = new StringTokenizer(sentence);


        String serverName = null;
        int port = 12000;

	if(sentence.startsWith("connect")) {
                tokens.nextToken(); // ignore connect
                serverName = tokens.nextToken(); // pass the connect command
                port = Integer.parseInt(tokens.nextToken());
                System.out.println("You are connected to " + serverName);
        }

	Socket ControlSocket = new Socket(serverName, port);
        
	while(isOpen && clientgo) {      
	      
          DataOutputStream outToServer = new DataOutputStream(ControlSocket.getOutputStream()); 
          
	  DataInputStream inFromServer = new DataInputStream(new BufferedInputStream (ControlSocket.getInputStream()));
          
    	  sentence = inFromUser.readLine();
                
                port = port + 2;
                outToServer.writeBytes (port + " " + sentence + " " + '\n');
                
                ServerSocket welcomeData = new ServerSocket(port);
                Socket dataSocket = welcomeData.accept(); 

                DataInputStream inData = new DataInputStream(new BufferedInputStream (dataSocket.getInputStream()));
                while(notEnd) {
                    modifiedSentence = inData.readUTF();
                    if (inData.available() == 0) {
                        notEnd = false;
                    }
                }
                

                welcomeData.close();
                dataSocket.close();
                System.out.println("\nWhat would you like to do next: \n retr: file.txt ||stor: file.txt  || close");

                if(sentence.equals("list:")) {
                    //connects to a the server to get the list //idk what port
                    ServerSocket serverList = new ServerSocket(port+2);
                    //idk if i can reuse this socket 
                    dataSocket = serverList.accept();
                    //gets the data from that socket:
                    DataInputStream din = new DataInputStream(new BufferedInputStream(dataSocket.getInputStream()));
                    //for the files 
                    String file = din.readUTF();
                    int filesCount = din.readInt();
                    //parse through the files 
                    int i =0;
                    while(i<filesCount){
                        system.out.println(file);
                        file = din.readUTF();
                    }
    
                    serverList.close();
                    dataSocket.close();
                } else if (sentence.startsWith("retr:")) {
                    // TODO: retrive file
                    String fileName = tokens.nextToken();
    
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
                } else if (sentence.startsWith("stor: ")) {
                    // TODO: store file
                } else if (sentence.equals("close")) {
                    ControlSocket.close();
                    isOpen = false;
                }
        }
}
}
	
