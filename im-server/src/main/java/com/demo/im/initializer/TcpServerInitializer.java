package com.demo.im.initializer;

import com.demo.im.handler.NettyServerHandler;
import com.demo.im.handler.ServerIdleStateHandler;
import com.demo.im.protocol.proto.MessageDef;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

/**
 * socket协议
 *
 * @author wbs
 * @date 2020/6/12
 */
public class TcpServerInitializer extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline()
                .addLast(new ServerIdleStateHandler())
                .addLast(new ProtobufVarint32FrameDecoder())
                .addLast(new ProtobufDecoder(MessageDef.Message.getDefaultInstance()))
                .addLast(new ProtobufVarint32LengthFieldPrepender())
                .addLast(new ProtobufEncoder())
                .addLast(new NettyServerHandler());
    }
}
