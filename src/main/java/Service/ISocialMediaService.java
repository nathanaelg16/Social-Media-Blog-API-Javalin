package Service;

import java.util.List;

import Model.*;

public interface ISocialMediaService {
    Account registerAccount(Account account) throws Exception;
    Account login(Account account) throws Exception;
    Message createMessage(Message message) throws Exception;
    Message getMessage(int message_id) throws Exception;
    List<Message> getAllMessages() throws Exception;
    List<Message> getAllMessagesFromPoster(int posted_by) throws Exception;
    Message updateMessage(Message message) throws Exception;
    Message deleteMessage(int message_id) throws Exception;
}
