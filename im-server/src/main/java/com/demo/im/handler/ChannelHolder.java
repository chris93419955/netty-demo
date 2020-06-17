package com.demo.im.handler;

import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wbs
 * @date 2020/6/11
 */
public class ChannelHolder {

    private static final Map<String, Channel> userChannelMap = new ConcurrentHashMap<>(10);

    public static void addChannel(String id, Channel channel) {
        if (null == userChannelMap.get(id)) {
            userChannelMap.put(id, channel);
        }
    }

    public static Channel getChannel(String id) {
        return userChannelMap.get(id);
    }

    public static void removeChannel(String id){
        userChannelMap.remove(id);
    }


}
