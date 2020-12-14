package io.github.kimmking.gateway.filter;


import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaders;

public class ReqHeadFilter implements HttpRequestFilter{

    @Override
    public void filter(FullHttpRequest fullRequest) {
        HttpHeaders headers = fullRequest.headers();
        headers.set("nio","wp");
    }
}
