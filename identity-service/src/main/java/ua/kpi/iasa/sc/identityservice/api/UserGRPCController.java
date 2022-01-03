package ua.kpi.iasa.sc.identityservice.api;

import io.grpc.stub.StreamObserver;
import ua.kpi.iasa.sc.grpc.UserGRPCRequest;
import ua.kpi.iasa.sc.grpc.UserGRPCResponse;
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
    public void getByIds(UserGRPCRequest request, StreamObserver<UserGRPCResponse> responseObserver) {
        List<Long> ids = request.getIdsList();
        List<User> validUsers = userService.fetchByIdIn(ids);
        UserGRPCResponse.Builder usersBuilder = UserGRPCResponse.newBuilder();
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

        UserGRPCResponse response = usersBuilder.build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
