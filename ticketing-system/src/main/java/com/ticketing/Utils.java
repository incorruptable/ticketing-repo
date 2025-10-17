package com.ticketing;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import jakarta.mail.AuthenticationFailedException;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;


public class Utils 
{
    private static final Logger log = LoggerFactory.getLogger(Utils.class);

    static void emailLogin() throws Exception
    {
        //Checks if database email can log in. If not, prompts for credentials.

        String[] roots = {"email_address", "email_password", "email_host", "email_port", "email_protocol"};

        Map<String,String> sysConfig = loadConfig("config.yml", roots);

        Properties props = new Properties();

        props.put("mail.store.protocol",sysConfig.get("email_protocol"));
        props.put("mail.imaps.host",sysConfig.get("email_host"));
        props.put("mail.imaps.port",sysConfig.get("email_port"));
        props.put("mail.imaps.ssl.trust",sysConfig.get("email_host"));

        Session session = Session.getInstance(props);

        try (Transport transport = session.getTransport("smtps")) 
        {
            int smtpPort = Integer.parseInt(sysConfig.get("email_port")); // This kept throwing an error while inside the transport.connect. This circumvents that.
            transport.connect(sysConfig.get("email_host"), smtpPort, sysConfig.get("email_address"), sysConfig.get("email_password"));
            //Connection check. Success means it's logged in.
        }
        catch (NumberFormatException e)
        {
            log.error("Invalid email port in config.yml", e);
        }
        catch (AuthenticationFailedException e)
        {
            log.error("SMTP Authentication failed: ", e);
        }
        catch (MessagingException e)
        {
            log.error("SMTP or protocol error.", e);
        }
        catch (Exception e)
        {
            log.error("Unexpected error during email login attempt.",e);
        }
    }

    public static Map<String, String> loadConfig(String filePath, String[] roots) throws IOException
    {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        JsonNode root = mapper.readTree(new File(filePath));
        Map<String, String> configMap = new HashMap<>();

        for(String key : roots)
        {
            JsonNode value = root.path(key);
            configMap.put(key, value.isMissingNode() ? "" : value.asText(""));
        }
        return configMap;
    }
}
