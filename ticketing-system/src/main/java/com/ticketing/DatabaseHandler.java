package com.ticketing;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseHandler {

    private static final Logger log = LoggerFactory.getLogger(DatabaseHandler.class);
    public static Map<String, String> loadSystemConfig() throws SQLException, IOException
    {
        Map<String, String> config = new HashMap<>();

        //Obligatory health check
        try 
        {
            HealthCheck.pingDatabase();
        }
        catch (Exception e)
        {
            log.error("How did this show up? This is from the Database Healthcheck!",e);
        }

        //Database is working. Load in properties.
        Properties props = new Properties();
        try(FileInputStream fstream = new FileInputStream("config/config.properties"))
        {
            props.load(fstream);
        }

        String url = props.getProperty("db.url");
        String user = props.getProperty("db.user");
        String pass = props.getProperty("db.pass");
        //Loading in the resources to work with.
        try(Connection conn = DriverManager.getConnection(url, user, pass);
        PreparedStatement stmt = conn.prepareStatement("SELECT configKey, configValue FROM SystemConfig");
        ResultSet rs = stmt.executeQuery())
        {
            while (rs.next())
            {
                //Pull the config stuff I need.
                config.put(rs.getString("configKey"), rs.getString("configValue"));
            }
        }

        return config;
    }   
}