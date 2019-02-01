package com.inther.services;

import com.inther.beans.AuthorityUtilityBean;
import com.inther.beans.ResponseBean;
import com.inther.beans.ServiceUtilityBean;
import com.inther.entities.implementation.MessageEntity;
import com.inther.exceptions.AccessDeniedException;
import com.inther.exceptions.NotFoundEntryException;
import com.inther.repositories.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class MessageService
{
    private final AuthorityUtilityBean authorityUtilityBean;
    private final ServiceUtilityBean serviceUtilityBean;
    private final MessageRepository messageRepository;
    private final ResponseBean responseBean;
    private final HttpHeaders httpHeaders;

    public ResponseBean putMessage(MessageEntity messageEntity) throws Exception
    {
        if (authorityUtilityBean.getCurrentAuthenticationEmail().equals(messageEntity.getEmail()))
        {
            messageRepository.save(messageEntity);
            responseBean.setHeaders(httpHeaders);
            responseBean.setStatus(HttpStatus.CREATED);
            responseBean.setResponse("Message for presentation with id " + messageEntity.getPresentationId() + " successfully added");
        }
        else
        {
            throw new AccessDeniedException("Access denied for you authority");
        }
        return responseBean;
    }
    public ResponseBean patchMessage(MessageEntity messageEntity) throws Exception
    {
        Optional<MessageEntity> optionalMessageEntity = messageRepository.findMessageEntityByMessageId(messageEntity.getMessageId());
        if (optionalMessageEntity.isPresent())
        {
            if (authorityUtilityBean.getCurrentAuthenticationEmail().equals(optionalMessageEntity.get().getEmail()))
            {
                messageRepository.save(serviceUtilityBean.patchEntity(optionalMessageEntity.get(), messageEntity));
                responseBean.setHeaders(httpHeaders);
                responseBean.setResponse("User " + messageEntity.getEmail() + " successfully patched");
            }
            else
            {
                throw new AccessDeniedException("Access denied for you authority");
            }
        }
        else
        {
            throw new NotFoundEntryException("Message with id " + messageEntity.getMessageId() + " not found");
        }
        return responseBean;
    }
    public ResponseBean deleteMessage(Integer messageId) throws Exception
    {
        Optional<MessageEntity> optionalMessageEntity = messageRepository.findMessageEntityByMessageId(messageId);
        if (optionalMessageEntity.isPresent())
        {
            if (authorityUtilityBean.getCurrentAuthenticationEmail().equals(optionalMessageEntity.get().getEmail()) || authorityUtilityBean.validateAdminAuthority())
            {
                messageRepository.deleteMessageEntityByMessageId(messageId);
                responseBean.setHeaders(httpHeaders);
                responseBean.setStatus(HttpStatus.OK);
                responseBean.setResponse("Message with id " + messageId + " successfully deleted");
            }
            else
            {
                throw new AccessDeniedException("Access denied for you authority");
            }
        }
        else
        {
            throw new NotFoundEntryException("Message with id " + messageId + " not found");
        }
        return responseBean;
    }

    @Autowired
    public MessageService(AuthorityUtilityBean authorityUtilityBean, ServiceUtilityBean serviceUtilityBean,
                          MessageRepository messageRepository, ResponseBean responseBean, HttpHeaders httpHeaders)
    {
        this.authorityUtilityBean = authorityUtilityBean;
        this.serviceUtilityBean = serviceUtilityBean;
        this.messageRepository = messageRepository;
        this.responseBean = responseBean;
        this.httpHeaders = httpHeaders;
    }
}