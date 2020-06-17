package com.demo.im.handler;

import com.demo.im.protocol.proto.MessageDef;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

@ChannelHandler.Sharable
@Slf4j
public class WSServerHandler extends SimpleChannelInboundHandler<MessageDef.Message> {

    /**
     * 取消绑定
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        // 可能出现业务判断离线后再次触发 channelInactive
        log.warn("触发 channelInactive 掉线![{}]", ctx.channel().id());
        userOffLine(ctx);
    }

    /**
     * 心跳检查
     *
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            // 读空闲
            if (idleStateEvent.state() == IdleState.READER_IDLE) {
                // 关闭用户的连接
                userOffLine(ctx);
            }
        }
        super.userEventTriggered(ctx, evt);
    }

    /**
     * 用户下线
     */
    private void userOffLine(ChannelHandlerContext ctx) throws IOException {
        ChannelHolder.removeChannel(ctx.channel().id().toString());
        ctx.channel().close();
    }

    /**
     * 读到客户端的内容 （这里只做心跳检查）
     *
     * @param ctx
     * @param message
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MessageDef.Message message) throws Exception {
        if (message.getCmd().equals(MessageDef.Message.CommandType.HEARTBEAT_REQUEST)) {
            log.info("收到客户端发来的心跳消息：{}", message.toString());
            MessageDef.MessageOrBuilder builder = MessageDef.Message.newBuilder();
            ((MessageDef.Message.Builder) builder).setCmd(MessageDef.Message.CommandType.HEARTBEAT_RESPONSE)
                    .setFromId(ctx.channel().id().toString());
            //回应pong
            ctx.writeAndFlush(((MessageDef.Message.Builder) builder).build());
        } else if (message.getCmd().equals(MessageDef.Message.CommandType.NORMAL)) {
            log.info("收到客户端的业务消息：{}", message.toString());
            pushMsg(message);
        }
        ChannelHolder.addChannel(ctx.channel().id().toString(), ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if ("Connection reset by peer".equals(cause.getMessage())) {
            log.error("连接出现问题");
            return;
        }
        log.error(cause.getMessage(), cause);
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
