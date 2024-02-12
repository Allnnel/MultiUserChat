package edu.school21.sockets.services;

import edu.school21.sockets.models.User;
import edu.school21.sockets.repositories.UsersRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component("usersService")
public class UsersServiceImpl implements UsersService {
  private final UsersRepository usersRepository;
  private final PasswordEncoder passwordEncoder;

  @Autowired
  public UsersServiceImpl(
      @Qualifier("passwordEncoder") PasswordEncoder passwordEncoder,
      @Qualifier("usersRepository") UsersRepository usersRepository) {
    this.usersRepository = usersRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public String addUser(String email, String password) {
    if (usersRepository.findByEmail(email).isPresent()) {
      return null;
    }
    User user = new User(null, email, passwordEncoder.encode(password));
    usersRepository.save(user);
    usersRepository.update(user);
    return user.getPassword();
  }

  @Override
  public Optional<User> findUser(String email, String password) {
    Optional<User> userOptional = usersRepository.findByEmail(email);
    if (userOptional.isPresent()) {
      User user = userOptional.get();
      if (passwordEncoder.matches(password, user.getPassword())) {
        return userOptional;
      }
    }
    return Optional.empty();
  }
}
