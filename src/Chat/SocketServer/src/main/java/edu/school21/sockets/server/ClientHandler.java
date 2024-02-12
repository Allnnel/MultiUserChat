package edu.school21.sockets.server;

import edu.school21.sockets.models.Chat;
import edu.school21.sockets.models.Message;
import edu.school21.sockets.models.User;
import edu.school21.sockets.services.ChatService;
import edu.school21.sockets.services.MessageService;
import edu.school21.sockets.services.UsersService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private final MessageService messageService;
    private final UsersService usersService;
    private final ChatService chatService;
    private String userName = null;
    private String userPassword = null;
    private User user;
    private Chat chat;
    Long charId;

    private LocalDateTime lastMessageTime;
    public ClientHandler(Socket socket, MessageService messageService, UsersService usersService, ChatService chatService) {
        this.clientSocket = socket;
        this.messageService = messageService;
        this.usersService = usersService;
        this.chatService = chatService;
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out.println("Hello from Server!\nCOMMAND: signUp / signIn / exit");

            String message = null;
            while ((message = in.readLine()) != null) {
                if (message.equalsIgnoreCase("exit")) {
                    out.println("You have left the chat.");
                    break;
                } else if (message.equalsIgnoreCase("signIn")) {
                    if(!getUserInfo()) {
                        break;
                    }
                    insertUser();
                }
                else if (message.equalsIgnoreCase("signUp")) {
                    if(!getUserInfo()) {
                        break;
                    }
                    if(!findUserInDatabase()) {
                        break;
                    }
                    out.println("1.\tCreate room\n" + "2.\tChoose room\n" + "3.\tExit");
                    message = in.readLine();
                    if(message.equals("1")) {
                        if(!createRoom()) {
                            out.println("You have left the chat.");
                            break;
                        }
                        break;
                    } else if (message.equals("2")) {
                        if(!startChatting()) {
                            out.println("Chatroom not found.");
                            break;
                        }
                    } else {
                        out.println("You have left the chat.");
                        break;
                    }
                }
            }
            stop();
        } catch (IOException | SQLException e) {
            System.err.println("Error reading from client: " + e.getMessage());
        }
    }

    private boolean startChatting() throws SQLException, IOException {
        List<Chat> chats = chatService.findAll();
        for (Chat chat : chats) {
            out.println(chat.getId() + ":\t" + chat.getName());
        }
        String text = in.readLine();
        try {
            Long chatId = Long.parseLong(text);
            Optional<Chat> chatOptional = chatService.findById(chatId);
            out.println("Java Room ---");
            if (chatOptional.isPresent()) {
                chat = chatOptional.get();
                print30Messages();
                lastMessageTime = LocalDateTime.now();
                Thread printChatMessage = printChatMessage();
                printChatMessage.start();
                while ((text = in.readLine()) != null) {
                    if (text.equals("exit")) {
                        out.println("You have left the chat.");
                        break;
                    }
                    messageService.addMessage(text, user, chat);
                }
                return true;
            }
        } catch (NumberFormatException e) {
            out.println("You have left the chat.");
        }
        return false;
    }


    private void print30Messages() throws SQLException {
        Optional<List<Message>> messagesOptional = messageService.findLast30MessagesInChat(chat);
        if (messagesOptional.isPresent()) {
            List<Message> messages = messagesOptional.get();
            if (!messages.isEmpty()) {
                for (Message message : messages) {
                    out.println( message.getUser().getEmail() + ": " + message.getTextMassage());
                }
            }
        }
    }

    private boolean createRoom() throws IOException, SQLException {
            out.println("Enter chatname:");
            String nameChat = in.readLine().trim();
            if(nameChat.equals("exit")) return false;
            chatService.addChat(nameChat, user.getId());
            out.println("Successful!");
            return true;
    }


    private Thread printChatMessage() {
        return new Thread(() -> {
            try {
                while (true) {
                    Optional<List<Message>> optionalMessages =
                            messageService.getAllMessagesAfterTimestamp(lastMessageTime, user, chat);
                    if (optionalMessages.isPresent()) {
                        List<Message> messages = optionalMessages.get();
                        for (Message message : messages) {
                            out.println(message.getUser().getEmail() + ": " + message.getTextMassage());
                            lastMessageTime = message.getTime();
                        }
                    }
                    Thread.sleep(100); // Задержка между запросами
                }
            } catch (InterruptedException | SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private boolean getUserInfo() throws IOException {
        out.println("Enter username:");
        userName = in.readLine().trim();
        if (userName.equalsIgnoreCase("exit")) {
            return false;
        }
        out.println("Enter password:");
        userPassword = in.readLine().trim();
        if (userPassword.equalsIgnoreCase("exit")) {
            return false;
        }
        return true;
    }

    private boolean findUserInDatabase() {
        if (!usersService.findUser(userName, userPassword).isPresent()) {
            out.println("User not found!");
            return false;
        }
        user = usersService.findUser(userName, userPassword).get();
        out.println("Start messaging!");
        return true;
    }


    private boolean insertUser() {
        String  encryptedPassword = usersService.addUser(userName, userPassword);
        if (encryptedPassword == null) {
            out.println("The user with this login is busy!");
            return false;
        }
        out.println("Successful!");
        return true;
    }

    private void stop() throws IOException {
        if (in != null) {
            in.close();
        }
        if (out != null) {
            out.close();
        }
        if (clientSocket != null) {
            clientSocket.close();
        }
    }
}
