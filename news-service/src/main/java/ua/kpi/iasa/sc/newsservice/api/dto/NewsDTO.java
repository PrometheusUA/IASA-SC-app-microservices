package ua.kpi.iasa.sc.newsservice.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewsDTO {
    private String title;
    private String imageLink;
    private String text;
    private String link;
}
