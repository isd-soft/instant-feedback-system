package com.inther.controllers;

import com.inther.assets.validators.RequestDataValidator;
import com.inther.services.mappers.MessageMapper;
import com.inther.dto.MessageDto;
import com.inther.entities.Message;
import com.inther.repositories.PresentationRepository;
import com.inther.services.entity.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin(origins="*", maxAge = 3600)
@RestController
@RequestMapping(value = "/api/messages")
public class MessageController
{
    private final MessageService messageService;
    private final MessageMapper messageMapper;
    private final HttpHeaders httpHeaders;
    private final PresentationRepository presentationRepository;

    //    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PostMapping
    public ResponseEntity<?> addMessage(
            @Validated(value = {RequestDataValidator.AddMessage.class})
            @RequestBody MessageDto messageDto)
    {
        return messageService.addMessage(messageMapper.toEntity(messageDto))
                .map(messageAdded -> messageAdded
                        ? new ResponseEntity<>( "Presentation not started yet!", httpHeaders, HttpStatus.FORBIDDEN)
                        : new ResponseEntity<>( "Message successfully added!", httpHeaders, HttpStatus.ACCEPTED))
                .orElseGet(() -> new ResponseEntity<>("No such presentationId.", httpHeaders, HttpStatus.NOT_FOUND));
    }

    @GetMapping(params = "presentationId")
    public ResponseEntity<?> getMessagesByPresentation(
            @RequestParam(value = "presentationId") String id)
    {
        if (presentationRepository.findPresentationById(UUID.fromString(id)).isPresent()) {
            List<Message> msgList = messageService.fetchMessagesByPresentationId(UUID.fromString(id));
            return new ResponseEntity<>(msgList, httpHeaders, msgList.isEmpty()
                    ? HttpStatus.NO_CONTENT
                    : HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Presentation not found!", httpHeaders, HttpStatus.NOT_FOUND);
        }
    }

    //    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @PutMapping
    public ResponseEntity<?> editMessage(
            @Validated(value = {RequestDataValidator.UpdateMessage.class})
            @RequestBody MessageDto messageDtoToPatch)
    {
        return messageService.editMessage(messageMapper.toEntity(messageDtoToPatch))
                .map(messageEdited -> messageEdited
                        ? new ResponseEntity<>( "You don't have enough rights to do this!", httpHeaders, HttpStatus.FORBIDDEN)
                        : new ResponseEntity<>( "Message successfully edited!", httpHeaders, HttpStatus.ACCEPTED))
                .orElseGet(() -> new ResponseEntity<>("No such text!", httpHeaders, HttpStatus.NOT_FOUND));
    }

    //    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteMessage(@PathVariable String id)
    {
        return messageService.deleteMessage(UUID.fromString(id))
                .map(messageDeleted -> messageDeleted
                        ? new ResponseEntity<>( "You don't have enough rights to do this!", httpHeaders, HttpStatus.FORBIDDEN)
                        : new ResponseEntity<>( "Message successfully deleted!", httpHeaders, HttpStatus.ACCEPTED))
                .orElseGet(() -> new ResponseEntity<>("No such text!", httpHeaders, HttpStatus.NOT_FOUND));
    }

    @Autowired
    public MessageController(MessageService messageService,
                             MessageMapper messageMapper,
                             HttpHeaders httpHeaders,
                             PresentationRepository presentationRepository)
    {
        this.messageService = messageService;
        this.messageMapper = messageMapper;
        this.httpHeaders = httpHeaders;
        this.presentationRepository = presentationRepository;
    }
}