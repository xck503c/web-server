package com.xck.socket.tftp;

import com.xck.socket.tftp.command.TFTPCommand;
import com.xck.socket.tftp.command.TFTPCommandFactory;
import org.apache.commons.lang.StringUtils;

import java.util.Scanner;

/**
 * @data 2021-04-04 21:00:00
 * @author xck
 */
public class TFTPTool {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        String command = null;
        while (!(command = scanner.nextLine()).equals("q")) {
            String[] commands = command.split(" ");
            for (int i=0; i<commands.length; i++) {
                if (StringUtils.isBlank(commands[i])) {
                    continue;
                }
                commands[i] = commands[i].trim();
            }

            try {
                TFTPCommand tftpCommand = TFTPCommandFactory.create(commands);
                tftpCommand.execute();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void test(){
        String ip = "49.235.32.249";
        int port = 69;
    }
}
