package com.ticketing;

import java.util.Map;
import java.util.Properties;

import jakarta.mail.Session;
import jakarta.mail.Transport;

public class Utils 
{
    static void emailLogin() throws Exception
    {
        //Checks if database email can log in. If not, prompts for credentials.

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

        Session session = Session.getInstance(props);

        try (Transport transport = session.getTransport("smtp")) 
        {
            transport.connect(host, Integer.parseInt(port), username, password);
            //Connection check. Success means it's logged in.
        }
        catch (Exception e)
        {
            //log in failed.
            e.printStackTrace(); //to be adjusted to output to a log system.
        }
    }
}
