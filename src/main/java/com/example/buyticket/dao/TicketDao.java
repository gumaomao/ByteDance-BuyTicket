package com.example.buyticket.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.buyticket.entity.Ticket;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

@Mapper
public interface TicketDao extends BaseMapper<Ticket> {

    @Update("update ticket set num = num - #{num} where id = #{id}")
    void debit(int id, int num);
}
