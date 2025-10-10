package com.ticketing;
import java.util.logging.Logger;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SpringBootApplication
public class Main {
    //Initialize the logger.
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    //TODO: Rest of the logic. REST Endpoints.

    public static void main(String[] args) {
        //Initialize and run the email listener in a new thread.
        new Thread(() -> {
            int retries = 0;
            while (retries < 3)
            {
                try{
                    Utils.emailLogin(); //Tests if the email can login. If it can, logs it in.
                    retries = 0;
                    EmailUtil.emailListener();
                    
                }
                catch (Exception e)
                {
                    retries++;
                    logger.warning("Email connection failed. Retrying in 10 seconds...");
                    try {Thread.sleep(10000);}
                    catch(InterruptedException ex)
                        {
                            Thread.currentThread().interrupt();
                            return;
                        }
                }
            }
        }).start();
    }
}