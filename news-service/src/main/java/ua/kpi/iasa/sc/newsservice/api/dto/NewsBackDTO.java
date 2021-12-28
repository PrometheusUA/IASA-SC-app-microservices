package ua.kpi.iasa.sc.newsservice.api.dto;

import lombok.Data;
import net.minidev.json.JSONObject;
import ua.kpi.iasa.sc.newsservice.repository.model.News;

import java.sql.Timestamp;

@Data
public class NewsBackDTO {
    private long id;
    private String title;
    private String imageLink;
    private String text;
    private String link;
    private JSONObject author;
    private Timestamp createdAt;

    public NewsBackDTO(News news){
        this.id = news.getId();
        this.imageLink = news.getImageLink();
        this.text = news.getText();
        this.link = news.getLink();
        this.title = news.getTitle();
        this.createdAt = news.getCreatedAt();
        this.author = null;
    }

    public NewsBackDTO(News news, JSONObject author){
        this.id = news.getId();
        this.imageLink = news.getImageLink();
        this.text = news.getText();
        this.link = news.getLink();
        this.title = news.getTitle();
        this.createdAt = news.getCreatedAt();
        this.author = author;
    }
}
