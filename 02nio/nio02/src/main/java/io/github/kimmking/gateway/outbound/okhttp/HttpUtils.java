package io.github.kimmking.gateway.outbound.okhttp;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Objects;

/**
 * @Description: HttpClientUtils
 * @Date: 2020/10/28 18:04
 * @Author: wp
 **/
public class HttpUtils {

    public static final String url ="http://localhost:8088";
    public static final int success  = 200;

    public static String callGet(String url) throws IOException {
        OkHttpClient httpClient = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        Response response = httpClient.newCall(request).execute();
        if (response.isSuccessful()){
            return Objects.requireNonNull(response.body()).string();
        }
        return null;
    }

    public static void main(String[] args) throws IOException, URISyntaxException {
        String response = HttpUtils.callGet(url);
        if (response == null) {
            throw new RuntimeException("调用失败");
        }
        System.out.println(response);
    }
}
