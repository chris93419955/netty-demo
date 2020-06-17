package com.demo.im.handler;

import com.demo.im.protocol.proto.MessageDef;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author wbs
 * @date 2020/6/10
 */
@Slf4j
@ChannelHandler.Sharable
public class NettyServerHandler extends SimpleChannelInboundHandler<MessageDef.Message> {


    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MessageDef.Message message) throws Exception {
        if (message.getCmd().equals(MessageDef.Message.CommandType.HEARTBEAT_REQUEST)) {
            log.info("收到客户端发来的心跳消息：{}", message.toString());
            MessageDef.MessageOrBuilder builder = MessageDef.Message.newBuilder();
            ((MessageDef.Message.Builder) builder).setCmd(MessageDef.Message.CommandType.HEARTBEAT_RESPONSE)
                    .setFromId(channelHandlerContext.channel().id().toString());
            //回应pong
            channelHandlerContext.writeAndFlush(((MessageDef.Message.Builder) builder).build());
        } else if (message.getCmd().equals(MessageDef.Message.CommandType.NORMAL)) {
            log.info("收到客户端的业务消息：{}", message.toString());
            pushMsg(message);
        }
        ChannelHolder.addChannel(channelHandlerContext.channel().id().toString(), channelHandlerContext.channel());
    }


    /**
     * 向目标channel推送消息
     * @param message
     */
    private void pushMsg(MessageDef.Message message) {
        Channel channel = ChannelHolder.getChannel(message.getToId());

        if (null != channel) {
            channel.writeAndFlush(message);
        }
    }


}
