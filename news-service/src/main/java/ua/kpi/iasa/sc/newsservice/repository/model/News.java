package ua.kpi.iasa.sc.newsservice.repository.model;

import ua.kpi.iasa.sc.newsservice.api.dto.NewsDTO;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "news")
public final class News {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    @Column(nullable = false)
    private String imageLink;
    @Column(nullable = false, length = 50)
    private String title;
    @Column(nullable = false)
    private String text;
    @Column
    private String link;
    @Column(nullable = false)
    private Timestamp createdAt;
    @Column
    private Long createdBy;
    @Column
    private boolean deleted;

    public News() {  }

    public News(String imageLink, String title, String text, String link, Timestamp createdAt, long createdBy, boolean deleted) {
        this.imageLink = imageLink;
        this.title = title;
        this.text = text;
        this.link = link;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.deleted = deleted;
    }

    public News(NewsDTO newsDTO, long createdBy){
        this.createdBy = createdBy;
        this.createdAt = new Timestamp(System.currentTimeMillis());
        this.deleted = false;
        this.title = newsDTO.getTitle();
        this.imageLink = newsDTO.getImageLink();
        this.link = newsDTO.getLink();
        this.text = newsDTO.getText();
    }

    public long getId() {
        return id;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}