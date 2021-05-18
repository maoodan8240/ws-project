package ws.thirdPartyServer.features.http;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class QhHttpClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(QhHttpClient.class);
    private static final int HTTP_RETURN_200 = 200;
    private static final int HTTP_RETURN_299 = 299;

    public static String httpSynSend(HttpMethodEnums httpMethodEnums, String uri, String body) {
        if (httpMethodEnums == HttpMethodEnums.GET) {
            return httpSynSend_Get(uri);
        } else if (httpMethodEnums == HttpMethodEnums.POST) {
            return httpSynSend_Post(uri, body);
        }
        return null;
    }

    public static String httpSynSend_Get(String uri) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpResponse httpResponse;
        try {
            LOGGER.info("向第三方发送GET请求 完整URI={}", uri);
            HttpGet httpGet = new HttpGet(uri);
            httpResponse = httpClient.execute(httpGet);
            int status = httpResponse.getStatusLine().getStatusCode();
            if (isSuccess(status)) {
                return processEntity(httpResponse.getEntity());
            }
            return null;
        } catch (Exception e) {
            LOGGER.error("Get 发送出错!", e);
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                LOGGER.error("httpClient 关闭出错!", e);
            }
        }
        return null;
    }

    public static String httpSynSend_Post(String uri, String body) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpResponse httpResponse;
        try {
            LOGGER.info("向第三方发送POST请求 完整URI={}  body={}", uri, body);
            HttpPost httpPost = new HttpPost(uri);
            httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
            httpPost.setEntity(new StringEntity(body, "utf-8"));
            // 关闭Expect:100-Continue握手
            RequestConfig.Builder con = RequestConfig.custom();
            con.setExpectContinueEnabled(false);
            httpPost.setConfig(con.build());
            httpResponse = httpClient.execute(httpPost);
            int status = httpResponse.getStatusLine().getStatusCode();
            if (isSuccess(status)) {
                return processEntity(httpResponse.getEntity());
            }
            return null;
        } catch (Exception e) {
            LOGGER.error("Post 发送出错!", e);
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                LOGGER.error("httpClient 关闭出错!", e);
            }
        }
        return null;
    }

    private static boolean isSuccess(int status) {
        return status >= HTTP_RETURN_200 && status <= HTTP_RETURN_299;
    }

    public static String processEntity(HttpEntity entity) throws ParseException, IOException {
        return entity != null ? EntityUtils.toString(entity) : null;
    }
}
