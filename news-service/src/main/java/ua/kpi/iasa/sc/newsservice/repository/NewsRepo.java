package ua.kpi.iasa.sc.newsservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.kpi.iasa.sc.newsservice.repository.model.News;

import java.util.List;

@Repository
public interface NewsRepo extends JpaRepository<News, Long> {
    public List<News> findByDeleted(boolean deleted);

    public Page<News> findByDeleted(boolean deleted, Pageable pageable);
}
