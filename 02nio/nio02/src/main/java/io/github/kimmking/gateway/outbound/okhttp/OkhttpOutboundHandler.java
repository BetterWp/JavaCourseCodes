package io.github.kimmking.gateway.outbound.okhttp;

import io.github.kimmking.gateway.outbound.httpclient4.NamedThreadFactory;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import okhttp3.*;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;

import static io.netty.handler.codec.http.HttpResponseStatus.NO_CONTENT;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class OkhttpOutboundHandler {

//    private ExecutorService proxyService;
    private String backendUrl;
    private OkHttpClient okHttpClient;

    public OkhttpOutboundHandler(String backendUrl){
        this.backendUrl = backendUrl.endsWith("/")?backendUrl.substring(0,backendUrl.length()-1):backendUrl;
//        int cores = Runtime.getRuntime().availableProcessors() * 2;
//        long keepAliveTime = 1000;
//        int queueSize = 2048;
//        RejectedExecutionHandler handler = new ThreadPoolExecutor.CallerRunsPolicy();//.DiscardPolicy();
//        proxyService = new ThreadPoolExecutor(cores, cores,
//                keepAliveTime, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(queueSize),
//                new NamedThreadFactory("proxyService"), handler);
        okHttpClient  = new OkHttpClient().newBuilder()
                .connectTimeout(5,TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5,TimeUnit.SECONDS)
                .build();
    }

    public void handle(final FullHttpRequest fullRequest, final ChannelHandlerContext ctx) {
        final String url = this.backendUrl + fullRequest.uri();
        fetchGet(fullRequest, ctx, url);
//        proxyService.submit(()->fetchGet(fullRequest, ctx, url));
    }

    private void fetchGet(final FullHttpRequest fullRequest, final ChannelHandlerContext ctx, final String url) {
        String resStr = "Request fail！！！";
        try {
            Request.Builder header = new Request.Builder().get().url(url);
            for (Map.Entry<String, String> head : fullRequest.headers()) {
                header.addHeader(head.getKey(),head.getValue());
            }
            Request request = header.build();
            System.out.println("nio => "+request.header("nio"));
            Response response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()){
                resStr =  Objects.requireNonNull(response.body()).string();
            }else {
                resStr = "Request error！！！";
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        handleResponse(fullRequest,ctx, resStr);
    }

    private void handleResponse(final FullHttpRequest fullRequest, final ChannelHandlerContext ctx, final String resStr){
        FullHttpResponse response = null;
        try {

            response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(resStr.getBytes()));
            response.headers().set("Content-Type", "application/json");
            response.headers().setInt("Content-Length", resStr.getBytes().length);

        } catch (Exception e) {
            e.printStackTrace();
            response = new DefaultFullHttpResponse(HTTP_1_1, NO_CONTENT);
            exceptionCaught(ctx, e);
        } finally {
            if (fullRequest != null) {
                if (!HttpUtil.isKeepAlive(fullRequest)) {
                    ctx.write(response).addListener(ChannelFutureListener.CLOSE);
                } else {
                    //response.headers().set(CONNECTION, KEEP_ALIVE);
                    ctx.write(response);
                }
            }
            ctx.flush();
            //ctx.close();
        }

    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
