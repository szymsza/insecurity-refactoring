/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ntnuhtwg.insecurityrefactoring.base.tools;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import ntnuhtwg.insecurityrefactoring.base.GlobalSettings;
import ntnuhtwg.insecurityrefactoring.base.GlobalSettings;
import ntnuhtwg.insecurityrefactoring.base.Util;
import ntnuhtwg.insecurityrefactoring.base.Util;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Calendar;

/**
 *
 * @author blubbomat
 */
public class CodeFormatter {

    static File rootFolder = null;

    static final long randomDatetimeBegin = new Date(0).getTime();    // 1970-01-01

    static final long randomDatetimeEnd = new Date().getTime();       // now

    static String recentDatetime = null;

    static final SimpleDateFormat randomDateFormat = new SimpleDateFormat("yyyyMMddHHmm.ss");

    public enum ChangeTimestamps {
        TIMESTAMPS_KEEP,    // don't modify files' modify at timestamps
        TIMESTAMPS_NOW,     // set timestamps of all files to now
        TIMESTAMPS_RANDOM,  // set timestamps of all files randomly
        TIMESTAMPS_DECOY,   // set timestamps of most files to two years ago and a few files to now
    }

    public static ChangeTimestamps changeTimestamps = ChangeTimestamps.TIMESTAMPS_KEEP;

    public static boolean reformatCode = true;

    public static void formatFolder(String path) {
        if (!CodeFormatter.reformatCode && changeTimestamps == ChangeTimestamps.TIMESTAMPS_KEEP) {
            return;
        }

        File baseDir = new File(path);
        formatRecursively(baseDir);
    }

    private static void formatRecursively(File folder) {
//        format(folder);
        for (File file : folder.listFiles()) {
            String filePath = file.getAbsolutePath();

            if (file.isDirectory()) {
                formatRecursively(file);
                changeTimestamp(filePath);
                continue;
            }

            if(file.getName().endsWith(".php") && CodeFormatter.reformatCode){
                format(filePath);
            }

            changeTimestamp(filePath);
        }
    }

    private static void format(String phpFilePath) {
            String command = "php ./php-code-formatter/format.php " + phpFilePath;
            runCommand(command);
    }

    private static void changeTimestamp(String filePath) {
        if (CodeFormatter.changeTimestamps == ChangeTimestamps.TIMESTAMPS_KEEP) {
            return;
        }

        String touchOptions = ""; // set timestamps to now by default

        if (CodeFormatter.changeTimestamps == ChangeTimestamps.TIMESTAMPS_RANDOM) {
            touchOptions = "-m -t " + getRandomDatetime() + " ";
        } else if (CodeFormatter.changeTimestamps == ChangeTimestamps.TIMESTAMPS_DECOY) {
            int random = ThreadLocalRandom.current().nextInt(0, 10);

            // In case of decoy, set to recent date for 90% of files and now for 10%
            if (random != 9) {
                touchOptions = "-m -t " + getRecentDatetime() + " ";
            }
        }

        runCommand("touch " + touchOptions + filePath);
    }

    private static String getRandomDatetime() {
        long randomMillisSinceEpoch = ThreadLocalRandom
                .current()
                .nextLong(randomDatetimeBegin, randomDatetimeEnd);

        return randomDateFormat.format(new Date(randomMillisSinceEpoch));
    }

    private static String getRecentDatetime() {
        // Generate pseudo random date cca 2 years ago
        if (recentDatetime == null) {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.YEAR, -2);
            calendar.add(Calendar.MONTH, 3);
            calendar.add(Calendar.DATE, 9);
            calendar.add(Calendar.HOUR, -3);
            calendar.add(Calendar.MINUTE, -37);
            calendar.add(Calendar.SECOND, -13);
            recentDatetime = randomDateFormat.format(calendar.getTime());
        }

        return recentDatetime;
    }

    private static void runCommand(String command) {
        if (rootFolder == null)
            rootFolder = new File(GlobalSettings.rootFolder);

        try {
            Util.runCommand(command, rootFolder);
        } catch (IOException ex) {
            Logger.getLogger(CodeFormatter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(CodeFormatter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        formatFolder("/home/blubbomat/Development/simple");
    }
}
