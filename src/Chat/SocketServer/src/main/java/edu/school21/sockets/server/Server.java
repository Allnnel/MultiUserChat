package edu.school21.sockets.server;

import edu.school21.sockets.config.SocketsApplicationConfig;
import edu.school21.sockets.services.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Server {
  @Autowired
  public Server(int port) {
    AnnotationConfigApplicationContext applicationContext =
        new AnnotationConfigApplicationContext();
    applicationContext.register(SocketsApplicationConfig.class);
    applicationContext.refresh();
    UsersService usersService = applicationContext.getBean(UsersServiceImpl.class);
    MessageService messageService = applicationContext.getBean(MessageServiceImpl.class);
    ChatService chatService = applicationContext.getBean(ChatServiceImpl.class);
    try {
      ServerSocket serverSocket = new ServerSocket(port);
      while (true) {
        Socket clientSocket = serverSocket.accept();
        ClientHandler clientHandler =
            new ClientHandler(clientSocket, messageService, usersService, chatService);
        Thread clientThread = new Thread(clientHandler);
        clientThread.start();
      }
    } catch (IOException e) {
      System.err.println("Could not listen on port " + port);
      System.exit(-1);
    }
  }
}
