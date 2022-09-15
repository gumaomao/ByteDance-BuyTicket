package com.example.buyticket.service;



public interface BuyTicketService {


    boolean buyTicket(String userId, Integer num) throws InterruptedException;

    void onTicket();
}
