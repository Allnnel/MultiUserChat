package edu.school21.sockets.models;

import java.util.List;
import java.util.Objects;

public class Chat {
  private Long id;
  private String name;
  private Long ownerID;
  private List<Message> messages;
  private List<User> users;

  public Chat(Long id, String name, Long ownerID, List<Message> messages) {
    this.id = id;
    this.name = name;
    this.ownerID = ownerID;
    this.messages = messages;
  }

  public long getId() {
    return id;
  }

  public long getOwnerID() {
    return ownerID;
  }

  public String getName() {
    return name;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setOwnerID(Long ownerID) {
    this.ownerID = ownerID;
  }

  public boolean equals(Object obj) {
    return (this == obj);
  }

  public List<Message> getMessages() {
    return messages;
  }

  public void setMessages(List<Message> messages) {
    this.messages = messages;
  }

  public int hashCode() {
    return Objects.hash(id, name, ownerID);
  }

  public String toString() {
    return "Chat {" + " id:" + id + " name:" + name + " ownerID:" + ownerID + " }";
  }
}
