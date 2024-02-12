package edu.school21.sockets.repositories;

import edu.school21.sockets.models.Chat;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ChatRepository {
  Optional<Chat> findById(long idChat) throws SQLException;

  void save(Chat chat) throws NotSavedSubEntityException, SQLException;

  void update(Chat chat) throws SQLException;

  List<Chat> findAll();
}
