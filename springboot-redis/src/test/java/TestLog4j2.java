import com.alibaba.fastjson.JSON;
import com.xck.RunMain;
import com.xck.proxy.AspectJInJson;
import com.xck.proxy.JSONService;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.appender.rolling.RollingFileManager;
import org.apache.logging.slf4j.Log4jLogger;
import org.aspectj.lang.annotation.Aspect;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.lang.reflect.Field;
import java.util.HashMap;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = RunMain.class)
public class TestLog4j2 {

    static Logger infoLog = LoggerFactory.getLogger("infoLog");
    static RollingFileManager manager;

    @Autowired
    private JSONService jsonService;

    @Test
    public void testjson(){
        jsonService.parse2String(new HashMap<>());
        System.out.println( JSON.toJSONString(new HashMap<>()));
    }

    @Before
    public void infoLog() throws Exception{

        if (infoLog instanceof Log4jLogger){
            Class clzz = Log4jLogger.class;
            Field field = clzz.getDeclaredField("logger");
            field.setAccessible(true);
            org.apache.logging.log4j.core.Logger logger
                    = (org.apache.logging.log4j.core.Logger)field.get(infoLog);
            RollingFileAppender appender = (RollingFileAppender) logger.getAppenders().get("infoLog");
            manager = appender.getManager();
        }
    }

    @Test
    public void logFlush() throws Exception {

        for (int i = 0; i < 1; i++)
            infoLog.info("fs");

        System.out.println("fffff1");

        Thread.sleep(10000);

        manager.flush();

        Thread.sleep(10000);

        for (int i = 0; i < 1; i++)
            infoLog.info("f");

        Thread.sleep(10000);

        System.out.println("fffff2");
        Thread.sleep(30000);
    }

    @Test
    public void logFilterInUserSwitch() {
        infoLog.info("yws", new Integer(1));
    }
}
