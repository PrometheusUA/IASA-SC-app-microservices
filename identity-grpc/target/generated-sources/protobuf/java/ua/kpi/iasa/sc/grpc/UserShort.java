// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: userShort.proto

package ua.kpi.iasa.sc.grpc;

public final class UserShort {
  private UserShort() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_ua_kpi_iasa_sc_grpc_UserGRPCRequestMulti_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_ua_kpi_iasa_sc_grpc_UserGRPCRequestMulti_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_ua_kpi_iasa_sc_grpc_UserGRPCRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_ua_kpi_iasa_sc_grpc_UserGRPCRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_ua_kpi_iasa_sc_grpc_UserShortBackDTO_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_ua_kpi_iasa_sc_grpc_UserShortBackDTO_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_ua_kpi_iasa_sc_grpc_UserGRPCResponseMulti_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_ua_kpi_iasa_sc_grpc_UserGRPCResponseMulti_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\017userShort.proto\022\023ua.kpi.iasa.sc.grpc\"#" +
      "\n\024UserGRPCRequestMulti\022\013\n\003ids\030\001 \003(\003\"\035\n\017U" +
      "serGRPCRequest\022\n\n\002id\030\001 \001(\003\"0\n\020UserShortB" +
      "ackDTO\022\n\n\002id\030\001 \001(\003\022\020\n\010fullname\030\002 \001(\t\"L\n\025" +
      "UserGRPCResponseMulti\0223\n\004user\030\001 \003(\0132%.ua" +
      ".kpi.iasa.sc.grpc.UserShortBackDTO2\314\001\n\017U" +
      "serGRPCService\022a\n\010getByIds\022).ua.kpi.iasa" +
      ".sc.grpc.UserGRPCRequestMulti\032*.ua.kpi.i" +
      "asa.sc.grpc.UserGRPCResponseMulti\022V\n\007get" +
      "ById\022$.ua.kpi.iasa.sc.grpc.UserGRPCReque",
      "st\032%.ua.kpi.iasa.sc.grpc.UserShortBackDT" +
      "OB\027\n\023ua.kpi.iasa.sc.grpcP\001b\006proto3"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
        new com.google.protobuf.Descriptors.FileDescriptor.    InternalDescriptorAssigner() {
          public com.google.protobuf.ExtensionRegistry assignDescriptors(
              com.google.protobuf.Descriptors.FileDescriptor root) {
            descriptor = root;
            return null;
          }
        };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
    internal_static_ua_kpi_iasa_sc_grpc_UserGRPCRequestMulti_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_ua_kpi_iasa_sc_grpc_UserGRPCRequestMulti_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_ua_kpi_iasa_sc_grpc_UserGRPCRequestMulti_descriptor,
        new java.lang.String[] { "Ids", });
    internal_static_ua_kpi_iasa_sc_grpc_UserGRPCRequest_descriptor =
      getDescriptor().getMessageTypes().get(1);
    internal_static_ua_kpi_iasa_sc_grpc_UserGRPCRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_ua_kpi_iasa_sc_grpc_UserGRPCRequest_descriptor,
        new java.lang.String[] { "Id", });
    internal_static_ua_kpi_iasa_sc_grpc_UserShortBackDTO_descriptor =
      getDescriptor().getMessageTypes().get(2);
    internal_static_ua_kpi_iasa_sc_grpc_UserShortBackDTO_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_ua_kpi_iasa_sc_grpc_UserShortBackDTO_descriptor,
        new java.lang.String[] { "Id", "Fullname", });
    internal_static_ua_kpi_iasa_sc_grpc_UserGRPCResponseMulti_descriptor =
      getDescriptor().getMessageTypes().get(3);
    internal_static_ua_kpi_iasa_sc_grpc_UserGRPCResponseMulti_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_ua_kpi_iasa_sc_grpc_UserGRPCResponseMulti_descriptor,
        new java.lang.String[] { "User", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
