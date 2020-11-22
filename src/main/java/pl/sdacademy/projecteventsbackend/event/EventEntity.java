package pl.sdacademy.projecteventsbackend.event;

import pl.sdacademy.projecteventsbackend.user.model.UserEntity;

import javax.persistence.*;

@Entity
public class EventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    @Enumerated(EnumType.STRING)
    private AccessToEvent accessToEvent;

    @ManyToOne
    @JoinColumn(name = "address_id")
    private AddressEntity address;

    @ManyToOne
    @JoinColumn(name = "organizer_id")
    private UserEntity organizer;


    public EventEntity(String name, String description, AccessToEvent accessToEvent, AddressEntity address) {
        this.name = name;
        this.description = description;
        this.accessToEvent = accessToEvent;
        this.address = address;

    }

    public EventEntity() {
    }


    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription(String description) {
        return description;
    }

    public AddressEntity getAddress() {
        return address;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAddress(AddressEntity address) {
        this.address = address;
    }

    public void setOrganizer(UserEntity organizerId) {
        this.organizer = organizerId;
    }
    public AccessToEvent getAccessToEvent() {
        return accessToEvent;
    }

    public void setAccessToEvent(AccessToEvent accessToEvent) {
        this.accessToEvent = accessToEvent;
    }


    public enum AccessToEvent {
        PUBLIC,
        PRIVATE
    }
}
