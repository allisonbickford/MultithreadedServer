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
	    
	
	    BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in)); 
        sentence = inFromUser.readLine();
        StringTokenizer tokens = new StringTokenizer(sentence);


        String serverName = null;
        int port = 12000;

	    if(sentence.startsWith("connect")) {
            tokens.nextToken(); // ignore connect
            serverName = tokens.nextToken(); // pass the connect command
            port = Integer.parseInt(tokens.nextToken());
        }

	    Socket ControlSocket = new Socket(serverName, 12000);
        System.out.println("You are connected to " + serverName);
        DataOutputStream outToServer = new DataOutputStream(ControlSocket.getOutputStream()); 
        DataInputStream inFromServer = new DataInputStream(new BufferedInputStream (ControlSocket.getInputStream()));
        
	    while(isOpen && clientgo) {
            sentence = inFromUser.readLine();
            port = port + 2;
            outToServer.writeBytes (port + " " + sentence + " " + '\n');
            if (sentence.equals("list:")) {
                
                ServerSocket welcomeData = new ServerSocket(port);
                Socket dataSocket = welcomeData.accept(); 
                try (
                    DataInputStream inData = new DataInputStream(new BufferedInputStream (dataSocket.getInputStream()))
                    ) {
                    System.out.println("\n-----List of Files-------\n");
                    while(notEnd) {
                        modifiedSentence = inData.readUTF();
                        System.out.println(modifiedSentence);
                        if (inData.available() == 0) {
                            notEnd = false;
                        }
                    }
                } catch(IOException e){
                    System.out.println("No files :( or didnt work idk:"+e);
                }
                
                dataSocket.close();	
                welcomeData.close();	
            } else if (sentence.startsWith("retr: ")) {
                ServerSocket welcomeData = new ServerSocket(port);
                Socket dataSocket = welcomeData.accept(); 
                
                String serverStatus = inFromServer.readUTF();
                if (serverStatus.startsWith("550")) {
                    System.out.println("File not found on server.");
                } else if (serverStatus.startsWith("200")) {
                    try {
                        DataInputStream inData = new DataInputStream(new BufferedInputStream(dataSocket.getInputStream()));

                        File newfile = new File(sentence.replace("retr: ", ""));
                        if (!newfile.exists()) {
                            System.out.println("Downloading file...");
                            FileOutputStream fos = new FileOutputStream(newfile);
                            int letter = -1;
                            while ((letter = inData.read()) != -1) {
                                fos.write((char) letter);
                            }
                            System.out.println("File successfully downloaded!");
                        } else {
                            System.out.println("File already exists.");
                        }
                    } catch(IOException e){
                        System.out.println(e);
                    }         
                }
                dataSocket.close();	
                welcomeData.close();	
            } else if (sentence.startsWith("stor: ")) {
                // TODO: store file
            } else if (sentence.equals("close")) {
                ControlSocket.close();
                System.out.println("Closing connection.");
                isOpen = false;
                break;
            } 
            System.out.println("\nWhat would you like to do next: \n retr: file.txt ||stor: file.txt  || close");
        }
    }
}
	