package ua.kpi.iasa.sc.complaintsservice.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ua.kpi.iasa.sc.complaintsservice.api.dto.ComplaintDTO;
import ua.kpi.iasa.sc.complaintsservice.repository.ComplaintsRepo;
import ua.kpi.iasa.sc.complaintsservice.repository.model.Complaint;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ComplaintsService {
    private ComplaintsRepo complaintsRepo;

    public List<Complaint> fetchAll() {
        return complaintsRepo.findAll();
    }
    public List<Complaint> fetchUnprocessed() { return complaintsRepo.findByProcessedByIdNull(); }
    public List<Complaint> fetchProcessed() { return complaintsRepo.findByProcessedByIdNotNull(); }
    public List<Complaint> fetchProcessedBy(long processedById) { return complaintsRepo.findByProcessedById(processedById); }

    public Complaint fetchById(long id) {
        final Optional<Complaint> foundComplaint = complaintsRepo.findById(id);
        if (foundComplaint.isEmpty())
            throw new IllegalArgumentException("News not found");
        return foundComplaint.get();
    }

    public long create(ComplaintDTO complaintDTO){
        Complaint complaint = complaintsRepo.save(new Complaint(complaintDTO));
        return complaint.getId();
    }

    public void update(long id, ComplaintDTO complaintDTO){
        Complaint complaint = fetchById(id);
        if (complaintDTO.getText() != null && !complaintDTO.getText().isBlank()) complaint.setText(complaintDTO.getText());
        if (complaintDTO.getPatronymic() != null && !complaintDTO.getPatronymic().isBlank()) complaint.setPatronymic(complaintDTO.getPatronymic());
        if (complaintDTO.getSurname() != null && !complaintDTO.getSurname().isBlank()) complaint.setSurname(complaintDTO.getSurname());
        if (complaintDTO.getFirstname() != null && !complaintDTO.getFirstname().isBlank()) complaint.setFirstname(complaintDTO.getFirstname());
        complaintsRepo.save(complaint);
    }

    public void process (long id, long processedById){
        Complaint complaint = fetchById(id);
        complaint.setProcessedById(processedById);
        complaintsRepo.save(complaint);
    }

    public void delete(long id) {
        complaintsRepo.deleteById(id);
    }
}