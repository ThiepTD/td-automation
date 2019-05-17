package td_automation.Util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CmdUtil {

    static Logger LOGGER = LogManager.getLogger(CmdUtil.class.getName());

    /*
     - Execute command using process builder
     */
    public static String execute(List<String> command) {
        String cmdOutput = "";
        try {
            ProcessBuilder builder = new ProcessBuilder();
            builder.command(command);
            builder.redirectErrorStream(true);
            Process p = builder.start();
            BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;

            while (true) {
                line = r.readLine();

                if (line == null) break;
                cmdOutput += line + "\n";
            }
        } catch (IOException e) {
            LOGGER.error(e.toString());
        }
        LOGGER.info(cmdOutput);
        return cmdOutput;
    }

    /*
     - Execute command using Runtime.exec()
     */
    public static boolean execute(String cmd, String [] args) {
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
    public static boolean execute(String [] args) {
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
    public static String tdExport(String dbName, String output, String query) {

        List<String> cmd = new ArrayList<String>();
        cmd.add(String.format("%stdQuery.sh", Constant.RESOURCE_PATH));
        cmd.add(dbName);
        cmd.add(output);
        cmd.add(query);
        return execute(cmd);
    }

    public static String generateData() {
        String cmd = String.format("%scmd.sh", Constant.RESOURCE_PATH);
        String folder = Constant.RESOURCE_PATH;
        String executeCmd = "sudo python python/data_generator/main.py thieptruong";
        List<String> command = new ArrayList<String>();
        command.add(cmd);
        command.add(folder);
        command.add(executeCmd);
        return execute(command);
    }
 }
