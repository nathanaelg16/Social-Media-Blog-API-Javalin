package Service;

import java.util.List;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import Model.*;
import DAO.*;
import Exception.*;

public class SocialMediaService implements ISocialMediaService {
    private static final Logger logger = LoggerFactory.getLogger(SocialMediaService.class);

    private ISocialMediaDAO socialMediaDAO = new SocialMediaDAO();
    
    public Account registerAccount(Account account) throws Exception {
        logger.info("Verifying account meets validation requirements...");
        if (account.getUsername() == null || account.getUsername().isBlank()) throw new AccountRegistrationException("Username is blank");
        if (account.getPassword() == null) throw new AccountRegistrationException("Password is blank");
        if (account.getPassword().length() < 4) throw new AccountRegistrationException("Password does not meet length requirements");
        if (this.socialMediaDAO.findAccount(account.getUsername()) == null) {
            logger.info("Account registration request has been successfully validated!");   
            return this.socialMediaDAO.createAccount(account);
        } else throw new AccountRegistrationException("Account with given username already exists");
    }

    public Account login(Account account) throws Exception {
        logger.info("Attempting to log in account with username `{}`", account.getUsername());
        
        if (account.getUsername().isBlank()) {
            logger.warn("Account username is blank!");
            return null;
        }

        Account actual = this.socialMediaDAO.findAccount(account.getUsername());
        
        if (actual == null) {
            logger.info("Could not find account with the specified username");
            return null;
        }

        if (actual.getPassword().equals(account.getPassword())) return actual;
        else {
            logger.info("Passwords do not match!");
            return null;
        }
    }

    public Message createMessage(Message message) throws Exception {
        logger.info("Validating message meets requirements...");
        validateMessageText(message.getMessage_text());
        if (this.socialMediaDAO.findAccount(message.getPosted_by()) == null) throw new MessageException("Invalid posted_by user account ID");
        return this.socialMediaDAO.createMessage(message);
    }

    public Message getMessage(int message_id) throws Exception {
        logger.info("Fetching message with id `{}`", message_id);
        return this.socialMediaDAO.findMessage(message_id);
    }

    public List<Message> getAllMessages() throws Exception {
        logger.info("Fetching all messages from the database");
        return this.socialMediaDAO.findAllMessages();
    }

    public List<Message> getAllMessagesFromPoster(int posted_by) throws Exception {
        logger.info("Fetching all messages from the database posted by poster with account id `{}`", posted_by);
        return this.socialMediaDAO.findMessagesByPoster(posted_by);
    }

    public Message updateMessage(Message message) throws Exception {
        logger.info("Updating message with id: `{}`", message.getMessage_id());
        validateMessageText(message.getMessage_text());
        if (this.socialMediaDAO.findMessage(message.getMessage_id()) == null) throw new MessageException("Message with specified ID not found");
        this.socialMediaDAO.updateMessage(message);
        return this.socialMediaDAO.findMessage(message.getMessage_id());
    }

    public Message deleteMessage(int message_id) throws Exception {
        Message message = this.getMessage(message_id);
        logger.info("Deleting message with id `{}`", message_id);
        this.socialMediaDAO.deleteMessage(message_id);
        return message;
    }

    private void validateMessageText(String messageText) throws MessageException {
        logger.info("Validating message_text meets requirements...");
        if (messageText == null || messageText.isBlank()) throw new MessageException("Message is blank");
        if (messageText.length() > 255) throw new MessageException("Message exceeds character limit (255)");
    }
}