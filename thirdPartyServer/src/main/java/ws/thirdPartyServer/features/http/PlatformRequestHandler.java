package ws.thirdPartyServer.features.http;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.nio.protocol.BasicAsyncRequestConsumer;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.apache.http.nio.protocol.HttpAsyncRequestConsumer;
import org.apache.http.nio.protocol.HttpAsyncRequestHandler;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.protos.EnumsProtos.PlatformTypeEnum;
import ws.relationship.base.cluster.ActorSystemPath;
import ws.thirdPartyServer.features.http.msg.In_HttpRequestContent;
import ws.thirdPartyServer.features.utils.UtilAll;
import ws.thirdPartyServer.system.actor.WsActorSystem;

import java.io.IOException;


public class PlatformRequestHandler implements HttpAsyncRequestHandler<HttpRequest> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlatformRequestHandler.class);

    @Override
    public void handle(final HttpRequest request, final HttpAsyncExchange httpexchange, final HttpContext context) throws HttpException, IOException {
        LOGGER.warn("\nAAA > 接受到第三方发送来的支付处理信息! \n  request={} \n  httpexchange={} \n  context={}", request, httpexchange, context);
        // 1. GET 或 POST 方法调用进行逻辑处理
        String method = request.getRequestLine().getMethod();
        String requestData = request.getRequestLine().getUri();
        HttpMethodEnums httpMethodEnums = HttpMethodEnums.parse(method);
        if (httpMethodEnums == null) {
            LOGGER.warn("request={} , 接受到非法请求={} !", request.toString(), requestData);
            return;
        }
        PlatformTypeEnum PlatformTypeEnum = HttpPlatform.parse(requestData);
        if (PlatformTypeEnum == null) {

            LOGGER.error("request={} , 不支持的平台={} !", request.toString(), requestData);
            return;
        }
        String postBody = null;
        if (httpMethodEnums == HttpMethodEnums.POST) {
            HttpEntityEnclosingRequest post = (HttpEntityEnclosingRequest) request;
            postBody = UtilAll.processEntity(post.getEntity());
        }
        sendMsgToPaymentCenter(new In_HttpRequestContent(PlatformTypeEnum, httpMethodEnums, httpexchange, requestData, postBody));
    }

    @Override
    public HttpAsyncRequestConsumer<HttpRequest> processRequest(HttpRequest arg0, HttpContext arg1) throws HttpException, IOException {
        return new BasicAsyncRequestConsumer();
    }

    private void sendMsgToPaymentCenter(In_HttpRequestContent content) {
        ActorSelection actorSelection = WsActorSystem.get().actorSelection(ActorSystemPath.WS_ThirdPartyServer_Selection_PaymentCenter);
        actorSelection.tell(content, ActorRef.noSender());
    }
}
