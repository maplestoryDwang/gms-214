import java.io.UnsupportedEncodingException;

public class UTFTest {

    public static void main(String[] args) throws UnsupportedEncodingException {
        byte[] bytes = "刺客".getBytes("UTF-8");
        String gbk = new String(bytes, "GBK");
        System.out.println(gbk);

    }
}
