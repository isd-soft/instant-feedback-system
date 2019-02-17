package com.inther.controllers;

import com.inther.assets.validators.RequestDataValidator;
import com.inther.services.mappers.ParticipantMapper;
import com.inther.dto.ParticipantDto;
import com.inther.entities.Participant;
import com.inther.entities.Presentation;
import com.inther.repositories.PresentationRepository;
import com.inther.services.entity.ParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/participants")
public class ParticipantController
{
    private final HttpHeaders httpHeaders;
    private final ParticipantService participantService;
    private final ParticipantMapper participantMapper;
    private final PresentationRepository presentationRepository;

    @GetMapping(params = "presentationId")
    public List<ParticipantDto> getPresentationParticipants(
            @RequestParam(value = "presentationId") String presentationId) {
        return participantService
                .fetchPresentationParticipants(UUID.fromString(presentationId))
                .stream()
                .map(participantMapper::toDto)
                .collect(Collectors.toList());
    }

    @PutMapping
    public ResponseEntity<?> putParticipant(
            @Validated(value = {RequestDataValidator.AddParticipant.class})
            @RequestBody List<ParticipantDto> participantDto)
    {
        List<Participant> newParticipantList = participantDto
                .stream()
                .map(participantMapper::toEntity)
                .collect(Collectors.toList());

        for (Participant newParticipant : newParticipantList)
        {
            participantService.addParticipant(newParticipant);

            Optional<Presentation> optionalPresentation = presentationRepository
                    .findPresentationById(newParticipant.getPresentation().getId());

            optionalPresentation.ifPresent(presentation ->
                participantService.sendNotificationMessages(newParticipant.getEmail(), "You have been invited to a presentation",
                         "Presentation name: " + presentation.getTitle()
                            + "n/Presentation description: " + presentation.getDescription()
                            + "n/n/Presentation start time: " + presentation.getStartTime()
                            + "n/n/Presentation end time: " + presentation.getStartTime()
                            + "n/n/Presentation place: " + presentation.getPlace())
            );
        }

        return new ResponseEntity<>(httpHeaders, HttpStatus.CREATED);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteParticipant(@PathVariable String id)
    {
        Optional<Boolean> participantRemovalStatus = participantService.deleteParticipant(UUID.fromString(id));

        return participantRemovalStatus.map(deleted -> deleted
                ? new ResponseEntity<>("Participant successfully deleted.", httpHeaders, HttpStatus.OK)
                : new ResponseEntity<>("Unable to delete participant", httpHeaders, HttpStatus.CONFLICT))
                .orElseGet(() -> new ResponseEntity<>("Participant not found.", httpHeaders, HttpStatus.NOT_FOUND));
    }

    @Autowired
    public ParticipantController(HttpHeaders httpHeaders,
                                 ParticipantService participantService,
                                 ParticipantMapper participantMapper,
                                 PresentationRepository presentationRepository)
    {
        this.httpHeaders = httpHeaders;
        this.participantService = participantService;
        this.participantMapper = participantMapper;
        this.presentationRepository = presentationRepository;
    }
}