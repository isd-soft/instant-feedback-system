package com.inther.beans.utilities;

import com.inther.entities.implementation.ParticipantEntity;
import com.inther.entities.implementation.PresentationEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class EmailUtilityBean
{
    private final JavaMailSender javaMailSender;

    public void sendPutParticipantNotificationEmail(ParticipantEntity participantEntity)
    {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(participantEntity.getEmail());
        simpleMailMessage.setSubject("Your joined presentation");
        simpleMailMessage.setText("Your joined presentation with id: '" + participantEntity.getPresentationId() + "'");
        javaMailSender.send(simpleMailMessage);
    }
    public void sendDeletePresentationNotificationEmails(PresentationEntity presentationEntity)
    {
        for (ParticipantEntity participantEntity : presentationEntity.getParticipants())
        {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setTo(participantEntity.getEmail());
            simpleMailMessage.setSubject("Presentation was canceled");
            simpleMailMessage.setText("Presentation with title: '" + presentationEntity.getTitle() + "' was canceled");
            javaMailSender.send(simpleMailMessage);
        }
    }

    @Autowired
    public EmailUtilityBean(JavaMailSender javaMailSender)
    {
        this.javaMailSender = javaMailSender;
    }
}