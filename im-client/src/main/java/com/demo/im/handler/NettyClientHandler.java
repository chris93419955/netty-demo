package com.demo.im.handler;

import com.demo.im.protocol.proto.MessageDef;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author wbs
 * @date 2020/6/11
 */
@Slf4j
public class NettyClientHandler extends SimpleChannelInboundHandler<MessageDef.Message> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, MessageDef.Message message) throws Exception {
        log.info("客户端收到消息：{}", message.toString());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
