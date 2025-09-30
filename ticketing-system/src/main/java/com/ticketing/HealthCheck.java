package com.ticketing;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import jakarta.mail.Session;
import jakarta.mail.Transport;

public class HealthCheck
{
    public static void main(String[] args) throws Exception
    {
        Map<String, Object> health = new HashMap<>();
        

        pingDatabase();

        boolean emailOk = checkEmailHandler();
        health.put("email", emailOk?"UP":"DOWN");
    }

    static void pingDatabase()
    {
        String currentDirectory = System.getProperty("user.dir");
        File folder = new File(currentDirectory+"/Configs");
        if(!folder.exists())
        {
            folder.mkdirs();
        }

        String healthCheckURL = "";
    }

    static boolean checkEmailHandler() throws SQLException, IOException
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
            return true;
        }
        catch (Exception e)
        {
            //log in failed.
            e.printStackTrace(); //to be adjusted to output to a log system.
            return false;
        }
    }
}