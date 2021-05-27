package com.xck.jdk.robot;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;

public class RobotTest {
    public volatile static boolean status = true;

    public static HashMap<Character, Integer> keys = new HashMap<>();

    static {
        for(int i=48,j=0x30; i<48+10; i++){
            keys.put(((char)i), j++);
        }

        for(int i=97,j=0x41; i<97+26; i++){
            keys.put(((char)i), j++);
        }

        keys.put(' ', KeyEvent.VK_SPACE);
        keys.put('*', KeyEvent.VK_MULTIPLY);
        keys.put('\'', KeyEvent.VK_QUOTE);
        keys.put('.', KeyEvent.VK_PERIOD);
        keys.put(';', KeyEvent.VK_SEMICOLON);
    }

    public static void main(String[] args) throws Exception{
//        Thread.sleep(3000);
//        System.out.println(getMouseInfoPos());
        KeyListen();
        startRobot();
    }
    public static void KeyListen(){
        JFrame jFrame = new JFrame();
        JPanel jPanel = new JPanel();
        jFrame.add(jPanel);
        jFrame.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {
                status = false;
                System.out.println("停止");
            }
        });
        jFrame.setSize(400, 300);

        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        jFrame.setVisible(true);
    }

    public static void startRobot(){
        try {
            Robot robot = new Robot();
            Thread.sleep(3000);

            clickMouse(robot, 955, 874);
            clickMouse(robot, 628, 613);

            long start = System.currentTimeMillis();
            long time = 1*60*1000L;
            while (status && (System.currentTimeMillis() - start) <= time){
                pressString(robot, "select * from information_schema.processlist where INFO like 'select% submit_message_check_group %';");
                Thread.sleep(200);
            }
        } catch (AWTException e) {
            e.printStackTrace();
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    public static void clickMouse(Robot robot, int x, int y){
        robot.mouseMove(x, y);
        robot.delay(100);
        robot.mousePress(InputEvent.BUTTON1_MASK);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);
        robot.delay(100);
    }

    public static void pressString(Robot robot, String printMsg){
        for (int i=0; i<printMsg.length(); i++) {
            char c = printMsg.charAt(i);
            if(c == '_'){
                robot.keyPress(KeyEvent.VK_SHIFT);
                robot.keyPress(KeyEvent.VK_MINUS);
                robot.keyRelease(KeyEvent.VK_MINUS);
                robot.keyRelease(KeyEvent.VK_SHIFT);
                robot.delay(5);
            }else if(c == '%'){
                robot.keyPress(KeyEvent.VK_SHIFT);
                robot.keyPress(KeyEvent.VK_5);
                robot.keyRelease(KeyEvent.VK_5);
                robot.keyRelease(KeyEvent.VK_SHIFT);
                robot.delay(5);
            }else if(c >= 65 && c <= 90){
                c = (char) (((int)c)+32);
                robot.keyPress(KeyEvent.VK_SHIFT);
                robot.keyPress(keys.get(c));
                robot.keyRelease(keys.get(c));
                robot.keyRelease(KeyEvent.VK_SHIFT);
                robot.delay(5);
            } else {
                robot.keyPress(keys.get(c));
                robot.keyRelease(keys.get(c));
                robot.delay(2);
            }
        }
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.keyRelease(KeyEvent.VK_ENTER);
        robot.delay(50);
    }

    public static Point getMouseInfoPos(){
        try {
            PointerInfo pointerInfo = MouseInfo.getPointerInfo();
            return pointerInfo.getLocation();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
