package com.demo.im.controller;

import com.demo.im.client.NettyClient;
import com.demo.im.protocol.proto.MessageDef;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author wbs
 * @date 2020/6/11
 */
@Controller
public class MessagerController {

    @Autowired
    private NettyClient nettyClient;

    @GetMapping("/send")
    @ResponseBody
    public String send(String msg, String from, String to) {
        MessageDef.MessageOrBuilder builder = MessageDef.Message.newBuilder();
        MessageDef.Message message = ((MessageDef.Message.Builder) builder)
                .setCmd(MessageDef.Message.CommandType.NORMAL)
                .setContent(msg)
                .setFromId(from)
                .setToId(to)
                .build();
        nettyClient.sendMsg(message);
        return "send ok";
    }
}
