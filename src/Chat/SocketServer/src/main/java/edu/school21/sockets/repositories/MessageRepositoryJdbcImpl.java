package edu.school21.sockets.repositories;

import edu.school21.sockets.models.Chat;
import edu.school21.sockets.models.Message;
import edu.school21.sockets.models.User;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("messageRepository")
public class MessageRepositoryJdbcImpl implements MessageRepository {
  private final Connection connection;
  private final UsersRepository userRepository;
  private final ChatRepository chatRepository;

  @Autowired
  public MessageRepositoryJdbcImpl(
      @Qualifier("hikariDataSource") DataSource dataSource,
      @Qualifier("usersRepository") UsersRepository userRepository,
      @Qualifier("chatRepository") ChatRepository chatRepository)
      throws SQLException {
    this.connection = dataSource.getConnection();
    this.userRepository = userRepository;
    this.chatRepository = chatRepository;
  }

  @Override
  public Optional<Message> findById(long idMessage) throws SQLException {
    final String sqlQuery = "SELECT * FROM service.message WHERE id=?";
    Message message = null;
    ResultSet resultSet = null;
    PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
    preparedStatement.setLong(1, idMessage);
    resultSet = preparedStatement.executeQuery();
    if (resultSet.next()) {
      long id = resultSet.getLong("id");
      long authorid = resultSet.getLong("authorID");
      long roomId = resultSet.getLong("roomID");
      String textMessage = resultSet.getString("textMessage");
      Timestamp timestamp = resultSet.getTimestamp("time");
      User user = userRepository.findById(authorid).orElse(null);
      Chat chat = chatRepository.findById(roomId).orElse(null);
      message = new Message(id, user, chat, textMessage, timestamp.toLocalDateTime());
    }
    return Optional.ofNullable(message);
  }

  @Override
  public Optional<List<Message>> findAllAfterTimestamp(
      LocalDateTime timestamp, User authorUser, Chat chatUser) throws SQLException {
    final String sqlQuery =
        "SELECT * FROM service.message WHERE time > ? AND service.message.authorId <> ? AND"
            + " service.message.roomID = ?";
    List<Message> messages = new ArrayList<>();
    ResultSet resultSet = null;
    PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
    preparedStatement.setTimestamp(1, Timestamp.valueOf(timestamp));
    preparedStatement.setLong(2, authorUser.getId());
    preparedStatement.setLong(3, chatUser.getId());
    resultSet = preparedStatement.executeQuery();
    while (resultSet.next()) {
      long id = resultSet.getLong("id");
      long authorId = resultSet.getLong("authorID");
      String textMessage = resultSet.getString("textMessage");
      Timestamp messageTimestamp = resultSet.getTimestamp("time");
      long roomId = resultSet.getLong("roomID");
      User user = userRepository.findById(authorId).orElse(null);
      Chat chat = chatRepository.findById(roomId).orElse(null);
      Message message =
          new Message(id, user, chat, textMessage, messageTimestamp.toLocalDateTime());
      messages.add(message);
    }
    return Optional.of(messages);
  }

  @Override
  public Optional<List<Message>> findLast30MessagesInChat(Chat chat) throws SQLException {
    final String sqlQuery =
        "SELECT * FROM service.message WHERE service.message.roomID = ? ORDER BY time DESC LIMIT"
            + " 30";
    List<Message> messages = new ArrayList<>();
    try (PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {
      preparedStatement.setLong(1, chat.getId());
      try (ResultSet resultSet = preparedStatement.executeQuery()) {
        while (resultSet.next()) {
          long id = resultSet.getLong("id");
          long authorId = resultSet.getLong("authorID");
          String textMessage = resultSet.getString("textMessage");
          Timestamp messageTimestamp = resultSet.getTimestamp("time");
          User user = userRepository.findById(authorId).orElse(null);
          Message message =
              new Message(id, user, chat, textMessage, messageTimestamp.toLocalDateTime());
          messages.add(message);
        }
      }
    }
    return Optional.of(messages);
  }

  @Override
  public void save(Message message) throws NotSavedSubEntityException, SQLException {
    final String sqlQuery =
        "INSERT INTO service.message (authorID, roomID, textMessage, time) VALUES (?, ?, ?, ?)"
            + " RETURNING *";
    ResultSet resultSet = null;
    if ((userRepository.findById(message.getUser().getId()).isPresent())
        && (chatRepository.findById(message.getChat().getId()).isPresent())) {
      PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
      preparedStatement.setLong(1, message.getUser().getId());
      preparedStatement.setLong(2, message.getChat().getId());
      preparedStatement.setString(3, message.getTextMassage());
      preparedStatement.setTimestamp(4, Timestamp.valueOf(message.getTime()));
      resultSet = preparedStatement.executeQuery();
      resultSet.next();
      message.setId(resultSet.getLong("id"));
    } else {
      throw new NotSavedSubEntityException();
    }
  }

  @Override
  public LocalDateTime getLastTime() throws NotSavedSubEntityException, SQLException {
    final String sqlQuery = "SELECT MAX(time) FROM service.message";
    ResultSet resultSet = null;
    LocalDateTime lastMessageTime = null;
    PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
    resultSet = preparedStatement.executeQuery();
    if (resultSet.next()) {
      Timestamp lastTimestamp = resultSet.getTimestamp(1);
      if (lastTimestamp != null) {
        lastMessageTime = lastTimestamp.toLocalDateTime();
      }
    }
    return lastMessageTime;
  }

  @Override
  public void update(Message message) throws SQLException {
    final String sqlQuery =
        "UPDATE service.message SET authorID=?, roomID=?, textMessage=?, time=? WHERE id=?";
    PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
    preparedStatement.setLong(1, message.getUser().getId());
    preparedStatement.setLong(2, message.getChat().getId());
    preparedStatement.setString(3, message.getTextMassage());

    if (message.getTime() != null) {
      preparedStatement.setTimestamp(4, Timestamp.valueOf(message.getTime()));
    } else {
      preparedStatement.setNull(4, Types.TIMESTAMP);
    }

    preparedStatement.setLong(5, message.getId());

    preparedStatement.executeUpdate();
  }
}
