package ua.kpi.iasa.sc.identityservice.api;

import io.grpc.stub.StreamObserver;
import ua.kpi.iasa.sc.grpc.UserGRPCRequest;
import ua.kpi.iasa.sc.grpc.UserGRPCRequestMulti;
import ua.kpi.iasa.sc.grpc.UserGRPCResponseMulti;
import ua.kpi.iasa.sc.grpc.UserGRPCServiceGrpc.UserGRPCServiceImplBase;
import ua.kpi.iasa.sc.grpc.UserShortBackDTO;
import ua.kpi.iasa.sc.identityservice.repository.model.User;
import ua.kpi.iasa.sc.identityservice.service.UserService;

import java.util.List;
import java.util.stream.IntStream;

public class UserGRPCController extends UserGRPCServiceImplBase {
    private UserService userService;

    public UserGRPCController(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void getByIds(UserGRPCRequestMulti request, StreamObserver<UserGRPCResponseMulti> responseObserver) {
        List<Long> ids = request.getIdsList();
        List<User> validUsers = userService.fetchByIdIn(ids);
        UserGRPCResponseMulti.Builder usersBuilder = UserGRPCResponseMulti.newBuilder();
        IntStream.range(0, validUsers.size()).forEach(id ->
        {
            User user = validUsers.get(id);
            String fullname = user.getSurname() + " " + user.getName() + (user.getPatronymic()==null?"":(" " + user.getPatronymic()));
            UserShortBackDTO u = UserShortBackDTO.newBuilder()
                    .setId(user.getId())
                    .setFullname(fullname)
                    .build();
            usersBuilder.addUser(u);
        }
        );

        UserGRPCResponseMulti response = usersBuilder.build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getById(UserGRPCRequest request, StreamObserver<UserShortBackDTO> responseObserver) {
        try {
            Long id = request.getId();
            User foundUser =  userService.fetchById(id);

            String fullname = foundUser.getSurname() + " " + foundUser.getName() + (foundUser.getPatronymic() == null ? "" : (" " + foundUser.getPatronymic()));

            UserShortBackDTO response = UserShortBackDTO.newBuilder()
                    .setId(foundUser.getId())
                    .setFullname(fullname)
                    .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
        catch (Exception e){
            responseObserver.onError(e);
            responseObserver.onCompleted();
        }
    }
}
