package com.ticketing;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IdleManager;

import java.io.UnsupportedEncodingException;
import java.sql.SQLException;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.event.MessageCountAdapter;
import jakarta.mail.event.MessageCountEvent;

import java.io.FileReader;
import java.io.IOException;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.mail.internet.AddressException;

@Component
public class EmailUtil
{
    //TODO: Update to handle email credentials from file/SQL
    //Handler for email importing to SQL Database
    static String emailHandlerAddress = ""; //the email address the ticketing system works through. Call from yml or json.
    static String emailHandlerPassword = "";
    static String emailHost = "";

    private static final Logger log = LoggerFactory.getLogger(EmailUtil.class);

    public static void sendEmail(Session session, String toEmail, String subject, String body)
    {
        try
        {
            MimeMessage msg = new MimeMessage(session);
            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
            msg.addHeader("format","flowed");
            msg.addHeader("Content-Transfer-Encoding", "8bit");

            msg.setFrom(new InternetAddress(emailHandlerAddress, "NoReply-JD"));

            msg.setReplyTo(InternetAddress.parse(emailHandlerAddress, false));

            msg.setSubject(subject,"UTF-8");

            msg.setText(body, "UTF-8");

            msg.setSentDate(new Date());

            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
            
            Transport.send(msg);
        }
        catch(AddressException e)
        {
            log.error("Recipient Invalid", e);
        }
        catch (SendFailedException e)
        {
            log.error("Error sending email", e);
        }
        catch (MessagingException e)
        {
            log.error("Messaging failure during send", e);
        }
        catch (UnsupportedEncodingException e)
        {
            log.error("Unsupported encoding in sender address", e);
        }
    }


    //TODO: Logic behind automatically creating new tickets needs to be done.
    //Needs to run the check from the last time it made the connection.
    public static void emailListener() throws SQLException, IOException
    {
        Map<String,String> sysConfig = DatabaseHandler.loadSystemConfig();

        String host = emailHost;
        String username = emailHandlerAddress;
        String password = emailHandlerPassword;
        String port = sysConfig.get("imap_port");

        Properties props = new Properties();
        props.put("mail.store.protocol","imaps");
        props.put("mail.imaps.host",host);
        props.put("mail.imaps.port",port);
        props.put("mail.imaps.ssl.trust",host);
        try
        {
            Session session = Session.getInstance(props);
            Store store = session.getStore();
            store.connect(username,password);

            IMAPFolder inbox = (IMAPFolder) store.getFolder("INBOX");
            inbox.open(Folder.READ_WRITE);

            ExecutorService executor = Executors.newSingleThreadExecutor();
            IdleManager idleManager = new IdleManager(session,executor);

            inbox.addMessageCountListener(new MessageCountAdapter() {
                @Override
                public void messagesAdded(MessageCountEvent ev)
                {
                    Message[] messages = ev.getMessages();
                    for(Message message : messages) {
                        try {
                            System.out.println("Received: " + message.getSubject());
                            Ticket ticket = new Ticket(message.getFrom(), "", message.getContent().toString(), 0, 1);
                        } catch (MessagingException e) 
                        {
                            log.error("General messaging error in listener");
                        }
                        catch (IOException e)
                        {
                            log.error("Error reading message content or configuration file", e);
                        }
                    }
                }
            });

            //Waiting for new messages here.
            idleManager.watch(inbox);
        }
        catch (NoSuchProviderException e)
        {
            log.error("Email Listener Failure",e);
        }
        catch (AuthenticationFailedException e)
        {
            log.error("Invalid credentials. Check email credentials for IMAP connection", e);
        }
        catch (MessagingException e)
        {
            log.error("General messagin error in listener", e);
        }
        catch (RuntimeException e)
        {
            log.error("Unexpected runtime error in email listener", e);
        }
    }
}
