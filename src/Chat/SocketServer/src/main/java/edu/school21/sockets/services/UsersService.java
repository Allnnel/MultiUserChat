package edu.school21.sockets.services;

import edu.school21.sockets.models.User;
import java.util.Optional;

public interface UsersService {

  String addUser(String email, String password);

  Optional<User> findUser(String email, String password);
}
