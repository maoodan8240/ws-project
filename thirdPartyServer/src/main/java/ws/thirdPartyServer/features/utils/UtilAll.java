/**
 * This file is part of Aion Core <aioncore.com>
 * <p>
 * This is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser Public License
 * along with this software.  If not, see <http://www.gnu.org/licenses/>.
 */

package ws.thirdPartyServer.features.utils;

import com.sun.org.apache.xerces.internal.impl.dv.util.HexBin;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.nio.protocol.BasicAsyncResponseProducer;
import org.apache.http.nio.protocol.HttpAsyncExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author lord_rex
 */
public class UtilAll {

    private static final Logger log = LoggerFactory.getLogger(UtilAll.class);

    public static final String GET_METHOD = "get";
    public static final String POST_METHOD = "post";

    public static final int GET_SUCCEED_STATUS = 200; // 请求成功
    public static final int POST_SUCCEED_STATUS = 201; // 上传数据成功
    public static final int PROCESSING_SUCCEED_STATUS = 202; // 请求已经接受但是还没处理

    /**
     * @param s
     */
    public static void printSection(String s) {
        s = "-[ " + s + " ]";

        while (s.length() < 79)
            s = "=" + s;
    }

    // 专门用来解析 key1=value1&key2=value2 类似的字符串
    public static Map<String, String> parseFormatString(String str, int expectNum) {
        return parseFormatString(str, expectNum, true);
    }

    // 专门用来解析 key1=value1&key2=value2 类似的字符串
    public static Map<String, String> parseFormatString(String str, int expectNum, boolean decode) {
        Map<String, String> valmap = null;

        if (str == null)
            return null;

        try {
            String[] keyvalues = str.split("&");
            if (keyvalues.length < expectNum) {
                log.error("expectNum=" + expectNum + ",actNum=" + keyvalues.length);
                return null;
            }

            valmap = new TreeMap<String, String>();

            for (String kv : keyvalues) {
                String[] vals = kv.split("=");
                if (vals.length == 2) {
                    String context = vals[1];
                    if (decode)
                        context = URLDecoder.decode(vals[1]);
                    valmap.put(vals[0], context);
                } else if (vals.length == 1) {
                    valmap.put(vals[0], "");
                } else {
                    log.error("kv=" + kv + "format err");
                    return null;
                }
            }
        } catch (Exception e) {
            log.error("parse key err, ", e);
            return null;
        }

        return valmap;
    }

    public static Map<String, String> parseFormatString(String str, int expectNum, int dummy) {
        Map<String, String> valmap = null;

        if (str == null)
            return null;

        try {
            String[] keyvalues = str.split("&");
            if (keyvalues.length < expectNum) {
                log.error("expectNum=" + expectNum + ",actNum=" + keyvalues.length);
                return null;
            }

            valmap = new TreeMap<String, String>();

            for (String kv : keyvalues) {
                int index = kv.indexOf("=");
                String key = kv.substring(0, index);
                String value = kv.substring(index + 1);
                valmap.put(key, value);
            }
        } catch (Exception e) {
            log.error("parse key err, ", e);
            return null;
        }

        return valmap;
    }

    public static String GetStringMD5(final String str) {
        String signStr = null;
        try {
            byte[] bytes = str.getBytes("utf-8");
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(bytes);
            byte[] md5Byte = md5.digest();
            if (md5Byte != null) {
                signStr = HexBin.encode(md5Byte);
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return signStr;
    }

    public static String md5(String str) {
        StringBuilder sb = new StringBuilder();
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(str.getBytes("UTF8"));
            byte bytes[] = m.digest();

            for (int i = 0; i < bytes.length; i++) {
                if ((bytes[i] & 0xff) < 0x10) {
                    sb.append("0");
                }
                sb.append(Long.toString(bytes[i] & 0xff, 16));
            }
        } catch (Exception e) {
        }
        return sb.toString();
    }

    public static String processEntity(HttpEntity entity) throws IllegalStateException, IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(entity.getContent()));
        String line, result = "";
        StringBuilder sBuilder = new StringBuilder(result);
        while ((line = br.readLine()) != null) {
            sBuilder.append(line);
        }
        result = sBuilder.toString();
        return result;
    }

    public static void replyRequstResult(HttpAsyncExchange httpexchange, String result) {
        HttpResponse response = httpexchange.getResponse();
        NStringEntity entity = null;
        try {
            entity = new NStringEntity(result);
        } catch (Exception e) {
            log.error("ReplyRequstResult NStringEntity error", e);
            return;
        }
        response.setEntity(entity);
        httpexchange.submitResponse(new BasicAsyncResponseProducer(response));
    }

    public static void cancelRequest(HttpAsyncExchange httpexchange) {
        httpexchange.submitResponse();
    }
}
