import java.io.*; 
import java.net.*;
import java.util.*;


class FTPServer {

    public static void main(String argv[]) throws Exception {
        ServerSocket welcomeSocket = new ServerSocket(12000, 0, InetAddress.getLoopbackAddress());
        int port = 12000;
        System.out.println("Listening at " + welcomeSocket.getInetAddress() + ":" + port);
        
        while(true) {
            Socket connectionSocket = welcomeSocket.accept();
            System.out.println("Victim at: " + connectionSocket.getPort());
            new Thread(new ClientHandler(connectionSocket)).start();
        }
    }
}

class ClientHandler implements Runnable {
    private Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override 
    public void run() {
        String fromClient;
        String clientCommand;
        byte[] data;
        String frstln;
        int port = 8000;
		String fileName="";
        try {
            DataOutputStream  outToClient = new DataOutputStream(this.socket.getOutputStream());
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
        
            fromClient = inFromClient.readLine();

            System.out.println(fromClient);
            StringTokenizer tokens = new StringTokenizer(fromClient);
            frstln = tokens.nextToken();
            port = Integer.parseInt(frstln);
            clientCommand = tokens.nextToken();
            Socket dataSocket = new Socket(this.socket.getInetAddress(), port);
            DataOutputStream  dataOutToClient = new DataOutputStream(dataSocket.getOutputStream());

            if(clientCommand.equals("list:")) { 
            // TODO: send data to client


            } else if (clientCommand.startsWith("retr:")) {
                fileName = tokens.nextToken();
                BufferedReader br = new BufferedReader(new FileReader(fileName));
                int letter = 0;
                while((letter = br.read()) != -1) {
                    System.out.println(letter);
                    outToClient.write(letter);
                }
                System.out.println("retrieving file...");

            }else if(clientCommand.startsWith("stor:")){

			fileName = tokens.nextToken();
			System.out.println("storing file..." + fileName);

			//Socket dSocket = new Socket(this.socket.getInetAddress(), port);

			 DataInputStream inData = new DataInputStream(new BufferedInputStream (dataSocket.getInputStream()));
			
			 byte[] fileData = new byte[1024];
			
			  String getDir = System.getProperty("user.dir");
			  FileOutputStream outputStream = new FileOutputStream(getDir+ "/"+ fileName);
			  inData.read(fileData);
			  outputStream.write(fileData);
			  System.out.println("file stored... "+fileName);
			  //dSocket.close();
			  inData.close();
			  outputStream.close();

			}

            dataSocket.close();
            System.out.println("Data Socket closed");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
    