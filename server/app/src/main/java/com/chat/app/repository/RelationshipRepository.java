package com.chat.app.repository;

import com.chat.app.enumeration.RelationshipStatus;
import com.chat.app.model.entity.Account;
import com.chat.app.model.entity.Relationship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RelationshipRepository extends JpaRepository<Relationship, Long> {

    public Relationship findByUserAndFriend(Account user, Account friend);

    @Query("SELECT r FROM Relationship r WHERE r.user.accountId = ?1 AND r.status = 'ACCEPTED'")
    public List<Relationship> findFriend(Long accountId);

    @Query("SELECT r.friend.accountId FROM Relationship r WHERE r.user.accountId = ?1 AND r.status = 'WAITING_TO_ACCEPT'")
    List<Long> getFriends(Long userId);


}
