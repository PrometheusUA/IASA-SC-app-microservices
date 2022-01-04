package ua.kpi.iasa.sc.newsservice.api;

import com.auth0.jwt.interfaces.DecodedJWT;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ua.kpi.iasa.sc.grpc.*;
import ua.kpi.iasa.sc.newsservice.api.dto.NewsBackDTO;
import ua.kpi.iasa.sc.newsservice.api.dto.NewsDTO;
import ua.kpi.iasa.sc.newsservice.repository.model.News;
import ua.kpi.iasa.sc.newsservice.security.utility.TokenUtility;
import ua.kpi.iasa.sc.newsservice.service.NewsService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/news")
public class NewsController {
    private NewsService newsService;
    private HttpClient httpclient;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
        httpclient = HttpClients.createDefault();
    }

    @GetMapping
    public ResponseEntity<?> index(@RequestParam(required = false) Integer pagenum, @RequestParam(required = false) Integer pagelen) {
        try{
            List<Long> idsOfAuthors = new ArrayList<>();
            List<News> newsResList = new ArrayList<>();
            if (pagelen == null && pagenum == null) {
                newsResList = newsService.fetchAll().stream().map(news -> {
                    idsOfAuthors.add(news.getCreatedBy());
                    return news;
                }).collect(Collectors.toList());
            }
            else{
                int _pagelen = pagelen == null ? 12 : pagelen;
                int _pagenum = pagenum == null ? 0 : pagenum;

                newsResList = newsService.fetchPage(_pagenum, _pagelen).stream().map(news -> {
                    idsOfAuthors.add(news.getCreatedBy());
                    return news;
                }).collect(Collectors.toList());
            }

            ManagedChannel channel = ManagedChannelBuilder.forAddress(System.getenv("AUTH_HOST"), Integer.parseInt(System.getenv("AUTH_GRPC_PORT")))
                    .usePlaintext()
                    .build();

            UserGRPCServiceGrpc.UserGRPCServiceBlockingStub stub
                    = UserGRPCServiceGrpc.newBlockingStub(channel);

            UserGRPCRequestMulti.Builder builder = UserGRPCRequestMulti.newBuilder().addAllIds(idsOfAuthors);

            UserGRPCResponseMulti usersResponse = stub.getByIds(builder.build());
            Map<Long, JSONObject> authors = new HashMap<>();
            List<UserShortBackDTO> authorsList = usersResponse.getUserList();
            for(int i = 0; i < authorsList.size(); i++){
                UserShortBackDTO user = authorsList.get(i);
                Map<String, Object> props = new HashMap<>();
                props.put("id", user.getId());
                props.put("fullname", user.getFullname());
                authors.put(user.getId(), new JSONObject(props));
            }
            channel.shutdown();

            return ResponseEntity.ok(newsResList.stream().map(news -> {
                if(news.getCreatedBy() == null || !authors.containsKey(news.getCreatedBy().longValue()))
                    return new NewsBackDTO(news);
                else{
                    return new NewsBackDTO(news, authors.get(news.getCreatedBy().longValue()));
                }
            }).collect(Collectors.toList()));
        }
        catch(Exception e){
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> indexById(@PathVariable long id) {
        try {
            final News foundNews = newsService.fetchById(id);
            if(foundNews.getCreatedBy() != null){
                ManagedChannel channel = ManagedChannelBuilder.forAddress(System.getenv("AUTH_HOST"), Integer.parseInt(System.getenv("AUTH_GRPC_PORT")))
                        .usePlaintext()
                        .build();

                UserGRPCServiceGrpc.UserGRPCServiceBlockingStub stub
                        = UserGRPCServiceGrpc.newBlockingStub(channel);

                UserGRPCRequest.Builder builder = UserGRPCRequest.newBuilder().setId(foundNews.getCreatedBy());

                UserShortBackDTO userResponse = stub.getById(builder.build());
                channel.shutdown();

                Map<String, Object> props = new HashMap<>();
                props.put("id", userResponse.getId());
                props.put("fullname", userResponse.getFullname());
                JSONObject author = new JSONObject(props);
                return ResponseEntity.ok(new NewsBackDTO(foundNews, author));
            }
            return ResponseEntity.ok(new NewsBackDTO(foundNews));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
        catch (Exception e){
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @GetMapping("/count")
    public ResponseEntity<Long> count() {
        try {
            final long newsCount = newsService.count();
            return ResponseEntity.ok(newsCount);
        } catch (Exception e) {
            return ResponseEntity.status(400).build();
        }
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody NewsDTO newsDTO, @RequestHeader String authorization) {
        try {
            DecodedJWT decodedJWT = TokenUtility.verifyToken(authorization);
            Long createdById = decodedJWT.getClaim("id").asLong();
            if(createdById == null)
                throw new IllegalArgumentException("Token is in invalid format!");
            long id = newsService.create(newsDTO, createdById);
            final String location = String.format("/news/%d", id);
            return ResponseEntity.created(URI.create(location)).build();
        } catch (Exception e) {
            return ResponseEntity.status(403).body(e.getMessage());
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable long id, @RequestBody NewsDTO newsDTO, @RequestHeader String authorization) {
        try {
            DecodedJWT decodedJWT = TokenUtility.verifyToken(authorization);
            List<String> roles = decodedJWT.getClaim("roles").asList(String.class);
            Long editorId = decodedJWT.getClaim("id").asLong();
            if(roles == null || editorId == null)
                throw new IllegalArgumentException("Token is in invalid format!");
            News oldNews = newsService.fetchById(id);
            if (roles.contains("Admin") || editorId == oldNews.getCreatedBy()) {
                newsService.update(id, newsDTO);
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.status(403).body("You have no rights to edit this news");
            }
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @PostMapping("/{id}/delete")
    public ResponseEntity<?> userdelete(@PathVariable long id, @RequestHeader String authorization) {
        try {
            DecodedJWT decodedJWT = TokenUtility.verifyToken(authorization);
            News oldNews = newsService.fetchById(id);
            List<String> roles = decodedJWT.getClaim("roles").asList(String.class);
            Long editorId = decodedJWT.getClaim("id").asLong();
            if(roles == null || editorId == null)
                throw new IllegalArgumentException("Token is in invalid format!");
            if (roles.contains("Admin") || editorId == oldNews.getCreatedBy()) {
                newsService.deleteByUser(id);
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.status(403).body("You have no rights to delete this news");
            }
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable long id, @RequestHeader String authorization) {
        try {
            DecodedJWT decodedJWT = TokenUtility.verifyToken(authorization);
            Long editorId = decodedJWT.getClaim("id").asLong();
            if(editorId == null)
                throw new IllegalArgumentException("Token is in invalid format!");
            News oldNews = newsService.fetchById(id);
            if (editorId == oldNews.getCreatedBy()) {
                newsService.delete(id);
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.status(403).body("You have no rights to delete this news. You should be author of the news to delete it completely");
            }
        } catch (Exception e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }
}