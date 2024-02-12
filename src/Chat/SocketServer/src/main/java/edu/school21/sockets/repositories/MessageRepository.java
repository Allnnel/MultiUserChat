package edu.school21.sockets.repositories;

import edu.school21.sockets.models.Chat;
import edu.school21.sockets.models.Message;
import edu.school21.sockets.models.User;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MessageRepository {
  Optional<Message> findById(long id) throws SQLException;

  void save(Message message) throws NotSavedSubEntityException, SQLException;

  Optional<List<Message>> findAllAfterTimestamp(LocalDateTime timestamp, User user, Chat chat)
      throws SQLException;

  LocalDateTime getLastTime() throws NotSavedSubEntityException, SQLException;

  void update(Message message) throws SQLException;

  Optional<List<Message>> findLast30MessagesInChat(Chat chat) throws SQLException;
}
