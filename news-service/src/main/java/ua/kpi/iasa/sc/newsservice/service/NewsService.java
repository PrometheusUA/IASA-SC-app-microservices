package ua.kpi.iasa.sc.newsservice.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ua.kpi.iasa.sc.newsservice.repository.NewsRepo;
import ua.kpi.iasa.sc.newsservice.api.dto.NewsDTO;
import ua.kpi.iasa.sc.newsservice.repository.NewsRepo;
import ua.kpi.iasa.sc.newsservice.repository.model.News;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class NewsService {
    private NewsRepo newsRepo;

    public List<News> fetchAll() {
        return newsRepo.findByDeleted(false);
    }

    public List<News> fetchPage(int pageNum, int len) {
        return newsRepo.findByDeleted(false, PageRequest.of(pageNum, len, Sort.by(Sort.Direction.DESC, "createdAt")))
                .stream().toList();
    }

    public News fetchById(long id) {
        final Optional<News> foundNews = newsRepo.findById(id);
        if (foundNews.isEmpty())
            throw new IllegalArgumentException("News not found");
        return foundNews.get();
    }

    public long create(NewsDTO newsDTO, long accId){
         News news = newsRepo.save(new News(newsDTO, accId));
         return news.getId();
    }

    public long count(){
        long count = newsRepo.count();
        return count;
    }

    public void update(long id, NewsDTO newsDTO){
        News news = fetchById(id);
        if (newsDTO.getLink() != null && !newsDTO.getLink().isBlank()) news.setLink(newsDTO.getLink());
        if (newsDTO.getImageLink() != null && !newsDTO.getImageLink().isBlank()) news.setImageLink(newsDTO.getImageLink());
        if (newsDTO.getText() != null && !newsDTO.getText().isBlank()) news.setText(newsDTO.getText());
        if (newsDTO.getTitle() != null && !newsDTO.getTitle().isBlank()) news.setTitle(newsDTO.getTitle());
        newsRepo.save(news);
    }

    public void delete(long id) {
        newsRepo.deleteById(id);
    }

    public void deleteByUser(long id) {
        News news = newsRepo.getById(id);
        news.setDeleted(true);
        newsRepo.save(news);
    }
}
