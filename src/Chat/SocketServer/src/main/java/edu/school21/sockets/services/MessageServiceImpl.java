package edu.school21.sockets.services;

import edu.school21.sockets.models.Chat;
import edu.school21.sockets.models.Message;
import edu.school21.sockets.models.User;
import edu.school21.sockets.repositories.MessageRepository;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("messageService")
public class MessageServiceImpl implements MessageService {
  MessageRepository messageRepository;

  @Autowired
  public MessageServiceImpl(@Qualifier("messageRepository") MessageRepository messageRepository) {
    this.messageRepository = messageRepository;
  }

  @Override
  public String addMessage(String text, User user, Chat chat) throws SQLException {
    Message message = new Message(1L, user, chat, text, LocalDateTime.now());
    messageRepository.save(message);
    return null;
  }

  @Override
  public Optional<List<Message>> getAllMessagesAfterTimestamp(
      LocalDateTime timestamp, User user, Chat chat) throws SQLException {
    return messageRepository.findAllAfterTimestamp(timestamp, user, chat);
  }

  @Override
  public Optional<List<Message>> findLast30MessagesInChat(Chat chat) throws SQLException {
    return messageRepository.findLast30MessagesInChat(chat);
  }

  @Override
  public LocalDateTime getLastMessageTime() throws SQLException {
    return messageRepository.getLastTime();
  }
}
