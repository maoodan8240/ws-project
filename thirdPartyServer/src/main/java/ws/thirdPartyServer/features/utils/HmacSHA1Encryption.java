package ws.thirdPartyServer.features.utils;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class HmacSHA1Encryption {

    private static final String MAC_NAME = "HmacSHA1";
    private static final String ENCODING = "UTF-8";

    /**
     * 使用HMAC-SHA1签名方法对对encryptText进行签名
     *
     * @paramencryptText被签名的字符串
     * @paramencryptKey密钥
     * @return返回被加密后的字符串
     * @throwsException
     */
    public static String hmacSHA1Encrypt(String encryptText, String encryptKey) throws Exception {
        byte[] data = encryptKey.getBytes(ENCODING);
        // 根据给定的字节数组构造一个密钥,第二参数指定一个密钥算法的名称
        SecretKey secretKey = new SecretKeySpec(data, MAC_NAME);
        // 生成一个指定Mac算法的Mac对象
        Mac mac = Mac.getInstance(MAC_NAME);
        // 用给定密钥初始化Mac对象
        mac.init(secretKey);
        byte[] text = encryptText.getBytes(ENCODING);
        // 完成Mac操作
        byte[] digest = mac.doFinal(text);
        StringBuilder sBuilder = bytesToHexString(digest);
        return sBuilder.toString();
    }

    /**
     * 转换成Hex
     *
     * @parambytesArray
     */
    public static StringBuilder bytesToHexString(byte[] bytesArray) {
        if (bytesArray == null)
            return null;
        StringBuilder sBuilder = new StringBuilder();
        for (byte b : bytesArray) {
            String hv = String.format("%02x", b);
            sBuilder.append(hv);
        }
        return sBuilder;
    }

    /**
     * 使用HMAC-SHA1签名方法对对encryptText进行签名
     *
     * @paramencryptData被签名的字符串
     * @paramencryptKey密钥
     * @return返回被加密后的字符串
     * @throwsException
     */
    public static String hmacSHA1Encrypt(byte[] encryptData, String encryptKey) throws Exception {
        byte[] data = encryptKey.getBytes(ENCODING);

        // 根据给定的字节数组构造一个密钥,第二参数指定一个密钥算法的名称
        SecretKey secretKey = new SecretKeySpec(data, MAC_NAME);

        // 生成一个指定Mac算法的Mac对象
        Mac mac = Mac.getInstance(MAC_NAME);

        // 用给定密钥初始化Mac对象
        mac.init(secretKey);

        // 完成Mac操作
        byte[] digest = mac.doFinal(encryptData);
        StringBuilder sBuilder = bytesToHexString(digest);

        return sBuilder.toString();
    }
}
