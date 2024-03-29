package edu.school21.sockets.app;

import edu.school21.sockets.client.Client;

public class Main {
  public static void main(String[] args) {
    if (args.length == 1 && args[0].matches("^--server-port=\\d{1,5}$")) {
      Client client = new Client("127.0.0.1", Integer.parseInt(args[0].split("=")[1]));
      client.run();
    }
  }
}
