package com.treasuredata.tdautomation.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CmdUtil {

    static Logger LOGGER = LogManager.getLogger(CmdUtil.class.getName());
    public static long TIME_OUT = 300;
    public static String CMD = (Util.getOsName().contains("Windows")) ? "src/main/scripts/cmd.bat" : "src/main/scripts/cmd.sh";
    public static String ONE_ONE_CMD = (Util.getOsName().contains("Windows")) ? "src/main/scripts/11cmd.bat" : "src/main/scripts/11cmd.sh";

    public static String execute(List<String> command){
        return execute(command, TIME_OUT);
    }

    /*
     - Execute command using process builder
     */
    public static String execute(List<String> command, long timeout) {
        String cmdOutput = "";
        try {
            ProcessBuilder builder = new ProcessBuilder();
            builder.command(command);
            builder.redirectErrorStream(true);
            Process p = builder.start();
            try {
                p.waitFor(timeout, TimeUnit.SECONDS);
                if (p.isAlive()) {
                    p.destroy();
                    p.waitFor();
                }
            } catch (InterruptedException e) {
                LOGGER.info(e.getMessage());
            }
            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;

            while (true) {
                line = r.readLine();

                if (line == null) break;
                cmdOutput += line + "\n";
            }
        } catch (IOException e) {
            LOGGER.error("{} or The step takes more than {} seconds to finish so it gets killed", e.toString(), TIME_OUT);
        }
        LOGGER.info(cmdOutput);
        return cmdOutput;
    }

    /*
     - Execute command using Runtime.exec()
     */
    public static boolean execute(String cmd, String[] args) {
        try {
            LOGGER.info(cmd);
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec(cmd, args);
            InputStream stdin = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(stdin);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            LOGGER.info("<OUTPUT>");

            while ((line = br.readLine()) != null)
                LOGGER.info(line);
            LOGGER.info("</OUTPUT>");
            int exitVal = proc.waitFor();
            LOGGER.info("Process exitValue: " + exitVal);
            return true;
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return false;
    }

    /*
     - Execute command using Runtime.exec()
     */
    public static boolean execute(String[] args) {
        try {
            LOGGER.info(args.toString());
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec(args);
            InputStream stdin = proc.getInputStream();
            InputStreamReader isr = new InputStreamReader(stdin);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            LOGGER.info("<OUTPUT>");
            while ((line = br.readLine()) != null)
                LOGGER.info(line);
            LOGGER.info("</OUTPUT>");
            int exitVal = proc.waitFor();
            LOGGER.info("Process exitValue: " + exitVal);
            return true;
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return false;
    }

    /*
     - Default parameters:
        - query type: -T presto
        - file format: -f csv
        - wait for finish the job: -w
     */
    public static String tdExport(String command, String query) {
        String[] params = command.split(" ");
        List<String> cmd = new ArrayList<String>();
        cmd.add(String.format("%s/%s", Constant.PATH, CMD));
        for (int i = 0; i < params.length; i++)
            cmd.add(params[i]);
        cmd.add(query);
        return execute(cmd);
    }

    public static String executeUsingCmd(String command) {
        String[] params = command.split(" ");
        List<String> cmd = new ArrayList<String>();
        String toConsole = "";
        cmd.add(String.format("%s/%s", Constant.PATH, CMD));
        for (int i = 0; i < params.length; i++) {
            if (params[i].contains("[*]")) {
                toConsole += "**** ";
                cmd.add(params[i].replace("[*]", ""));
            } else {
                toConsole += params[i] + " ";
                cmd.add(params[i]);
            }
        }

        LOGGER.info("Execute command {}", toConsole);
        return execute(cmd);
    }

    public static String oneByOneExecute(String[] commands) {
        List<String> cmd = new ArrayList<String>();
        cmd.add(String.format("%s/%s", Constant.PATH, ONE_ONE_CMD));
        for (int i = 0; i < commands.length; i++)
            cmd.add(commands[i]);
        return execute(cmd);
    }

    public static String oneByOneExecuteWithTdAccount(String[] commands) {
        List<String> cmd = new ArrayList<String>();
        cmd.add(0, "td account");
        cmd.add(String.format("%s/%s", Constant.PATH, ONE_ONE_CMD));
        for (int i = 0; i < commands.length; i++)
            cmd.add(commands[i]);
        return execute(cmd);
    }

    public static String generateData(String outputPath) {
        String cmd = String.format("%s/%s", Constant.PATH, CMD);
        String pythonCmd = "python %s %s";
        String[] params = String.format(pythonCmd, Constant.PYTHON_MAIN, outputPath).split(" ");
        List<String> command = new ArrayList<String>();
        command.add(cmd);
        for (int i = 0; i < params.length; i++)
            command.add(params[i]);
        return execute(command);
    }
}
