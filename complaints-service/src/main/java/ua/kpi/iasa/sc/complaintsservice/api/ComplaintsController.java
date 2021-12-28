package ua.kpi.iasa.sc.complaintsservice.api;

import com.auth0.jwt.interfaces.DecodedJWT;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ua.kpi.iasa.sc.complaintsservice.api.dto.ComplaintBackDTO;
import ua.kpi.iasa.sc.complaintsservice.api.dto.ComplaintDTO;
import ua.kpi.iasa.sc.complaintsservice.repository.model.Complaint;
import ua.kpi.iasa.sc.complaintsservice.security.utility.TokenUtility;
import ua.kpi.iasa.sc.complaintsservice.service.ComplaintsService;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/complaints")
public class ComplaintsController {
    private ComplaintsService complaintService;
    private HttpClient httpclient;

    public ComplaintsController(ComplaintsService complaintService) {
        this.complaintService = complaintService;
        httpclient = HttpClients.createDefault();
    }

    @GetMapping
    public ResponseEntity<List<ComplaintBackDTO>> index(@RequestParam(required = false) Boolean processed,
                                                        @RequestParam(required = false) Long by,
                                                        @RequestHeader String authorization){
        try{
            List<Complaint> complaints;
            Map<Integer, JSONObject> users = new HashMap<>();
            if(processed == null)
                complaints = complaintService.fetchAll();
            else if (processed) {
                if(by == null)
                    complaints = complaintService.fetchProcessed();
                else{
                    complaints = complaintService.fetchProcessedBy(by);
                }
            }
            else
                complaints = complaintService.fetchUnprocessed();
            if(processed == null || processed){
                List<Long> idsOfProcessors = new ArrayList<>();
                for(Complaint complaint: complaints)
                    if(complaint.getProcessedById() != null)
                        idsOfProcessors.add(complaint.getProcessedById());

                HttpPost httppost = new HttpPost(System.getenv("AUTH_URL") + "/identity/byids");
                httppost.setHeader("User-Agent", "Mozilla/9.0");
                httppost.setHeader("Authorization", authorization);

                StringEntity requestEntity = new StringEntity(
                        idsOfProcessors.toString(),
                        ContentType.APPLICATION_JSON);

                httppost.setEntity(requestEntity);

                HttpResponse httpResponse = httpclient.execute(httppost);
                HttpEntity entity = httpResponse.getEntity();
                if (entity != null) {
                    InputStream instream = entity.getContent();
                    BufferedReader in = new BufferedReader(new InputStreamReader(instream));
                    String inputLine;
                    StringBuffer response = new StringBuffer();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    JSONParser parser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
                    JSONArray res = (JSONArray) parser.parse(response.toString());
                    for(int i = 0; i < res.size(); i++){
                        JSONObject user = (JSONObject) res.get(i);
                        users.put((Integer) user.get("id"), user);
                    }
                }
            }
            return ResponseEntity.ok(complaints.stream().map(complaint -> {
                if(complaint.getProcessedById() == null || !users.containsKey(complaint.getProcessedById().intValue()))
                    return new ComplaintBackDTO(complaint);
                else{
                    return new ComplaintBackDTO(complaint, users.get(complaint.getProcessedById().intValue()));
                }
            }).collect(Collectors.toList()));
        }
        catch (Exception e){
            return ResponseEntity.status(400).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ComplaintBackDTO> get(@PathVariable long id, @RequestHeader String authorization){
        try{
            Complaint complaint = complaintService.fetchById(id);
            ComplaintBackDTO complaintRes = new ComplaintBackDTO(complaint);
            if (complaint.getProcessedById() != null){
                Long processedById = complaint.getProcessedById();

                HttpGet httpGet = new HttpGet(System.getenv("AUTH_URL") +  "/identity/" + processedById.toString());
                httpGet.setHeader("User-Agent", "Mozilla/9.0");
                httpGet.setHeader("Authorization", authorization);

                HttpResponse httpResponse = httpclient.execute(httpGet);
                HttpEntity entity = httpResponse.getEntity();
                if (entity != null) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(entity.getContent()));
                    String inputLine;
                    StringBuffer response = new StringBuffer();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    //System.out.println(response.toString());
                    JSONParser parser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
                    JSONObject json = (JSONObject) parser.parse(response.toString());
                    complaintRes.setProcessedBy(json);
                }
            }
            return ResponseEntity.ok(complaintRes);
        } catch (Exception e){
            return ResponseEntity.status(400).build();
        }
    }

    @PostMapping
    public ResponseEntity<Void> publish(@RequestBody ComplaintDTO complaintDTO){
        try{
            long id = complaintService.create(complaintDTO);
            final String location = String.format("/complaints/%d", id);
            return ResponseEntity.created(URI.create(location)).build();
        } catch (Exception e){
            return ResponseEntity.status(400).build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> process(@PathVariable long id, @RequestHeader String authorization){
        try{
            DecodedJWT decodedJWT = TokenUtility.verifyToken(authorization);
            long processedById = decodedJWT.getClaim("id").asLong();
            complaintService.process(id, processedById);
            return ResponseEntity.ok().build();
        } catch (Exception e){
            return ResponseEntity.status(400).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id){
        try{
            complaintService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e){
            return ResponseEntity.status(400).build();
        }
    }
}