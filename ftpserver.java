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
        int port = this.socket.getPort();

        while (true) {
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
                DataOutputStream dataOutToClient = new DataOutputStream(dataSocket.getOutputStream());

                if(clientCommand.equals("list:")) { 
                    // TODO: send data to client
                } else if (clientCommand.startsWith("retr:")) {
                    String fileName = "./server/" + tokens.nextToken(); // files should be stored in server folder
                    File fileRequested = new File(fileName);
                    if (fileRequested.exists()) {
                        outToClient.writeUTF("200 OK");
                        BufferedReader reader = new BufferedReader(new FileReader(fileName));
                        int letter = -1;
                        while ((letter = reader.read()) != -1) {
                            dataOutToClient.writeByte(letter);
                        }
                    } else {
                        outToClient.writeUTF("550 Not Found");
                    }
                } else if(clientCommand.startsWith("stor:")){
                    String fileName = tokens.nextToken();
                    System.out.println("storing file..." + fileName);

                    DataInputStream inData = new DataInputStream(new BufferedInputStream (dataSocket.getInputStream()));
                    
                    FileOutputStream outputStream = new FileOutputStream("./server/" + fileName);
                    int letter = -1;
                    while ((letter = inData.read()) != -1) {
                        outputStream.write((char) letter);
                    }

                    System.out.println("file stored... " + fileName);
                    inData.close();
                    outputStream.close();
                } else if (clientCommand.equals("close")) {
                    dataSocket.close();
                    System.out.println("Connection ended with victim " + this.socket.getPort());
                    this.socket.close();
                    break;
                }
                dataSocket.close();
                System.out.println("Data Socket closed");
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("There was a problem, " +
                    "so we are closing the connection to victim " + this.socket.getPort());
                try {
                    this.socket.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                break;
            }
        }
    }

}
    