package DAO;

import Model.*;

import java.util.List;

public interface ISocialMediaDAO {
    Account findAccount(String username) throws Exception;
    Account findAccount(int id) throws Exception;
    Account createAccount(Account account) throws Exception;
    Message createMessage(Message message) throws Exception;
    Message findMessage(int id) throws Exception;
    List<Message> findMessagesByPoster(int posted_by) throws Exception;
    List<Message> findAllMessages() throws Exception;
    void updateMessage(Message message) throws Exception;
    void deleteMessage(int id) throws Exception;
}
