import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Test;

public class TestByteBuf {

    @Test
    public void testreadsmppHeader(){
        ByteBuf byteBuf = Unpooled.buffer(12);
        System.out.println(byteBuf.readableBytes());
        System.out.println(byteBuf.readByte());
    }
}
