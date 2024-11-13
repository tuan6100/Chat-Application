//package com.chat.app.service;
//
//import com.chat.app.model.entity.Message;
//import com.chat.app.repository.MessageRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//public class MessageService {
//    private final MessageRepository messageRepository;
//
//    @Autowired
//    public MessageService(MessageRepository messageRepository) {
//        this.messageRepository = messageRepository;
//    }
//
//    public List<Message> getMessagesByChatRoomId(Long chatRoomId) {
//        return messageRepository.findByChatRoomId(chatRoomId);
//    }
//
//    public Message saveMessage(Message message) {
//        return messageRepository.save(message);
//    }
//
//}
