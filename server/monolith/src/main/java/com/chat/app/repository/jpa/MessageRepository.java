package com.chat.app.repository.jpa;


import com.chat.app.model.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m WHERE m.randomId = ?1")
    Message findByRandomId(String randomId);

    @Query("SELECT m FROM Message m WHERE m.unsent = true")
    Stream<Message> findStreamByUnsentTrue();
}
