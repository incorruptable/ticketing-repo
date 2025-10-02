package com.ticketing;

import com.sun.mail.imap.IMAPFolder;
import com.sun.mail.imap.IdleManager;

import java.io.IOException;
import java.sql.SQLException;

import jakarta.mail.*;
import jakarta.mail.Message;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.event.MessageCountAdapter;
import jakarta.mail.event.MessageCountEvent;
import jakarta.mail.Folder;
import jakarta.mail.MessagingException;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Properties;

public class EmailUtil
{
    //Handler for email importing to SQL Database
    static String emailHandlerAddress = ""; //the email address the ticketing system works through. Call from yml or json.
    static String emailHandlerPassword = "";
    static String emailHost = "";

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
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void emailListener() throws SQLException, IOException
    {
        Map<String,String> sysConfig = DatabaseHandler.loadSystemConfig();

        String host = sysConfig.get("imap_host");
        String username = sysConfig.get("mail_user");
        String password = sysConfig.get("mail_pass");
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
                        } catch (MessagingException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            //Waiting for new messages here.
            idleManager.watch(inbox);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
