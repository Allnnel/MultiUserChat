package edu.school21.sockets.services;

import edu.school21.sockets.models.Chat;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface ChatService {
  void addChat(String name, Long ownerID) throws SQLException;

  List<Chat> findAll();

  Optional<Chat> findById(Long id) throws SQLException;
}
