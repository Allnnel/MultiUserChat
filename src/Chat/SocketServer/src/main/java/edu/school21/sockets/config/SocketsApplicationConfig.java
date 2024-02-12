package edu.school21.sockets.config;

import java.util.Objects;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@ComponentScan(basePackages = "edu.school21.sockets.services edu.school21.sockets.repositories")
@PropertySource("classpath:db.properties")
public class SocketsApplicationConfig {
  @Autowired private Environment environment;

  @Bean
  public HikariDataSource hikariDataSource() {
    HikariDataSource hikariDataSource = new HikariDataSource();
    hikariDataSource.setDriverClassName(
        Objects.requireNonNull(environment.getProperty("db.driver.name")));
    hikariDataSource.setJdbcUrl(environment.getProperty("db.url"));
    hikariDataSource.setUsername(environment.getProperty("db.user"));
    hikariDataSource.setPassword(environment.getProperty("db.password"));
    return hikariDataSource;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
