package com.chat.app.service.implementations.chat;

import com.chat.app.enumeration.MessageType;
import com.chat.app.exception.ChatException;
import com.chat.app.model.entity.Account;
import com.chat.app.model.entity.extend.chat.CloudStorage;
import com.chat.app.payload.request.NewMessageRequest;
import com.chat.app.payload.response.ChatResponse;
import com.chat.app.repository.jpa.CloudStorageRepository;
import com.chat.app.service.interfaces.chat.CloudStorageService;
import com.chat.app.service.interfaces.system.aws.S3Service;
import com.chat.app.service.interfaces.user.information.AccountSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class CloudStorageServiceImpl extends ChatServiceImpl implements CloudStorageService {

    @Autowired
    private CloudStorageRepository cloudStorageRepository;

    @Autowired
    private AccountSearchService accountSearchService;

    @Autowired
    private S3Service s3Service;


    @Override
    public ChatResponse getCloudStorageByAccountId(Long accountId) throws ChatException {
        Account account = accountSearchService.searchAccountById(accountId);
        CloudStorage cloudStorage = cloudStorageRepository.findCloudStorageByAccountId(accountId);
        return new ChatResponse(cloudStorage.getChatId(), account.getUsername(), account.getAvatar(),
                ChatResponse.LatestMessage.fromResponse(accountId, getLastestMessage(cloudStorage.getChatId())));
    }

    @Override
    public void checkFullStorage(Long accountId, NewMessageRequest messageRequest) throws ChatException {
        CloudStorage cloudStorage = cloudStorageRepository.findCloudStorageByAccountId(accountId);
        Long messageRequestSize = (Objects.equals(messageRequest.getType(), MessageType.TEXT.toString())) ?
            messageRequest.getContent().getBytes().length :
                s3Service.getFileSize(messageRequest.getContent());
        if (cloudStorage.getStorageUsed() + messageRequestSize > cloudStorage.MAX_STORAGE_SIZE) {
            throw new ChatException("Storage is full");
        }
    }

    @Override
    public void getMoreStorage(Long accountId, Long storageSize) {
        CloudStorage cloudStorage = cloudStorageRepository.findCloudStorageByAccountId(accountId);
        cloudStorage.setStorageUsed(cloudStorage.getStorageUsed() + storageSize);
        cloudStorageRepository.save(cloudStorage);
    }
}
