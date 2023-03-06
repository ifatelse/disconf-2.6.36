package com.baidu.disconf.core.grpc.auto;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.14.0)",
    comments = "Source: disconf_grpc_service.proto")
public final class BiRequestStreamGrpc {

  private BiRequestStreamGrpc() {}

  public static final String SERVICE_NAME = "BiRequestStream";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<com.baidu.disconf.core.grpc.auto.Message,
      com.baidu.disconf.core.grpc.auto.Message> getBiRequestStreamMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "biRequestStream",
      requestType = com.baidu.disconf.core.grpc.auto.Message.class,
      responseType = com.baidu.disconf.core.grpc.auto.Message.class,
      methodType = io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
  public static io.grpc.MethodDescriptor<com.baidu.disconf.core.grpc.auto.Message,
      com.baidu.disconf.core.grpc.auto.Message> getBiRequestStreamMethod() {
    io.grpc.MethodDescriptor<com.baidu.disconf.core.grpc.auto.Message, com.baidu.disconf.core.grpc.auto.Message> getBiRequestStreamMethod;
    if ((getBiRequestStreamMethod = BiRequestStreamGrpc.getBiRequestStreamMethod) == null) {
      synchronized (BiRequestStreamGrpc.class) {
        if ((getBiRequestStreamMethod = BiRequestStreamGrpc.getBiRequestStreamMethod) == null) {
          BiRequestStreamGrpc.getBiRequestStreamMethod = getBiRequestStreamMethod = 
              io.grpc.MethodDescriptor.<com.baidu.disconf.core.grpc.auto.Message, com.baidu.disconf.core.grpc.auto.Message>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
              .setFullMethodName(generateFullMethodName(
                  "BiRequestStream", "biRequestStream"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.baidu.disconf.core.grpc.auto.Message.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  com.baidu.disconf.core.grpc.auto.Message.getDefaultInstance()))
                  .setSchemaDescriptor(new BiRequestStreamMethodDescriptorSupplier("biRequestStream"))
                  .build();
          }
        }
     }
     return getBiRequestStreamMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static BiRequestStreamStub newStub(io.grpc.Channel channel) {
    return new BiRequestStreamStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static BiRequestStreamBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new BiRequestStreamBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static BiRequestStreamFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new BiRequestStreamFutureStub(channel);
  }

  /**
   */
  public static abstract class BiRequestStreamImplBase implements io.grpc.BindableService {

    /**
     */
    public io.grpc.stub.StreamObserver<com.baidu.disconf.core.grpc.auto.Message> biRequestStream(
        io.grpc.stub.StreamObserver<com.baidu.disconf.core.grpc.auto.Message> responseObserver) {
      return asyncUnimplementedStreamingCall(getBiRequestStreamMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getBiRequestStreamMethod(),
            asyncBidiStreamingCall(
              new MethodHandlers<
                com.baidu.disconf.core.grpc.auto.Message,
                com.baidu.disconf.core.grpc.auto.Message>(
                  this, METHODID_BI_REQUEST_STREAM)))
          .build();
    }
  }

  /**
   */
  public static final class BiRequestStreamStub extends io.grpc.stub.AbstractStub<BiRequestStreamStub> {
    private BiRequestStreamStub(io.grpc.Channel channel) {
      super(channel);
    }

    private BiRequestStreamStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected BiRequestStreamStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new BiRequestStreamStub(channel, callOptions);
    }

    /**
     */
    public io.grpc.stub.StreamObserver<com.baidu.disconf.core.grpc.auto.Message> biRequestStream(
        io.grpc.stub.StreamObserver<com.baidu.disconf.core.grpc.auto.Message> responseObserver) {
      return asyncBidiStreamingCall(
          getChannel().newCall(getBiRequestStreamMethod(), getCallOptions()), responseObserver);
    }
  }

  /**
   */
  public static final class BiRequestStreamBlockingStub extends io.grpc.stub.AbstractStub<BiRequestStreamBlockingStub> {
    private BiRequestStreamBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private BiRequestStreamBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected BiRequestStreamBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new BiRequestStreamBlockingStub(channel, callOptions);
    }
  }

  /**
   */
  public static final class BiRequestStreamFutureStub extends io.grpc.stub.AbstractStub<BiRequestStreamFutureStub> {
    private BiRequestStreamFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private BiRequestStreamFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected BiRequestStreamFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new BiRequestStreamFutureStub(channel, callOptions);
    }
  }

  private static final int METHODID_BI_REQUEST_STREAM = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final BiRequestStreamImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(BiRequestStreamImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_BI_REQUEST_STREAM:
          return (io.grpc.stub.StreamObserver<Req>) serviceImpl.biRequestStream(
              (io.grpc.stub.StreamObserver<com.baidu.disconf.core.grpc.auto.Message>) responseObserver);
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class BiRequestStreamBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    BiRequestStreamBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.baidu.disconf.core.grpc.auto.DisconfGrpcService.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("BiRequestStream");
    }
  }

  private static final class BiRequestStreamFileDescriptorSupplier
      extends BiRequestStreamBaseDescriptorSupplier {
    BiRequestStreamFileDescriptorSupplier() {}
  }

  private static final class BiRequestStreamMethodDescriptorSupplier
      extends BiRequestStreamBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    BiRequestStreamMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (BiRequestStreamGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new BiRequestStreamFileDescriptorSupplier())
              .addMethod(getBiRequestStreamMethod())
              .build();
        }
      }
    }
    return result;
  }
}
