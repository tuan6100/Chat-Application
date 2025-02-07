package com.chat.app.service.interfaces.message.kafka;

import com.chat.app.exception.ChatException;
import com.chat.app.payload.request.*;
import org.springframework.stereotype.Service;

@Service
public interface MessageProducerService {

     void produceMessageSending(Long chatId, NewMessageRequest newMessageRequest);

     void produceMessageConfirmation(Long chatId, MessageConfirmationRequest messageConfirmationRequest) ;

     void produceMessageMarkedAsViewing(Long chatId, MessageSeenRequest messageSeenRequest) ;

     void produceMessageUpdating(Long chatId, MessageUpdateRequest messageUpdateRequest) ;

     void produceMessageDeleting(Long chatId, MessageRequest messageRequest) ;
     void produceMessageRestoring(Long chatId, MessageRequest messageRequest) ;




}
