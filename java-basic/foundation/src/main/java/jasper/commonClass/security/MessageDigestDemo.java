package jasper.commonClass.security;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class MessageDigestDemo {
    public static void main(String[] args) throws NoSuchAlgorithmException {
        final MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        String a = "12312";
        // 要计算hash值的数据
        messageDigest.update(a.getBytes(StandardCharsets.UTF_8));
        //  计算摘要值 16位
        final byte[] digest = messageDigest.digest();
        System.out.println("digest = " + Arrays.toString(digest));
    }
}
