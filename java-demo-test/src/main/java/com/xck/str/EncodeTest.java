package com.xck.str;

import org.apache.commons.lang.CharEncoding;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

public class EncodeTest {

    static CharsetEncoder charsetEncoder = Charset.forName(CharEncoding.ISO_8859_1).newEncoder();

    public static void main(String[] args) {
        String s1 = "sdg35436534 sags%#@!~)({}[]|\\<>?,/..,";
        String s2 = "Fdsfsd干发高烧";

        System.out.println(charsetEncoder.canEncode(s1));
        System.out.println(charsetEncoder.canEncode(s2));
    }
}
