package com.xck.jdk.file;

import java.io.File;
import java.io.FileInputStream;

public class UnicodeBitEndianTest {

    public static void main(String[] args) throws Exception{

        String path1 = "C:\\Users\\xuchengkun\\Desktop\\Untitled1.txt";
        String path2 = "C:\\Users\\xuchengkun\\Desktop\\Untitled2.txt";
        read(path1);
        read(path2);

    }

    public static void read(String path) throws Exception{
        FileInputStream fileInputStream = new FileInputStream(new File(path));

        try {
            byte[] b = new byte[8];
            fileInputStream.read(b);
            for(int i=0; i<b.length; i++){
                System.out.println(b[i]);
            }
        } finally {
            fileInputStream.close();
        }
    }
}
