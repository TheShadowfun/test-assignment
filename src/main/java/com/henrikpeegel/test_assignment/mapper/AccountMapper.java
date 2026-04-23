package com.henrikpeegel.test_assignment.mapper;

import com.henrikpeegel.test_assignment.domain.Account;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccountMapper {
    // This method will link to an <insert> tag in XML
    void insertAccount(Account account);

    // This allows us to fetch an account by ID for the "Get account" API
    Account findById(Long id);
}