package com.demo.im.server;

import com.demo.im.initializer.TcpServerInitializer;
import com.demo.im.initializer.WSServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wbs
 * @date 2020/6/10
 */
@Component
@Slf4j
public class ImServer implements CommandLineRunner {

    @Value("${netty.server.socket.port.from}")
    Integer tcpPortFrom;

    @Value("${netty.server.socket.port.to}")
    Integer tcpPortTo;

    @Value("${netty.server.ws.port.from}")
    Integer wsPortFrom;

    @Value("${netty.server.ws.port.to}")
    Integer wsPortTo;

    public void start() throws InterruptedException {
        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        try {
            ServerBootstrap boot = new ServerBootstrap();
            boot.group(boss, worker);
            boot.channel(NioServerSocketChannel.class);
            boot.option(ChannelOption.SO_BACKLOG, 128);
            boot.childOption(ChannelOption.SO_KEEPALIVE, true);
            // 开始监听
            List<ChannelFuture> channelFutureList = new ArrayList<>();
            for (int i = tcpPortFrom; i <= tcpPortTo; i++) {
                log.info("IM服务-开始监听：tcp端口：" + i);
                boot.childHandler(new TcpServerInitializer());
                ChannelFuture future = boot.bind(i).sync();
                channelFutureList.add(future);
            }
            for (int i = wsPortFrom; i <= wsPortTo; i++) {
                log.info("IM服务-开始监听：ws端口：" + i);
                boot.childHandler(new WSServerInitializer());
                ChannelFuture future = boot.bind(i).sync();
                channelFutureList.add(future);
            }

            // close
            for (ChannelFuture future : channelFutureList) {
                future.channel().closeFuture().sync();
            }
        } catch (Exception e) {
            log.error("启动失败：{}", e.getMessage());
        } finally {
            boss.shutdownGracefully().sync();
            worker.shutdownGracefully().sync();
            log.info("关闭Netty");
        }

    }

    @Override
    public void run(String... args) throws Exception {
        start();
    }
}
