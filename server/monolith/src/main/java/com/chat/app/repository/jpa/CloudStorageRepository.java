package com.chat.app.repository.jpa;

import com.chat.app.model.entity.extend.chat.CloudStorage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CloudStorageRepository extends JpaRepository<CloudStorage, Long> {

    @Query("SELECT c FROM CloudStorage c WHERE c.account.accountId = ?1")
    CloudStorage findCloudStorageByAccountId(Long accountId);

}