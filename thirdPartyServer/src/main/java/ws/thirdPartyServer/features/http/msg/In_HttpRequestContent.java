package ws.thirdPartyServer.features.http.msg;

import org.apache.http.nio.protocol.HttpAsyncExchange;
import ws.common.utils.message.implement.AbstractInnerMsg;
import ws.protos.EnumsProtos.PlatformTypeEnum;
import ws.thirdPartyServer.features.http.HttpMethodEnums;


public class In_HttpRequestContent extends AbstractInnerMsg {
    private static final long serialVersionUID = 1L;
    private PlatformTypeEnum PlatformTypeEnum;
    private HttpMethodEnums httpMethodEnums;
    private HttpAsyncExchange httpexchange;
    private String requestData;
    private String postBody;

    public In_HttpRequestContent(PlatformTypeEnum PlatformTypeEnum, HttpMethodEnums httpMethodEnums, HttpAsyncExchange httpexchange, String requestData, String postBody) {
        this.PlatformTypeEnum = PlatformTypeEnum;
        this.httpMethodEnums = httpMethodEnums;
        this.httpexchange = httpexchange;
        this.requestData = requestData;
        this.postBody = postBody;
    }

    public PlatformTypeEnum getPlatformTypeEnum() {
        return PlatformTypeEnum;
    }

    public HttpMethodEnums getHttpMethodEnums() {
        return httpMethodEnums;
    }

    public HttpAsyncExchange getHttpexchange() {
        return httpexchange;
    }

    public String getRequestData() {
        return requestData;
    }

    public String getPostBody() {
        return postBody;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("PlatformTypeEnum =[").append(PlatformTypeEnum).append("]\n");
        sb.append("httpMethodEnums=[").append(httpMethodEnums).append("]\n");
        sb.append("httpexchange=[").append(httpexchange).append("]\n");
        sb.append("requestData=[").append(requestData).append("]\n");
        sb.append("postBody=[").append(postBody).append("]\n");
        return sb.toString();
    }
}
