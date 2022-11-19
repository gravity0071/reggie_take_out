package com.raggie.service;

import com.raggie.dto.OrdersDto;
import com.raggie.mapper.OrdersDtoMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public interface OrdersDtoService {
   public List<OrdersDto> getOrderDto(Long id);
}
