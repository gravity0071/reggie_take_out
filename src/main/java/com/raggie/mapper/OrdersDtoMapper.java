package com.raggie.mapper;

import com.raggie.dto.OrdersDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrdersDtoMapper {
    public List<OrdersDto> searchForOther(@Param("id") Long id);
}
