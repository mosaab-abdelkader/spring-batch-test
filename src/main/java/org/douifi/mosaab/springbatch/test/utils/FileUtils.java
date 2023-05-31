package fr.sfr.sumo.xms.srr.alim.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.stream.Stream;

public class FileUtils {

    private static final Logger logger = LoggerFactory.getLogger(FileUtils.class);
    public static long lineNumbers = 0;
    private static final String TMP_PATH="/tmp/";
    private static final String TMP_NAME="_chksFich.tmp";
    private static final int NBCOL=81;
    private static final int ABST=82;

    public static void InputStreamToFile(InputStream inputStream, File file) throws IOException {
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            bufferedReader.lines().forEach( sCurrentLine -> {
                try {
                    writer.write(sCurrentLine + "\n");
                } catch (IOException e) {
                	logger.error("Error to transform inputStream to flat file");
                    throw new RuntimeException(e);
                }
            });

            writer.close();
        }


    }
    public static File fileWithoutFirstAndLastLine( File file) throws IOException {
        try (Stream<String> lines = Files.lines(Paths.get(file.getPath()))) {
        	
            File fileForChksFich = new File(TMP_PATH + file.getName() + TMP_NAME);
            BufferedWriter ChksFichWriter = new BufferedWriter(new FileWriter(fileForChksFich));
            lines.toList().subList(1, (int)lineCounts(file) - 1).forEach(str -> {
                try {
                    ChksFichWriter.write(str + "\n");
                } catch (IOException e) {
                	logger.error("Error to prepare file for checkSum");
                    throw new RuntimeException(e);
                }
            });

            ChksFichWriter.close();
            lineNumbers = lineCounts(new File(TMP_PATH + file.getName() + TMP_NAME));
            return fileForChksFich;
        }
    }

    public static long lineCounts(File file) throws IOException {
        try (Stream<String> stream = Files.lines(Path.of(file.getPath()), StandardCharsets.UTF_8)) {
            return stream.count();
        }
    }

    public static String getFirstLine(File file) throws IOException {
        try (Stream<String> lines = Files.lines(Path.of(file.getPath()))) {
            return lines.findFirst().orElse(null);
        }
    }

    public static String getLastLine(File file) throws IOException {
        try (Stream<String> lines = Files.lines(Path.of(file.getPath()))) {
            return lines.skip(lineCounts(file) - 1).findFirst().orElse(null);
        }
    }

    public static void writeRejectedTicket(String fileName, String ticket, int codeErreur, int numrFich) {

        // logger.info("file >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> : {} not found",fileName + ".TICKETS_REJETES");

        FileOutputStream fop = null;
        File file;
        file = new File(fileName + ".TICKETS_REJETES");
        String errorString="";

        try {
        	
        	if(codeErreur==NBCOL) {
                errorString = "NBCOL";
            }else if(codeErreur==ABST) {
        		errorString="ABST";

        	}
            fop = new FileOutputStream(file,true);

            if (!file.exists())
                file.createNewFile();

            fop.write((errorString + ";" + ticket+ ";" + numrFich + "\n" ).getBytes());        // TODO : mettre les différents codes d'erreur dans une enum

            logger.info("write to file >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>:{} le ticket {}", fileName + ".TICKETS_REJETES", ticket);

            fop.flush();
            fop.close();

        } catch (FileNotFoundException e) {
            logger.error("file : {} not found", fileName + ".TICKETS_REJETES");

        } catch (IOException e) {
            logger.error("Exception : {} read write file", fileName + ".TICKETS_REJETES");

        } finally {
            try {
                if (fop != null)
                    fop.close();

            } catch (IOException e) {
                logger.error("Exception to close file --> {}" , fileName + ".TICKETS_REJETES");
            }
        }
    }
    
    
    
    public  static String computeChecksum(String filePath) throws NoSuchAlgorithmException, IOException {

        MessageDigest md = MessageDigest.getInstance("SHA-256");
        try (DigestInputStream dis = new DigestInputStream(new FileInputStream(filePath), md)) {
            while (dis.read() != -1); //empty loop to clear the data
            md = dis.getMessageDigest();
        }

        // bytes to hex
        StringBuilder result = new StringBuilder();
        for (byte b : md.digest()) {
            result.append(String.format("%02x", b));
        }
        logger.info("\n** Checksum of File content ** {}", result);

        return result.toString();
    }

}
