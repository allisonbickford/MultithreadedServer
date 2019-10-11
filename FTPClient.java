import java.io.*; 
import java.net.*;
import java.util.*;
import java.text.*;
import java.lang.*;
import javax.swing.*;
class FTPClient { 

    public static void main(String argv[]) throws Exception { 
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
        Integer port = 0;
	if(sentence.startsWith("connect")) {
                serverName = tokens.nextToken(); // pass the connect command
                port = Integer.parseInt(tokens.nextToken());
                System.out.println("You are connected to " + serverName);
        }

	Socket ControlSocket = new Socket(serverName, port);
        
	while(isOpen && clientgo) {      
	      
          DataOutputStream outToServer = new DataOutputStream(ControlSocket.getOutputStream()); 
          
	  DataInputStream inFromServer = new DataInputStream(new BufferedInputStream (ControlSocket.getInputStream()));
          
    	  sentence = inFromUser.readLine();
	   
                if(sentence.equals("list:")) {
                
                port = port + 2;
                outToServer.writeBytes (port + " " + sentence + " " + '\n');
                
                ServerSocket welcomeData = new ServerSocket(port);
                Socket dataSocket = welcomeData.accept(); 

                DataInputStream inData = new DataInputStream(new BufferedInputStream (dataSocket.getInputStream()));
                while(notEnd) {
                        modifiedSentence = inData.readUTF();
                        notEnd = false;
                // TODO: read data
                }
                

                        welcomeData.close();
                        dataSocket.close();
                        System.out.println("\nWhat would you like to do next: \n retr: file.txt ||stor: file.txt  || close");

                } else if (sentence.startsWith("retr: ")) {
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
	
