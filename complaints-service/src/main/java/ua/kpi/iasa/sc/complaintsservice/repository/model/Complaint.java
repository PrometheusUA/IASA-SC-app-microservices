package ua.kpi.iasa.sc.complaintsservice.repository.model;

import ua.kpi.iasa.sc.complaintsservice.api.dto.ComplaintDTO;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name="complaints")
public class Complaint {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column
    private String firstname;
    @Column
    private String surname;
    @Column
    private String patronymic;
    @Column(nullable = false)
    private String text;
    @Column(nullable = false)
    private Timestamp createdAt;
    @Column
    private Long processedById;

    public Complaint() {
    }

    public Complaint(String firstname, String surname, String patronymic, String text) {
        this.firstname = firstname;
        this.surname = surname;
        this.patronymic = patronymic;
        this.text = text;
        this.createdAt = new Timestamp(System.currentTimeMillis());
        this.processedById = null;
    }

    public Complaint(ComplaintDTO complaint) {
        this.firstname = complaint.getFirstname();
        this.surname = complaint.getSurname();
        this.patronymic = complaint.getPatronymic();
        this.text = complaint.getText();
        this.createdAt = new Timestamp(System.currentTimeMillis());
        this.processedById = null;
    }

    public long getId() {
        return id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Long getProcessedById() {
        return processedById;
    }

    public void setProcessedById(Long processedById) {
        this.processedById = processedById;
    }
}

