package com.jreddit.botkernel;

import java.io.*;
import java.util.*;


/**
 *
 * Some common utils 
 *
 */
public class Utils {

    /**
     *
     * Load a file into a List.
     *
     * Load the specified file line by line into a list for the given
     * number of lines.
     *
     * @param filename  The name of the file
     * @param list      The list on which to add items
     * @param numLines  The max number of lines to read from the file.
     *
     */
    public static void loadList(String filename, 
                                List<String> list, 
                                int numLines) {

        try {
            FileInputStream fis = new FileInputStream(filename);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);

            int i = 0;
            String line = null;

            while((line = br.readLine()) != null) {
                list.add(line.trim());
                i++;
                if(i == numLines) {
                    break;
                }
            }

            br.close();
            isr.close();
            fis.close();

        } catch (IOException e) {
            e.printStackTrace();
            BotKernel.getBotKernel().log("Error loading file " + filename);
        }
    }


}
