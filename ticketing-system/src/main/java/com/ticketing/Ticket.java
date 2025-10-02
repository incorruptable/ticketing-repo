package com.ticketing;

public class Ticket
{
    //Instance variables
    String ticketSender;
    String ticketOwner;
    String ticketBody;
    int ticketNum;
    int ticketPriority;

    //basic constructor
    public Ticket(String ticketSender, String ticketOwner, String ticketBody, int ticketNum, int ticketPriority)
    {
        this.ticketSender = ticketSender;
        this.ticketOwner = ticketOwner;
        this.ticketBody = ticketBody;
        this.ticketNum = ticketNum;
        this.ticketPriority = ticketPriority;
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