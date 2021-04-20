package com.xck.db;

import com.hskj.DigestUtil;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

/**
 * ff
 *
 * @author xuchengkun
 * @date 2021/03/17 12:23
 **/
public class Test {

    public static void main(String[] args) throws Exception{
        String srcPath = "C:\\Users\\xuchengkun\\Documents\\WXWork\\1688850069167591\\Cache\\File\\2021-03\\顺丰补发数据.txt";
        File file = new File(srcPath);

        FileReader fis = new FileReader(file);
        BufferedReader fr = new BufferedReader(fis);

        FileWriter fw = new FileWriter("D:\\顺丰补发output1.txt");
        BufferedWriter bw = new BufferedWriter(fw);

        int lineNumber = 0;
        String line = null;
        int dd = 0;
        Set<String> set = new HashSet<>();
        while ((line = fr.readLine()) != null){
            String[] smsInfoArr = line.split("\t");
            String mobile = DigestUtil.decryptData(smsInfoArr[0]);
            String msgContent = DigestUtil.decryptData(smsInfoArr[1]);
//            if(msgContent.contains("验证码")){
//                dd++;
//                continue;
//            }
            if(!set.add(mobile+msgContent)){
                System.out.println(lineNumber);
            }
//            msgContent = msgContent.replaceAll("【顺丰速运】", "");
            msgContent = msgContent.replaceAll("\n", "\\\\n");
            ++lineNumber;
            bw.write(mobile + "|" + msgContent + "\n");
        }
        System.out.println(lineNumber);
        System.out.println(dd);
        bw.flush();
        fr.close();
        bw.close();
    }
}
