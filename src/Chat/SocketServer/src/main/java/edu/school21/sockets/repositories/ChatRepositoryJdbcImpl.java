package edu.school21.sockets.repositories;

import static java.lang.System.out;

import edu.school21.sockets.models.Chat;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component("chatRepository")
public class ChatRepositoryJdbcImpl implements ChatRepository {
  private static Connection connection;
  private final UsersRepositoryImpl userRepositoryJdbc;

  @Autowired
  public ChatRepositoryJdbcImpl(
      @Qualifier("hikariDataSource") DataSource dataSource,
      @Qualifier("usersRepository") UsersRepositoryImpl userRepositoryJdbc)
      throws SQLException {
    this.connection = dataSource.getConnection();
    this.userRepositoryJdbc = userRepositoryJdbc;
  }

  @Override
  public Optional<Chat> findById(long idChat) throws SQLException {
    final String sqlQuery = "SELECT * FROM service.chat WHERE id=?";
    Chat chat = null;
    ResultSet resultSet = null;
    PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
    preparedStatement.setLong(1, idChat);
    resultSet = preparedStatement.executeQuery();
    if (resultSet.next()) {
      Long id = resultSet.getLong("id");
      String name = resultSet.getString("name");
      Long ownerId = resultSet.getLong("ownerId");
      chat = new Chat(id, name, ownerId, new ArrayList<>());
    }
    return Optional.ofNullable(chat);
  }

  @Override
  public void save(Chat chat) throws NotSavedSubEntityException, SQLException {
    final String sqlQuery = "INSERT INTO service.chat (name, ownerId) VALUES (?, ?) RETURNING *";
    ResultSet resultSet = null;
    if (userRepositoryJdbc.findById(chat.getOwnerID()).isPresent()) {
      PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
      preparedStatement.setString(1, chat.getName());
      preparedStatement.setLong(2, chat.getOwnerID());
      resultSet = preparedStatement.executeQuery();
      resultSet.next();
      chat.setId(resultSet.getLong("id"));
    } else {
      throw new NotSavedSubEntityException();
    }
  }

  @Override
  public void update(Chat chat) throws SQLException {
    final String sqlQuery = "UPDATE service.chat SET name=?, ownerId=? WHERE id=?";
    PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
    preparedStatement.setString(1, chat.getName());
    preparedStatement.setLong(2, chat.getOwnerID());
    preparedStatement.setLong(3, chat.getId());
    preparedStatement.execute();
  }

  @Override
  public List<Chat> findAll() {
    String sqlQuery = "SELECT * FROM service.chat";
    ResultSet resultSet = null;
    List<Chat> list = new ArrayList<>();
    try {
      PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);
      resultSet = preparedStatement.executeQuery();
      while (resultSet.next()) {
        Long id = resultSet.getLong("id");
        String name = resultSet.getString("name");
        Long ownerID = resultSet.getLong("ownerID");
        Chat chat = new Chat(id, name, ownerID, new ArrayList<>());
        list.add(chat);
      }
    } catch (SQLException e) {
      out.println(e);
    }
    return list;
  }
}
