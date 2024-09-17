package DAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.List;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import Model.*;
import Util.ConnectionUtil;

public class SocialMediaDAO implements ISocialMediaDAO {

    private static final Logger logger = LoggerFactory.getLogger(SocialMediaDAO.class);
    
    public Account findAccount(String username) throws Exception {
        logger.info("Finding an account with username `{}`", username);
        Connection connection = ConnectionUtil.getConnection();
        try (PreparedStatement p = connection.prepareStatement("select * from account where username = ?;")) {
            p.setString(1, username);
            try (ResultSet r = p.executeQuery()) {
                if (r.next()) return new Account(r.getInt("account_id"), r.getString("username"), r.getString("password"));
                else return null;
            }
        } catch (SQLException e) {
            logger.error("An SQL Exception occurred retrieving account from database!", e);
            throw new Exception(e);
        } finally {
            connection.close();
        }
    }

    public Account findAccount(int id) throws Exception {
        logger.info("Finding an account with id `{}`", id);
        Connection connection = ConnectionUtil.getConnection();
        try (PreparedStatement p = connection.prepareStatement("select * from account where account_id = ?;")) {
            p.setInt(1, id);
            try (ResultSet r = p.executeQuery()) {
                if (r.next()) return new Account(r.getInt("account_id"), r.getString("username"), r.getString("password"));
                else return null;
            }
        } catch (SQLException e) {
            logger.error("An SQL Exception occurred retrieving account from database!", e);
            throw new Exception(e);
        } finally {
            connection.close();
        }
    }

    public Account createAccount(Account account) throws Exception {
        logger.info("Creating an account with username {}", account.getUsername());
        Connection connection = ConnectionUtil.getConnection();
        try (PreparedStatement p = connection.prepareStatement("insert into account (username, password) values (?, ?);", Statement.RETURN_GENERATED_KEYS)) {
            p.setString(1, account.getUsername());
            p.setString(2, account.getPassword());
            p.executeUpdate();
            try (ResultSet r = p.getGeneratedKeys()) {
                if (r.next()) {
                    int accountID = r.getInt(1);
                    account.setAccount_id(accountID);
                    logger.info("Account successfully created. ID: {}", accountID);
                }
            }
            return account;
        } catch (SQLException e) {
            logger.error("An SQL Exception occurred creating account", e);
            throw new Exception(e);
        } finally {
            connection.close();
        }
    }

    public Message createMessage(Message message) throws Exception {
        logger.info("Persisting a new message posted by user with account id `{}`", message.getPosted_by());
        Connection connection = ConnectionUtil.getConnection();
        try (PreparedStatement p = connection.prepareStatement("insert into message (posted_by, message_text, time_posted_epoch) values (?, ?, ?);", Statement.RETURN_GENERATED_KEYS)) {
            p.setInt(1, message.getPosted_by());
            p.setString(2, message.getMessage_text());
            p.setLong(3, message.getTime_posted_epoch());
            p.executeUpdate();
            try (ResultSet r = p.getGeneratedKeys()) {
                if (r.next()) {
                    int messageID = r.getInt(1);
                    message.setMessage_id(messageID);
                    logger.info("Message persisted successfully with id `{}`", messageID);
                }
            }
            return message;
        } catch (SQLException e) {
            logger.error("An SQL Exception occurred persisting message to database", e);
            throw new Exception(e);
        } finally {
            connection.close();
        }
    }

    public Message findMessage(int id) throws Exception {
        logger.info("Finding message with id `{}`", id);
        Connection connection = ConnectionUtil.getConnection();
        try (PreparedStatement p = connection.prepareStatement("select * from message where message_id = ?;")) {
            p.setInt(1, id);
            try (ResultSet r = p.executeQuery()) {
                if (r.next()) return new Message(r.getInt("message_id"), r.getInt("posted_by"), r.getString("message_text"), r.getLong("time_posted_epoch"));
                else {
                    logger.info("A message with the given ID was not found.");
                    return null;
                }
            }
        } catch (SQLException e) {
            logger.error("An SQL Exception occurred finding the message.", e);
            throw new Exception(e);
        } finally {
            connection.close();
        }
    }

    public List<Message> findMessagesByPoster(int posted_by) throws Exception {
        logger.info("Finding message posted by account id `{}`", posted_by);
        Connection connection = ConnectionUtil.getConnection();
        List<Message> results = new ArrayList<>();
        try (PreparedStatement p = connection.prepareStatement("select * from message where posted_by = ?;")) {
            p.setInt(1, posted_by);
            try (ResultSet r = p.executeQuery()) {
                while (r.next()) 
                    results.add(new Message(r.getInt("message_id"), r.getInt("posted_by"), r.getString("message_text"), r.getLong("time_posted_epoch")));
            }
            return results;
        } catch (SQLException e) {
            logger.error("An SQL Exception occurred finding the message.", e);
            throw new Exception(e);
        } finally {
            connection.close();
        }
    }

    public List<Message> findAllMessages() throws Exception {
        logger.info("Finding all messages");
        Connection connection = ConnectionUtil.getConnection();
        List<Message> results = new ArrayList<>();
        try (PreparedStatement p = connection.prepareStatement("select * from message;");
            ResultSet r = p.executeQuery()) {
            while (r.next()) 
                results.add(new Message(r.getInt("message_id"), r.getInt("posted_by"), r.getString("message_text"), r.getLong("time_posted_epoch")));
            return results;
        } catch (SQLException e) {
            logger.error("An SQL Exception occurred finding the message.", e);
            throw new Exception(e);
        } finally {
            connection.close();
        }
    }

    public void updateMessage(Message message) throws Exception {
        logger.info("Updating message with id `{}`", message.getMessage_id());
        Connection c = ConnectionUtil.getConnection();
        try (PreparedStatement p = c.prepareStatement("update message set message_text = ? where message_id = ?;")) {
            p.setString(1, message.getMessage_text());
            p.setInt(2, message.getMessage_id());
            p.executeUpdate();
        } catch (SQLException e) {
            logger.error("An SQL Exception occurred updating the message.", e);
            throw new Exception(e);
        } finally {
            c.close();
        }
    }

    public void deleteMessage(int id) throws Exception {
        logger.info("Deleting message with id `{}`", id);
        Connection c = ConnectionUtil.getConnection();
        try (PreparedStatement p = c.prepareStatement("delete from message where message_id = ?;")) {
            p.setInt(1, id);
            p.executeUpdate();
        } catch (SQLException e) {
            logger.error("An SQL Exception occurred deleting the message.", e);
            throw new Exception(e);
        } finally {
            c.close();
        }
    }
}
