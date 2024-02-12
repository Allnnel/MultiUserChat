package edu.school21.sockets.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
  private final String serverAddress;
  private final int serverPort;
  private Socket socket;
  private PrintWriter printWriter;
  private BufferedReader in;
  private BufferedReader reader;
  private volatile boolean isProgramRunning = true;

  public Client(String address, int port) {
    this.serverPort = port;
    this.serverAddress = address;
  }

  public void run() {
    try {
      socket = new Socket(serverAddress, serverPort);
      printWriter = new PrintWriter(socket.getOutputStream(), true);
      in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      reader = new BufferedReader(new InputStreamReader(System.in));
      Thread receiveThread = receiveThread();
      Thread sendThread = sendThread();
      receiveThread.start();
      sendThread.start();
      receiveThread.join();
      sendThread.join();
      stop();

    } catch (IOException e) {
      System.err.println("Error connecting to the server: " + e.getMessage());
    } catch (InterruptedException e) {
      System.err.println("Interrupted while waiting for threads to finish: " + e.getMessage());
    }
  }

  private Thread sendThread() {
    return new Thread(
        () -> {
          try {
            String message;
            while (isProgramRunning) {
              if (reader.ready() && (message = reader.readLine()) != null) {
                printWriter.println(message);
                if (message.equals("exit")) {
                  isProgramRunning = false;
                  break;
                }
              }
              Thread.sleep(100);
            }
          } catch (IOException e) {
            System.err.println("Error reading input: " + e.getMessage());
          } catch (InterruptedException e) {
            System.err.println("Error sleeping thread: " + e.getMessage());
          }
        });
  }

  private Thread receiveThread() {
    return new Thread(
        () -> {
          try {
            String response;
            while (isProgramRunning && (response = in.readLine()) != null) {
              System.out.println(response);
              if (response.equals("Successful!")
                  || response.equals("The user with this login is busy!")
                  || response.equals("User not found!")
                  || response.equals("You have left the chat.")
                  || response.equals("Chatroom not found.")
                  || !isProgramRunning) {
                isProgramRunning = false;
                break;
              }
            }
          } catch (IOException e) {
            if (!socket.isClosed()) {
              throw new RuntimeException(e);
            }
          }
        });
  }

  public void stop() {
    try {
      if (printWriter != null) {
        printWriter.close();
      }
      if (in != null) {
        in.close();
      }
      if (reader != null) {
        reader.close();
      }
      if (socket != null) {
        socket.close();
      }
    } catch (IOException e) {
      System.err.println("Error while closing resources: " + e.getMessage());
    }
  }
}
