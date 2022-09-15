package com.example.buyticket.controller;

import com.example.buyticket.service.BuyTicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("ticket")
public class BuyTicketController {

    @Autowired
    private BuyTicketService buyTicketService;

//    抢购车票
    @GetMapping(value = "/buyTicket")
    public String buyTicket(@RequestParam("userId") String userId,
                          @RequestParam("num") Integer num) throws InterruptedException {


         boolean flag = buyTicketService.buyTicket(userId,num);
         if (flag){
             return "抢票成功！";
         }else{
             return "抱歉，票已卖完";
         }


    }

//    上架车票
    @GetMapping(value = "/onTicket")
    public String onTicket() {
        buyTicketService.onTicket();
        return "success";
    }

}
