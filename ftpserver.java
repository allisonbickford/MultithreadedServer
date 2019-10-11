import java.io.*; 
import java.net.*;
import java.util.*;


class FTPServer {

    // TODO: put stuff here
    public static void main(String argv[]) throws Exception {
	
    String fromClient;
    String clientCommand;
    byte[] data;
    

    ServerSocket welcomeSocket = new ServerSocket(12000);
    String frstln;
    int port = 12000;
    System.out.println("Running on port" + port);
    
    while(true) {
        Socket connectionSocket = welcomeSocket.accept();
                
        DataOutputStream  outToClient = new DataOutputStream(connectionSocket.getOutputStream());
        BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
    
        fromClient = inFromClient.readLine();

        StringTokenizer tokens = new StringTokenizer(fromClient);
        frstln = tokens.nextToken();
        port = Integer.parseInt(frstln);
        clientCommand = tokens.nextToken();
        Socket dataSocket = new Socket(connectionSocket.getInetAddress(), port);
        DataOutputStream  dataOutToClient = new DataOutputStream(dataSocket.getOutputStream());

        if(clientCommand.equals("list:")) { 
        // TODO: send data to client


        } else if (clientCommand.startsWith("retr:")) {
            String fileName = tokens.nextToken();
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            int letter = 0;
            while((letter = br.read()) != -1) {
                System.out.println(letter);
                outToClient.write(letter);
            }
        }

        dataSocket.close();
        System.out.println("Data Socket closed");
    }

    // TODO: condition here
        
        // if(clientCommand.equals("retr:")) {
        //     // TODO: implement retrieve
        // }

    }
}
    