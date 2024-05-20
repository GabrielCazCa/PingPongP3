package com.example.pingpongp3;

import java.io.*;
import java.net.*;
import java.util.*;

public class Server {
    private static final int PORT = 12345;
    private static Set<ServerThread> clientThreads =new HashSet<>();

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)){
            System.out.println("Servidor iniciado en el puerto: "+ PORT);

            while(true){
                Socket clientSocket =serverSocket.accept();
                ServerThread thread = new ServerThread(clientSocket);
                clientThreads.add(thread);
                thread.start();
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    static void broadcast(String message, ServerThread excludeThread){
        for(ServerThread thread : clientThreads){
            if (thread != excludeThread){
                thread.sendMessage(message);
            }
        }
    }

    static class ServerThread extends Thread{
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;

        ServerThread(Socket socket){
            this.socket = socket;
        }
        public void run(){
            try{
                out =new PrintWriter(socket.getOutputStream(),true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                String message;
                while((message = in.readLine()) != null){
                    System.out.println("Recivido: " + message);
                    Server.broadcast(message, this);
                }
            } catch(IOException e){
                e.printStackTrace();
            } finally {
                try{
                    socket.close();
                } catch (IOException e){
                    e.printStackTrace();
                }
                clientThreads.remove(this);
            }
        }

        void sendMessage(String message){
            System.out.println(message);
        }
    }
}