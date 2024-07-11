import com.riteny.simpleauth.SimpleAuthService;
import com.riteny.simpleauth.datasource.impl.PropertiesUserDataSource;
import org.apache.commons.codec.binary.Hex;

import javax.security.auth.login.LoginException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Test {
    public static void main(String[] args) throws NoSuchAlgorithmException {

        String password = "e10adc3949ba59abbe56e057f20f883e";
        Long timestamp = System.currentTimeMillis();
        String data = password + timestamp;

        MessageDigest messageDigest = MessageDigest.getInstance("MD5");

        byte[] bytes = messageDigest.digest(data.getBytes());

        System.out.println(timestamp);
        System.out.println(Hex.encodeHexString(bytes));
    }
}
