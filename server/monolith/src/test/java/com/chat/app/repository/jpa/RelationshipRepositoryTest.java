package com.chat.app.repository.jpa;

import com.chat.app.enumeration.RelationshipStatus;
import com.chat.app.model.entity.Account;
import com.chat.app.model.entity.Relationship;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles("test")
class RelationshipRepositoryTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private RelationshipRepository relationshipRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    public void testFindByAccountAndStatus() {
//        Account account1 = accountRepository.findById(3L).get();
//        entityManager.persist(account1);
//        Account account2 = accountRepository.findById(4L).get();
//        entityManager.persist(account2);
//        Relationship relationship = new Relationship();
//        relationship.setFirstAccount(account1);
//        relationship.setSecondAccount(account2);
//        relationship.setStatus(RelationshipStatus.ACCEPTED);
//        entityManager.persist(relationship);
//        entityManager.flush();
        List<Relationship> result = relationshipRepository.findByAccountAndStatus(3L, RelationshipStatus.ACCEPTED);
        assertEquals(2, result.size());
        assertEquals(RelationshipStatus.ACCEPTED, result.getFirst().getStatus());
        Query query = entityManager.createQuery("SELECT r FROM Relationship r " +
                "WHERE (r.firstAccount.accountId = :accountId OR r.secondAccount.accountId = :accountId) " +
                "AND r.status = :status");
        query.setParameter("accountId", 1L);
        query.setParameter("status", RelationshipStatus.ACCEPTED);
        System.out.println(query.unwrap(org.hibernate.query.Query.class).getQueryString());
    }
}