package com.henrikpeegel.test_assignment.mapper;

import com.henrikpeegel.test_assignment.domain.OutboxEntry;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OutboxMapper {
    void insert(OutboxEntry entry);

    List<OutboxEntry> findPending(@Param("limit") int limit);

    void updateStatus(@Param("id") Long id, @Param("status") String status);

    void deleteOldSentMessages(@Param("olderThan") LocalDateTime olderThan);
}