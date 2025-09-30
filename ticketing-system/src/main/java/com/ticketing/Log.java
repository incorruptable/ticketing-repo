package com.ticketing;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

public class Log {

    public static void LogToFile(Exception e)
    {
        try
        {
        String currentDirectory = System.getProperty("user.dir");
        
        File folder = new File(currentDirectory+"/Logs");
        if(!folder.exists())
        {
            folder.mkdirs();
        }
        
        File logFile = new File(folder, "logs.log");

        try(PrintWriter writer = new PrintWriter(new FileWriter(logFile, true))) {
            writer.println(java.time.LocalDateTime.now() + ": "+ e.toString());
        }

        } catch (Exception io) {
            e.printStackTrace();
        }
    }
}
