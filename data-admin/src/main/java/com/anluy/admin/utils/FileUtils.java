package com.anluy.admin.utils;

import org.mozilla.intl.chardet.nsDetector;
import org.mozilla.intl.chardet.nsPSMDetector;

import java.io.*;

/**
 * 功能说明：
 * <p>
 * Created by hc.zeng on 2018/6/2.
 */
public class FileUtils {

    public static String detectorCharset(File file) throws IOException {
        BufferedInputStream bis = null;
        try {
            bis = new BufferedInputStream(new FileInputStream(file));
            byte[] buf = new byte[4096];
            boolean isDone = false;
            int nread;
            nsDetector detector = new nsDetector(nsPSMDetector.ALL);
            while ((nread = bis.read(buf)) > 0 && !isDone) {
                isDone = detector.DoIt(buf, nread, false);
            }
            detector.DataEnd();
            String[] charsets = detector.getProbableCharsets();
            String encode = null;
            if (charsets != null && charsets.length > 0 && !"nomatch".equals(charsets[0])) {
                encode = charsets[0];
            }
            if ("Big5".equals(encode)) {
                encode = "GB2312";
            }
            if (encode == null) {
                encode = "GBK";
            }
            return encode;
        }catch (Exception e){
            throw e;
        }finally {
           if(bis!=null) {
               bis.close();
           }
        }
    }
}
