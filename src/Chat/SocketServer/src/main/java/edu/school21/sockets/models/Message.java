package edu.school21.sockets.models;

import java.time.LocalDateTime;

public class Message {
  private Long id;
  private String textMassage;
  private LocalDateTime time;
  private Chat chat;
  private User user;

  public Message(Long id, User user, Chat chat, String textMassage, LocalDateTime time) {
    this.id = id;
    this.user = user;
    this.chat = chat;
    this.textMassage = textMassage;
    this.time = time;
  }

  public Long getId() {
    return id;
  }

  public Chat getChat() {
    return chat;
  }

  public LocalDateTime getTime() {
    return time;
  }

  public String getTextMassage() {
    return textMassage;
  }

  public User getUser() {
    return user;
  }

  public void setId(long id) {
    this.id = id;
  }

  public void setChat(Chat chat) {
    this.chat = chat;
  }

  public void setTextMassage(String textMassage) {
    this.textMassage = textMassage;
  }

  public void setTime(LocalDateTime time) {
    this.time = time;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String toString() {
    return "Message {" + " id:" + id + " textMassage:" + textMassage + " time:" + time + " }";
  }
}
