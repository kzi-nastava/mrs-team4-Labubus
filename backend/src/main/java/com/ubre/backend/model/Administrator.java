package com.ubre.backend.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("ADMINISTRATOR")
public class Administrator extends User {

    @OneToMany(mappedBy = "administrator", cascade = CascadeType.ALL)
    private List<UserNote> userNotes = new ArrayList<>();

    @OneToMany(mappedBy = "resolver", cascade = CascadeType.ALL)
    private List<PanicAlert> resolvedPanicAlerts = new ArrayList<>();

    // Constructors
    public Administrator() {
        super();
    }

    public Administrator(String email, String password, String firstName, String lastName, String address, String phoneNumber) {
        super(email, password, firstName, lastName, address, phoneNumber);
    }

    // Getters and Setters
    public List<UserNote> getUserNotes() {
        return userNotes;
    }

    public void setUserNotes(List<UserNote> userNotes) {
        this.userNotes = userNotes;
    }

    public List<PanicAlert> getResolvedPanicAlerts() {
        return resolvedPanicAlerts;
    }

    public void setResolvedPanicAlerts(List<PanicAlert> resolvedPanicAlerts) {
        this.resolvedPanicAlerts = resolvedPanicAlerts;
    }
}
