package com.example.buyticket.entity;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("ticket")
public class Ticket {
    @TableId
    private Integer id;


    private Integer num;
}
