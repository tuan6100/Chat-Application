package com.chat.app.repository.jpa;

import com.chat.app.enumeration.RelationshipStatus;
import com.chat.app.model.entity.Relationship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RelationshipRepository extends JpaRepository<Relationship, Long> {

    @Query("SELECT r.relationshipId FROM Relationship r " +
            "WHERE (r.firstAccount.accountId = ?1 AND r.secondAccount.accountId = ?2)")
    Long findByFirstAccountAndSecondAccount(Long firstAccountId, Long secondAccountId);

    @Query("SELECT r FROM Relationship r " +
            "WHERE (r.firstAccount.accountId = ?1 OR r.secondAccount.accountId = ?1) " +
            "   AND r.status = ?2")
    List<Relationship> findByAccountAndStatus(Long accountId, RelationshipStatus status);

    @Query("SELECT r FROM Relationship r " +
            "WHERE r.firstAccount.accountId = ?1 OR r.secondAccount.accountId = ?1")
    List<Relationship> findAllFriendsByAccount(Long accountId);

}

