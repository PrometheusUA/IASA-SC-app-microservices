package ua.kpi.iasa.sc.complaintsservice.api;

import com.auth0.jwt.interfaces.DecodedJWT;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
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
import ua.kpi.iasa.sc.grpc.*;

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
            Map<Long, JSONObject> users = new HashMap<>();
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

                ManagedChannel channel = ManagedChannelBuilder.forAddress(System.getenv("AUTH_HOST"), Integer.parseInt(System.getenv("AUTH_GRPC_PORT")))
                        .usePlaintext()
                        .build();

                UserGRPCServiceGrpc.UserGRPCServiceBlockingStub stub
                        = UserGRPCServiceGrpc.newBlockingStub(channel);

                UserGRPCRequestMulti.Builder builder = UserGRPCRequestMulti.newBuilder().addAllIds(idsOfProcessors);

                UserGRPCResponseMulti usersResponse = stub.getByIds(builder.build());
                List<UserShortBackDTO> authorsList = usersResponse.getUserList();
                for(int i = 0; i < authorsList.size(); i++){
                    UserShortBackDTO user = authorsList.get(i);
                    Map<String, Object> props = new HashMap<>();
                    props.put("id", user.getId());
                    props.put("fullname", user.getFullname());
                    users.put(user.getId(), new JSONObject(props));
                }
                channel.shutdown();
            }
            return ResponseEntity.ok(complaints.stream().map(complaint -> {
                if(complaint.getProcessedById() == null || !users.containsKey(complaint.getProcessedById().longValue()))
                    return new ComplaintBackDTO(complaint);
                else{
                    return new ComplaintBackDTO(complaint, users.get(complaint.getProcessedById().longValue()));
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

                ManagedChannel channel = ManagedChannelBuilder.forAddress(System.getenv("AUTH_HOST"), Integer.parseInt(System.getenv("AUTH_GRPC_PORT")))
                        .usePlaintext()
                        .build();

                UserGRPCServiceGrpc.UserGRPCServiceBlockingStub stub
                        = UserGRPCServiceGrpc.newBlockingStub(channel);

                UserGRPCRequest.Builder builder = UserGRPCRequest.newBuilder().setId(processedById);

                UserShortBackDTO userResponse = stub.getById(builder.build());
                channel.shutdown();

                Map<String, Object> props = new HashMap<>();
                props.put("id", userResponse.getId());
                props.put("fullname", userResponse.getFullname());
                JSONObject user = new JSONObject(props);
                complaintRes.setProcessedBy(user);
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