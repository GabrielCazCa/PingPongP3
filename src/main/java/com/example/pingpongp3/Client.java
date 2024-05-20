package com.example.pingpongp3;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.io.*;
import java.net.Socket;

public class Client extends Application {
    private static final String SERVER_ADDRES = "localhost";
    private static final int SERVER_PORT = 12345;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private Canvas canvas;
    private GraphicsContext gc;

    private double playerY = 250;
    private double opponentY = 250;
    private double ballX = 300, ballY = 200;

    @Override
    public void start(Stage primaryStage) {
        canvas = new Canvas(800, 600);
        gc = canvas.getGraphicsContext2D();

        StackPane root = new StackPane();
        root.getChildren().add(canvas);

        Scene scene = new Scene(root, 800, 600);

        primaryStage.setTitle("Ping Pong P3");
        primaryStage.setScene(scene);
        primaryStage.show();

        canvas.setFocusTraversable(true);
        canvas.setOnKeyPressed(this::handleKeyPress);

        new Thread(this::connectToServer).start();
        new Thread(this::gameLoop).start();
    }

    private void connectToServer() {
        try{
            socket = new Socket(SERVER_ADDRES,SERVER_PORT);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String message;
            while((message = in.readLine())!=null){
                String[] parts = message.split(";");
                Platform.runLater(()->{
                    ballX = Double.parseDouble(parts[0]);
                    ballY = Double.parseDouble(parts[1]);
                    opponentY = Double.parseDouble(parts[2]);
                    drawGame();
                });
            }
        } catch(IOException e){
            e.printStackTrace();
        }
    }


    private void handleKeyPress(KeyEvent event) {
        switch (event.getCode()) {
            case UP -> playerY -= 10;
            case DOWN -> playerY += 10;
        }
        out.println(playerY);
    }

    private void gameLoop() {
        while(true){
            drawGame();
            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void drawGame() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gc.setFill(Color.BLACK);
        gc.fillRect(0, playerY, 10, 60);
        gc.fillRect(canvas.getWidth() - 10, opponentY, 10, 60);

        gc.setFill(Color.RED);
        gc.fillRect(ballX, ballY, 15, 15);
    }

    public static void main (String[]args){
        launch(args);
    }
}