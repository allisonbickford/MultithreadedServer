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
        String modifiedSentence = "";
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
        int nextPort = 1668;


        if (sentence.startsWith("connect")) {
            tokens.nextToken(); // ignore connect
            serverName = tokens.nextToken(); // pass the connect command
            port = Integer.parseInt(tokens.nextToken());
        }

        Socket ControlSocket = new Socket(serverName, port);
        System.out.println("You are connected to " + serverName);
        DataOutputStream outToServer = new DataOutputStream(ControlSocket.getOutputStream());
        DataInputStream inFromServer = new DataInputStream(new BufferedInputStream(ControlSocket.getInputStream()));

        while (isOpen && clientgo) {
            System.out.println("\nWhat would you like to do next: \n list: || retr: file.txt || stor: file.txt || close");

            sentence = inFromUser.readLine();

            port = port + 2;
            ServerSocket welcomeData = new ServerSocket(port);
            outToServer.writeBytes(port + " " + sentence + " " + '\n');
            Socket dataSocket = welcomeData.accept();
            modifiedSentence = "";
            notEnd = true;
            if (sentence.equals("list:")) {

                try (
                        DataInputStream inData = new DataInputStream(new BufferedInputStream(dataSocket.getInputStream()))
                ) {
                    System.out.println("\n-----List of Files-------\n");
                    while (notEnd) {
                        modifiedSentence = inData.readUTF();
                        System.out.println(modifiedSentence + "\n");
                        if (inData.available() == 0) {
                            notEnd = false;
                        }
                    }
                    inData.close();
                } catch (IOException e) {
                    System.out.println("No files :( or didnt work idk:" + e);
                }

                dataSocket.close();
                welcomeData.close();
            } else if (sentence.startsWith("retr: ")) {
                String serverStatus = inFromServer.readUTF();
                if (serverStatus.startsWith("550")) {
                    System.out.println("File not found on server.");
                } else if (serverStatus.startsWith("200")) {
                    try {
                        DataInputStream inData = new DataInputStream(new BufferedInputStream(dataSocket.getInputStream()));

                        File newfile = new File(sentence.replace("retr: ", ""));

                        System.out.println("Downloading file...");
                        FileOutputStream fos = new FileOutputStream(newfile);
                        int letter = -1;
                        while ((letter = inData.read()) != -1) {
                            fos.write((char) letter);
                        }
                        System.out.println("File successfully downloaded!");
                    } catch (IOException e) {
                        System.out.println(e);
                    }
                }
                dataSocket.close();
                welcomeData.setReuseAddress(true);
                welcomeData.close();
            } else if (sentence.startsWith("stor: ")) {
                String str = sentence;
                String[] splitStr = str.split("\\s+");
                String fileName = splitStr[1];

                DataOutputStream outData = new DataOutputStream(new BufferedOutputStream(dataSocket.getOutputStream()));

                File file = new File(fileName);
                FileInputStream fis = new FileInputStream(fileName);
                BufferedInputStream bis = new BufferedInputStream(fis);

                byte[] fileData = new byte[(int) file.length()];
                bis.read(fileData, 0, (int) file.length());

                outData.write(fileData);

                bis.close();
                fis.close();
                outData.close();
                welcomeData.close();
                dataSocket.close();
            } else if (sentence.equals("close")) {
                ControlSocket.close();
                System.out.println("Closing connection.");
                isOpen = false;
                break;
            }
        }
    }
}