import com.xck.cmpp.CmppConnectMessage;
import com.xck.cmpp.CmppHeader;
import com.xck.cmpp.CmppMessage;
import com.xck.util.StringUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Test;

import java.nio.charset.Charset;

public class TestConnectRespMsg {
    private int seq = 0;

    @Test
    public void testRespAndConnMsg(){
//        try {
//            int timeStamp = (int)(System.currentTimeMillis()/1000);
//            CmppHeader header = new CmppHeader(CmppHeader.CONNRCT_BODY_LEN, CmppMessage.CONNECT, seq);
//            CmppConnectMessage connectMessage = new CmppConnectMessage(header, "xck001", "xck123", timeStamp);
//            System.out.println(connectMessage);
//            CmppConnectMessage connectMessage1 = (CmppConnectMessage)CmppMessage.createMessage(connectMessage.getMessageBuf());
//            System.out.println(connectMessage1);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    @Test
    public void md5(){
        int timestamp = (int)(System.currentTimeMillis()/1000);
        ByteBuf md5Buf = Unpooled.buffer(16);
        md5Buf.writeCharSequence("xck001", Charset.forName("GBK"));
        md5Buf.writeBytes(new byte[9]);
        md5Buf.writeCharSequence("xck123", Charset.forName("GBK"));
        md5Buf.writeInt(timestamp);
        String s1 = StringUtils.md5ConvertByte(new String(md5Buf.array()), "ISO8859_1");
        String s2 = StringUtils.md5ConvertByte(new String(md5Buf.array()), "ISO8859_1");
        System.out.println(s1.equalsIgnoreCase(s2));;

    }
}
