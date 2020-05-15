import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class TestJavaOOM {

    //-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=D:
    @Test
    public void testOOM(){
        for(int i=0; i<1000000; i++){
            alloc();
        }
    }

    public void alloc(){
        byte[] a = new byte[1024*1024];
    }

}
