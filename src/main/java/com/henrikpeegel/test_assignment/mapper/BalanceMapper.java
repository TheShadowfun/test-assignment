package com.henrikpeegel.test_assignment.mapper;

import com.henrikpeegel.test_assignment.domain.Balance;
import com.henrikpeegel.test_assignment.domain.Currency;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

@Mapper
public interface BalanceMapper {
    void insertBalances(List<Balance> balances);

    Balance findBalanceForUpdate(@Param("accountId") Long accountId, @Param("currency") Currency currency);

    int updateBalance(@Param("id") Long id, @Param("availableAmount") BigDecimal availableAmount);
}
