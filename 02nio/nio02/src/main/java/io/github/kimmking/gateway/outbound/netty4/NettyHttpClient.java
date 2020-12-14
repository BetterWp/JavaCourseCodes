package io.github.kimmking.gateway.outbound.netty4;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.util.Map;

public class NettyHttpClient {

    Bootstrap b = new Bootstrap();
    EventLoopGroup workerGroup = new NioEventLoopGroup();

    private String host;
    private int port;
    public NettyHttpClient(String backendUrl){
        backendUrl = backendUrl.endsWith("/")?backendUrl.substring(0,backendUrl.length()-1):backendUrl;
        String[] split = backendUrl.substring(backendUrl.indexOf("://") + 3).split(":");
        host = split[0];
        port = Integer.parseInt(split[1]);
//        connect(host,port);
        b.group(workerGroup);
        b.channel(NioSocketChannel.class);
        b.option(ChannelOption.SO_KEEPALIVE, true);
    }

    public void connect(String host, int port,final FullHttpRequest fullRequest, final ChannelHandlerContext ctx) throws InterruptedException {

//        try {

            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    // 客户端接收到的是httpResponse响应，所以要使用HttpResponseDecoder进行解码
                    //客户端发送的是httprequest，所以要使用HttpRequestEncoder进行编码
                    ch.pipeline().addLast(new HttpResponseDecoder());
                    ch.pipeline().addLast(new HttpRequestEncoder());
                    ch.pipeline().addLast(new NettyHttpClientOutboundHandler(ctx.channel()));
                    ch.pipeline().addLast(new LoggingHandler(LogLevel.INFO));
                }
            });
            // Start the client.
            Channel channel = b.connect(host, port).sync().channel();
            channel.writeAndFlush(fullRequest);
//        } finally {
//            workerGroup.shutdownGracefully();
//
//        }

    }

    public void handle(final FullHttpRequest fullRequest, final ChannelHandlerContext ctx) throws Exception {
        System.out.println(fullRequest.headers().get("nio"));

        connect(host,port,fullRequest,ctx);
    }
}