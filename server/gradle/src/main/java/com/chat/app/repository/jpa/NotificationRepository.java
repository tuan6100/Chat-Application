package com.chat.app.repository.jpa;

import com.chat.app.model.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("SELECT n FROM Notification n WHERE n.receiverAccount.accountId = ?1")
    List<Notification> findByReceiverAccountId(Long userId);
}