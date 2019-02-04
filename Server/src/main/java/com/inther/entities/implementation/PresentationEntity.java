package com.inther.entities.implementation;

import com.inther.entities.Entities;
import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "presentations")
public class PresentationEntity implements Entities
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "email")
    private String email;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "start_date")
    private Date startDate;

    @Column(name = "end_date")
    private Date endDate;

    @Column(name = "place")
    private String place;

    @OneToMany(targetEntity = ParticipantEntity.class, mappedBy = "presentationId", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ParticipantEntity> presentationParticipants;

    @OneToMany(targetEntity = MessageEntity.class, mappedBy = "presentationId", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<MessageEntity> presentationMessages;

    @OneToMany(targetEntity = MarkEntity.class, mappedBy = "presentationId", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<MarkEntity> presentationMarks;

    public Integer getId()
    {
        return id;
    }
    public void setId(Integer id)
    {
        this.id = id;
    }
    public String getEmail()
    {
        return email;
    }
    public void setEmail(String email)
    {
        this.email = email;
    }
    public String getTitle()
    {
        return title;
    }
    public void setTitle(String title)
    {
        this.title = title;
    }
    public String getDescription()
    {
        return description;
    }
    public void setDescription(String description)
    {
        this.description = description;
    }
    public Date getStartDate()
    {
        return startDate;
    }
    public void setStartDate(Date startDate)
    {
        this.startDate = startDate;
    }
    public Date getEndDate()
    {
        return endDate;
    }
    public void setEndDate(Date endDate)
    {
        this.endDate = endDate;
    }
    public String getPlace()
    {
        return place;
    }
    public void setPlace(String place)
    {
        this.place = place;
    }
    public List<ParticipantEntity> getPresentationParticipants()
    {
        return presentationParticipants;
    }
    public void setPresentationParticipants(List<ParticipantEntity> presentationParticipants)
    {
        this.presentationParticipants = presentationParticipants;
    }
    public List<MessageEntity> getPresentationMessages()
    {
        return presentationMessages;
    }
    public void setPresentationMessages(List<MessageEntity> presentationMessages)
    {
        this.presentationMessages = presentationMessages;
    }
    public List<MarkEntity> getPresentationMarks()
    {
        return presentationMarks;
    }
    public void setPresentationMarks(List<MarkEntity> presentationMarks)
    {
        this.presentationMarks = presentationMarks;
    }
}