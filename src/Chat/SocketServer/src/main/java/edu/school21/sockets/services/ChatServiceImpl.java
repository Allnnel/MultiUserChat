package edu.school21.sockets.services;

import edu.school21.sockets.models.Chat;
import edu.school21.sockets.repositories.ChatRepository;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("chatService")
public class ChatServiceImpl implements ChatService {

  private final ChatRepository chatRepository;

  @Autowired
  public ChatServiceImpl(@Qualifier("chatRepository") ChatRepository chatRepository) {
    this.chatRepository = chatRepository;
  }

  @Override
  public void addChat(String name, Long ownerID) throws SQLException {
    Chat chat = new Chat(null, name, ownerID, new ArrayList<>());
    chatRepository.save(chat);
  }

  @Override
  public List<Chat> findAll() {
    return chatRepository.findAll();
  }

  @Override
  public Optional<Chat> findById(Long id) throws SQLException {
    return chatRepository.findById(id);
  }
}
