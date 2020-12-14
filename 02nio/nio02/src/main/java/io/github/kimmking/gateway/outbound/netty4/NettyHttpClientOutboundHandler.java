package io.github.kimmking.gateway.outbound.netty4;

import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.CharsetUtil;

import java.nio.charset.Charset;

public class NettyHttpClientOutboundHandler  extends ChannelInboundHandlerAdapter {

    private Channel serverChannel;

    public NettyHttpClientOutboundHandler(Channel serverChannel){
        this.serverChannel = serverChannel;
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg){
        System.out.println("client channelRead.."+msg);
        serverChannel.writeAndFlush(msg);
    }
}