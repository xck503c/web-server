import com.xck.InsertSubmitThread;
import com.xck.MissionConfig;
import com.xck.ThreadPoolTransferThread;
import org.junit.Test;

public class TestDBOper {

    @Test
    public void insert(){
        InsertSubmitThread thread = new InsertSubmitThread();
        thread.insert(1000);
    }

    @Test
    public void queryMin(){
        ThreadPoolTransferThread thread = new ThreadPoolTransferThread(new MissionConfig());
//        System.out.println(thread.getMinSn());;
    }

    @Test
    public void transferData(){
        ThreadPoolTransferThread thread = new ThreadPoolTransferThread(new MissionConfig());
//        thread.transferData("bak", "3,999,1000");
    }

    @Test
    public void testNum(){
        ThreadPoolTransferThread thread = new ThreadPoolTransferThread(new MissionConfig());
        System.out.println(thread.executePrimaryKeyQuery("select sn from submit_message_send_history order by sn desc limit 1"));
    }
}
