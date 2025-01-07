package com.chat.app.repository.jpa;


import com.chat.app.model.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m WHERE m.unsend = true")
    List<Message> findAllByUnsendTrue();

    Message findByRandomId(String randomId);
}
