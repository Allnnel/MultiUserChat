package edu.school21.sockets.services;

import edu.school21.sockets.models.Chat;
import edu.school21.sockets.models.Message;
import edu.school21.sockets.models.User;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MessageService {

  String addMessage(String text, User user, Chat chat) throws SQLException;

  Optional<List<Message>> getAllMessagesAfterTimestamp(
      LocalDateTime timestamp, User user, Chat chat) throws SQLException;

  LocalDateTime getLastMessageTime() throws SQLException;

  Optional<List<Message>> findLast30MessagesInChat(Chat chat) throws SQLException;
}
