package com.treasuredata.tdautomation.pluginautomation.googledrive;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.treasuredata.tdautomation.util.CsvUtil;

import java.io.*;
import java.util.Random;
import java.util.zip.GZIPOutputStream;


public class GoogleDriveUtil extends CsvUtil {

    public static Logger LOGGER = LogManager.getLogger(GoogleDriveUtil.class.getName());


    public GoogleDriveUtil(){
        super();
    }


/** Compressing a file using Gzip compression java class **/
    public static void compressGZIP(File input, File output) throws IOException {
        try (GZIPOutputStream out = new GZIPOutputStream(new FileOutputStream(output))){
            try (FileInputStream in = new FileInputStream(input)){
                byte[] buffer = new byte[1024];
                int len;
                while((len=in.read(buffer)) != -1){
                    out.write(buffer, 0, len);
                }
            }
        }
    }
    /** replacing file content with random empty values **/
    public static void replaceFileWithRandomEmptyCells(String file,String replaceValue,String file2) throws FileNotFoundException,IOException {
        PrintWriter pw = new PrintWriter(file2);

        // BufferedReader object for file.txt
        BufferedReader br = new BufferedReader(new FileReader(file));
        Random random = new Random();
        String line = br.readLine();
        pw.println(line);
        line = br.readLine();

        while(line != null) {
            String data[] = line.split(",");
            int num = random.nextInt(data.length);

            for (int i = 0; i < data.length; i++) {
            String replacedLine = " ";
                if (i ==  num) {
                    data[i] = replaceValue;
                    for (int j =0;j< data.length-1;j++) {
                        replacedLine += data[j] + ",";
                    }
                    replacedLine += data[data.length-1];
                    pw.println(replacedLine);
                    break;
                }
            }
            line = br.readLine();

        }
        pw.flush();

    }
    /** replacing empty values with null values **/
    public static void replaceEmptyWithNull(String fileName, String condition, String newValue, String targetFile) throws FileNotFoundException,IOException{
        PrintWriter pw = new PrintWriter(targetFile);

        // BufferedReader object for file.txt
        BufferedReader br = new BufferedReader(new FileReader(fileName));

        String line = br.readLine();
        pw.println(line);
        line = br.readLine();


        while(line != null) {
            String data[] = line.split(",");
            for (int i = 0; i < data.length; i++) {
                String replacedLine = " ";
                if (data[i].equals("null")) {
                    data[i] = newValue;
                    for (int j =0;j< data.length-1;j++) {
                        replacedLine += data[j] + ",";
                    }
                    replacedLine += data[data.length-1];
                    pw.println(replacedLine);
                    //System.out.println(replacedLine);
                    break;
                }
            }
            line = br.readLine();

        }
        pw.flush();
    }

    /** merging file1 and file2 in file3  **/
    public void mergeFiles(String file1,String file2,String file3) {
        try {
            PrintWriter pw = new PrintWriter(file3);

            // BufferedReader object for file1.txt
            BufferedReader br1 = new BufferedReader(new FileReader(file1));
            BufferedReader br2 = new BufferedReader(new FileReader (file2));


            String line1 = br1.readLine();
            br2.readLine();
            String line2 = br2.readLine();

            // loop to copy lines of
            // file1.txt and file2.txt
            // to  file3.txt alternatively
            while (line1 != null || line2 != null) {
                if (line1 != null) {
                    pw.println(line1);
                    line1 = br1.readLine();
                }

                if (line2 != null) {
                    pw.println(line2);
                    line2 = br2.readLine();
                }
            }

            pw.flush();

            // closing resources
            br1.close();
            br2.close();
            pw.close();
        }catch(IOException ex){
            System.out.println(ex);
        }
    }




}
