package com.ticketing;

import jakarta.mail.*;
import java.util.Arrays;


public class Ticket
{
    //Instance variables
    String ticketSender;
    String ticketOwner;
    String ticketBody;
    int ticketNum;
    int ticketPriority;

    //TODO: Update to include things such as ticket duration, technician assignment, priority adjustment

    //basic constructor
    public Ticket(Address[] ticketSender, String ticketOwner, String ticketBody, int ticketNum, int ticketPriority)
    {
        this.ticketSender = getSenderAsString(ticketSender);
        this.ticketOwner = ticketOwner;
        this.ticketBody = ticketBody;
        this.ticketNum = ticketNum;
        this.ticketPriority = ticketPriority;
    }

    private String getSenderAsString(Address[] ticketSender)
    {
        String[] senderArray = Arrays.stream(ticketSender).map(Address::toString).toArray(String[]::new);
        String joinedString = "";

        for(String sender : senderArray)
        {
            joinedString = String.join(", ", sender);
        }
        return joinedString;
    }

    public String getTicketSender()
    {
        return ticketSender;
    }
    public String getTicketOwner()
    {
        return ticketOwner;
    }
    public int getTicketNumber()
    {
        return ticketNum;
    }

    public String getTicketBody()
    {
        return ticketBody;
    }

    public int getTicketPriority()
    {
        return ticketPriority;
    }
}