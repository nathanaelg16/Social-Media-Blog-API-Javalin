package Controller;

import io.javalin.Javalin;
import io.javalin.http.Context;

import Model.*;
import Service.*;
import Exception.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

/**
 * TODO: You will need to write your own endpoints and handlers for your controller. The endpoints you will need can be
 * found in readme.md as well as the test cases. You should
 * refer to prior mini-project labs and lecture materials for guidance on how a controller may be built.
 */
public class SocialMediaController {
    
    private static final Logger logger = LoggerFactory.getLogger(SocialMediaController.class);

    private ISocialMediaService socialMediaService = new SocialMediaService();
    
    /**
     * In order for the test cases to work, you will need to write the endpoints in the startAPI() method, as the test
     * suite must receive a Javalin object from this method.
     * @return a Javalin app object which defines the behavior of the Javalin controller.
     */
    public Javalin startAPI() {
        Javalin app = Javalin
            .create()
            .post("register", this::register)
            .post("login", this::login)
            .post("messages", this::createMessage)
            .get("messages", this::getAllMessages)
            .get("messages/{id}", this::getMessage)
            .get("accounts/{id}/messages", this::getMessagesByAccount)
            .patch("messages/{id}", this::updateMessage)
            .delete("messages/{id}", this::deleteMessage)
            .exception(JsonProcessingException.class, (e, ctx) -> {
                logger.error("An error occurred processing the request.", e);
                ctx.status(400);
            }).exception(Exception.class, (e, ctx) -> {
                ctx.status(500);
            });
        return app;
    }

    /**
     * This is an example handler for an example endpoint.
     * @param context The Javalin Context object manages information about both the HTTP request and response.
     */
    private void register(Context context) throws Exception {
        logger.info("Received new account registration request.");
        try {
            ObjectMapper om = new ObjectMapper();
            Account account = om.readValue(context.body(), Account.class);
            Account registered = this.socialMediaService.registerAccount(account);
            context
                .result(om.writeValueAsString(registered))
                .status(200);
        } catch (AccountRegistrationException e) {
            logger.error("Account registration failed!", e);
            context.status(400);
        }
    }

    private void login(Context context) throws Exception {
        logger.info("Received new login request.");
        ObjectMapper om = new ObjectMapper();
        Account account = om.readValue(context.body(), Account.class);
        Account loggedIn = this.socialMediaService.login(account);
        if (loggedIn == null) context.status(401);
        else {
            context
                .result(om.writeValueAsString(loggedIn))
                .status(200);
        }
    }

    private void createMessage(Context context) throws Exception {
        logger.info("Received new request to create a message.");
        try {
            ObjectMapper om = new ObjectMapper();
            Message message = om.readValue(context.body(), Message.class);
            Message created = this.socialMediaService.createMessage(message);
            context
                .result(om.writeValueAsString(created))
                .status(200);
        } catch (MessageException e) {
            logger.error("An error occurred creating the message.", e);
            context.status(400);
        }
    }

    private void getAllMessages(Context context) throws Exception {
        logger.info("Received request to get all messages.");
        ObjectMapper om = new ObjectMapper();
        List<Message> messages = this.socialMediaService.getAllMessages();
        context
            .result(om.writeValueAsString(messages))
            .status(200);
    }

    private void getMessage(Context context) throws Exception {
        logger.info("Received request to get message with id `{}`", context.pathParam("id"));
        ObjectMapper om = new ObjectMapper();
        Message message = this.socialMediaService.getMessage(Integer.parseInt(context.pathParam("id")));
        if (message != null) {
            context
                .result(om.writeValueAsString(message))
                .status(200);
        }
    }

    private void getMessagesByAccount(Context context) throws Exception {
        logger.info("Received request to get messages from account with id `{}`", context.pathParam("id"));
        ObjectMapper om = new ObjectMapper();
        List<Message> messages = this.socialMediaService.getAllMessagesFromPoster(Integer.parseInt(context.pathParam("id")));
        context
            .result(om.writeValueAsString(messages))
            .status(200);
    }

    private void updateMessage(Context context) throws Exception {
        logger.info("Received request to patch message with id `{}`", context.pathParam("id"));
        try {
            ObjectMapper om = new ObjectMapper();
            Message message = om.readValue(context.body(), Message.class);
            message.setMessage_id(Integer.parseInt(context.pathParam("id")));
            Message patchedMessage = this.socialMediaService.updateMessage(message);
            if (patchedMessage != null) {
                context
                    .result(om.writeValueAsString(patchedMessage))
                    .status(200);
            }
        } catch (MessageException e) {
            logger.error("An error occurred patching the message.", e);
            context.status(400);
        }
    }

    private void deleteMessage(Context context) throws Exception {
        logger.info("Received request to get message with id `{}`", context.pathParam("id"));
        ObjectMapper om = new ObjectMapper();
        Message message = this.socialMediaService.deleteMessage(Integer.parseInt(context.pathParam("id")));
        if (message != null) {
            context
                .result(om.writeValueAsString(message))
                .status(200);
        }
    }
}