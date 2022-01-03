package ua.kpi.iasa.sc.grpc;

import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.4.0)",
    comments = "Source: userShort.proto")
public final class UserGRPCServiceGrpc {

  private UserGRPCServiceGrpc() {}

  public static final String SERVICE_NAME = "ua.kpi.iasa.sc.grpc.UserGRPCService";

  // Static method descriptors that strictly reflect the proto.
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<ua.kpi.iasa.sc.grpc.UserGRPCRequest,
      ua.kpi.iasa.sc.grpc.UserGRPCResponse> METHOD_GET_BY_IDS =
      io.grpc.MethodDescriptor.<ua.kpi.iasa.sc.grpc.UserGRPCRequest, ua.kpi.iasa.sc.grpc.UserGRPCResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "ua.kpi.iasa.sc.grpc.UserGRPCService", "getByIds"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              ua.kpi.iasa.sc.grpc.UserGRPCRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              ua.kpi.iasa.sc.grpc.UserGRPCResponse.getDefaultInstance()))
          .build();

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static UserGRPCServiceStub newStub(io.grpc.Channel channel) {
    return new UserGRPCServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static UserGRPCServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new UserGRPCServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static UserGRPCServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new UserGRPCServiceFutureStub(channel);
  }

  /**
   */
  public static abstract class UserGRPCServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public void getByIds(ua.kpi.iasa.sc.grpc.UserGRPCRequest request,
        io.grpc.stub.StreamObserver<ua.kpi.iasa.sc.grpc.UserGRPCResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_GET_BY_IDS, responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            METHOD_GET_BY_IDS,
            asyncUnaryCall(
              new MethodHandlers<
                ua.kpi.iasa.sc.grpc.UserGRPCRequest,
                ua.kpi.iasa.sc.grpc.UserGRPCResponse>(
                  this, METHODID_GET_BY_IDS)))
          .build();
    }
  }

  /**
   */
  public static final class UserGRPCServiceStub extends io.grpc.stub.AbstractStub<UserGRPCServiceStub> {
    private UserGRPCServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private UserGRPCServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected UserGRPCServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new UserGRPCServiceStub(channel, callOptions);
    }

    /**
     */
    public void getByIds(ua.kpi.iasa.sc.grpc.UserGRPCRequest request,
        io.grpc.stub.StreamObserver<ua.kpi.iasa.sc.grpc.UserGRPCResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_GET_BY_IDS, getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class UserGRPCServiceBlockingStub extends io.grpc.stub.AbstractStub<UserGRPCServiceBlockingStub> {
    private UserGRPCServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private UserGRPCServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected UserGRPCServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new UserGRPCServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public ua.kpi.iasa.sc.grpc.UserGRPCResponse getByIds(ua.kpi.iasa.sc.grpc.UserGRPCRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_GET_BY_IDS, getCallOptions(), request);
    }
  }

  /**
   */
  public static final class UserGRPCServiceFutureStub extends io.grpc.stub.AbstractStub<UserGRPCServiceFutureStub> {
    private UserGRPCServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private UserGRPCServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected UserGRPCServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new UserGRPCServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<ua.kpi.iasa.sc.grpc.UserGRPCResponse> getByIds(
        ua.kpi.iasa.sc.grpc.UserGRPCRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_GET_BY_IDS, getCallOptions()), request);
    }
  }

  private static final int METHODID_GET_BY_IDS = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final UserGRPCServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(UserGRPCServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_GET_BY_IDS:
          serviceImpl.getByIds((ua.kpi.iasa.sc.grpc.UserGRPCRequest) request,
              (io.grpc.stub.StreamObserver<ua.kpi.iasa.sc.grpc.UserGRPCResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  private static final class UserGRPCServiceDescriptorSupplier implements io.grpc.protobuf.ProtoFileDescriptorSupplier {
    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return ua.kpi.iasa.sc.grpc.UserShort.getDescriptor();
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (UserGRPCServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new UserGRPCServiceDescriptorSupplier())
              .addMethod(METHOD_GET_BY_IDS)
              .build();
        }
      }
    }
    return result;
  }
}
