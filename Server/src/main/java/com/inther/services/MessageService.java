package com.inther.services;

import com.inther.beans.utilities.AuthorityUtilityBean;
import com.inther.beans.ResponseBean;
import com.inther.beans.utilities.ServiceUtilityBean;
import com.inther.entities.implementation.MessageEntity;
import com.inther.entities.implementation.PresentationEntity;
import com.inther.exceptions.AccessDeniedException;
import com.inther.exceptions.NotFoundEntryException;
import com.inther.repositories.MessageRepository;
import com.inther.repositories.PresentationRepository;
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
    private final PresentationRepository presentationRepository;
    private final MessageRepository messageRepository;
    private final ResponseBean responseBean;
    private final HttpHeaders httpHeaders;

    private MessageEntity setAuthenticatedEmailPropertyValue(MessageEntity targetEntity)
    {
        if (targetEntity.getAnonymous())
        {
            targetEntity.setEmail("ANONYMOUS");
        }
        else
        {
            targetEntity.setEmail(authorityUtilityBean.getCurrentAuthenticationEmail());
        }
        return targetEntity;
    }

    public ResponseBean putMessage(MessageEntity messageEntity) throws Exception
    {

        Optional<PresentationEntity> optionalPresentationEntity = presentationRepository
                .findPresentationEntityById(messageEntity.getPresentationId());
        if (optionalPresentationEntity.isPresent())
        {
            messageRepository.save(setAuthenticatedEmailPropertyValue(messageEntity));
            responseBean.setHeaders(httpHeaders);
            responseBean.setStatus(HttpStatus.CREATED);
            responseBean.setResponse("Message for presentation with id: '" + messageEntity.getPresentationId() + "' successfully added");
        }
        else
        {
            throw new NotFoundEntryException("Presentation with id: '" + messageEntity.getPresentationId() + "' not found");
        }
        return responseBean;
    }
    public ResponseBean patchMessage(MessageEntity messageEntity) throws Exception
    {
        Optional<MessageEntity> optionalMessageEntity = messageRepository.findMessageEntityById(messageEntity.getId());
        if (optionalMessageEntity.isPresent())
        {
            if (authorityUtilityBean.getCurrentAuthenticationEmail().equals(optionalMessageEntity.get().getEmail()))
            {
                messageRepository.save(serviceUtilityBean.patchEntity(optionalMessageEntity.get(), messageEntity));
                responseBean.setHeaders(httpHeaders);
                responseBean.setResponse("Message with id: '" + messageEntity.getId() + "' successfully patched");
            }
            else
            {
                throw new AccessDeniedException("Access denied for you authority");
            }
        }
        else
        {
            throw new NotFoundEntryException("Message with id: '" + messageEntity.getId() + "' not found");
        }
        return responseBean;
    }
    public ResponseBean deleteMessage(Integer id) throws Exception
    {
        Optional<MessageEntity> optionalMessageEntity = messageRepository.findMessageEntityById(id);
        if (optionalMessageEntity.isPresent())
        {
            if (authorityUtilityBean.getCurrentAuthenticationEmail().equals(optionalMessageEntity.get().getEmail()) || authorityUtilityBean.validateAdminAuthority())
            {
                messageRepository.deleteMessageEntityById(id);
                responseBean.setHeaders(httpHeaders);
                responseBean.setStatus(HttpStatus.OK);
                responseBean.setResponse("Message with id: '" + id + "' successfully deleted");
            }
            else
            {
                throw new AccessDeniedException("Access denied for you authority");
            }
        }
        else
        {
            throw new NotFoundEntryException("Message with id: '" + id + "' not found");
        }
        return responseBean;
    }

    @Autowired
    public MessageService(AuthorityUtilityBean authorityUtilityBean, ServiceUtilityBean serviceUtilityBean,
                          PresentationRepository presentationRepository, MessageRepository messageRepository, ResponseBean responseBean, HttpHeaders httpHeaders)
    {
        this.authorityUtilityBean = authorityUtilityBean;
        this.serviceUtilityBean = serviceUtilityBean;
        this.presentationRepository = presentationRepository;
        this.messageRepository = messageRepository;
        this.responseBean = responseBean;
        this.httpHeaders = httpHeaders;
    }
}