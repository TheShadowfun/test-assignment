package com.henrikpeegel.test_assignment.mapper;

import com.henrikpeegel.test_assignment.domain.Transaction;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface TransactionMapper {
    void insertTransaction(Transaction transaction);

    List<Transaction> findAllByAccountId(Long accountId);
}
