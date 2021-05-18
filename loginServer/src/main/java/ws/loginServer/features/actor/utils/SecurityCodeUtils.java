package ws.loginServer.features.actor.utils;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ws.common.utils.codec.AESEncodeUtil;

import java.io.IOException;

public class SecurityCodeUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityCodeUtils.class);
    private static String BASE64_KEY_RESOURCE_PATH = "/key.aes.base64";

    private static String getEncryptKey() throws IOException {
        return IOUtils.toString(SecurityCodeUtils.class.getResource(BASE64_KEY_RESOURCE_PATH));
    }

    public static String encryptAsBase64String(SecurityCode securityCode) {
        try {
            return AESEncodeUtil.aesEncrypt(securityCode.toJsonString(), getEncryptKey());
        } catch (Exception e) {
            LOGGER.error("加密异常！", e);
            return null;
        }
    }

    public static SecurityCode decryptAsSecurityCode(String base64String) {
        try {
            return SecurityCode.parseFromJsonString(AESEncodeUtil.aesDecrypt(base64String, getEncryptKey()));
        } catch (Exception e) {
            LOGGER.error("解密异常！", e);
            return null;
        }
    }
}
