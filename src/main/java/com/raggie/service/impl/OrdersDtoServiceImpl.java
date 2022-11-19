package com.raggie.service.impl;

import com.raggie.dto.OrdersDto;
import com.raggie.mapper.OrdersDtoMapper;
import com.raggie.service.OrdersDtoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrdersDtoServiceImpl implements OrdersDtoService {
    @Autowired
    private OrdersDtoMapper ordersDtoMapper;

    @Override
    @Transactional
    public List<OrdersDto> getOrderDto(Long id) {
        return ordersDtoMapper.searchForOther(id);
    }
}
