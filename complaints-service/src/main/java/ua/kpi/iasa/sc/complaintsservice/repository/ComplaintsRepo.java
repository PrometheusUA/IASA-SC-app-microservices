package ua.kpi.iasa.sc.complaintsservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.kpi.iasa.sc.complaintsservice.repository.model.Complaint;

import java.util.List;

@Repository
public interface ComplaintsRepo extends JpaRepository<Complaint, Long> {
    public List<Complaint> findByProcessedByIdNull();
    public List<Complaint> findByProcessedByIdNotNull();
    public List<Complaint> findByProcessedById(long processedById);
}