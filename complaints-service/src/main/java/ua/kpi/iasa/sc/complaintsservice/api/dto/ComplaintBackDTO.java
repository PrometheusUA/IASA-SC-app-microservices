package ua.kpi.iasa.sc.complaintsservice.api.dto;

import lombok.Data;
import net.minidev.json.JSONObject;
import ua.kpi.iasa.sc.complaintsservice.repository.model.Complaint;

@Data
public class ComplaintBackDTO {
    private long id;
    private String firstname;
    private String surname;
    private String patronymic;
    private String text;
    private JSONObject processedBy;

    public ComplaintBackDTO(Complaint complaint){
        this.id = complaint.getId();
        this.firstname = complaint.getFirstname();
        this.surname = complaint.getSurname();
        this.patronymic = complaint.getPatronymic();
        this.text = complaint.getText();
        this.processedBy = null;
    }

    public ComplaintBackDTO(Complaint complaint, JSONObject processedBy){
        this.id = complaint.getId();
        this.firstname = complaint.getFirstname();
        this.surname = complaint.getSurname();
        this.patronymic = complaint.getPatronymic();
        this.text = complaint.getText();
        this.processedBy = processedBy;
    }
}
