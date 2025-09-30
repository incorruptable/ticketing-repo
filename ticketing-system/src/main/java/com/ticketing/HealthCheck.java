package com.ticketing;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.lang.management.ManagementFactory;
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
        

        boolean dbOk = pingDatabase();
        health.put("database", dbOk?"UP":"DOWN");

        boolean emailOk = checkEmailHandler();
    }

    static boolean pingDatabase()
    {
        try (Connection conn = DriverManager.getConnection("","","");
        PreparedStatement stmt = conn.prepareStatement("SELECT 1");
        ResultSet rs = stmt.executeQuery())
        {
            if(rs.next()) //This is just for my understanding. Code wise, unnecessary. If rs didn't work, the if is unnecessary.
            {
                //Database is responding.
                return true;
            }
            else return false; //This is just to get the compiler to stop complaining. This is functionally impossible.
        } catch (SQLException e)
        {
            //Healthcheck failed. Prep to set things on fire.
            return false;
        }
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
            return false;
        }
    }
}