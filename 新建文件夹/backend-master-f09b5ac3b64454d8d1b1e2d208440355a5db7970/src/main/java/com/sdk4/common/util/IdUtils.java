package com.sdk4.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class IdUtils {
    private static SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMddHHmmssSSS");
    private static SimpleDateFormat SDF_YYMMDDHHMMSS = new SimpleDateFormat("yyMMddHHmmss");
    private static SimpleDateFormat SDF_DATE = new SimpleDateFormat("yyyyMMdd");
    private static SimpleDateFormat SDF_AUCID_SUFFIX = new SimpleDateFormat("HHmm");
    
    private static AtomicInteger n = new AtomicInteger(0);

    public synchronized static String newId() {
        StringBuilder id = new StringBuilder();
        
        id.append(SDF.format(new Date()));
        id.append(String.format("%03d", n.incrementAndGet()));
        
        if (n.get() > 990) {
            n.set(0);
        }
        
        id.append(String.format("%03d", new Random().nextInt(1000)));

        return id.toString();
    }
    
    public synchronized static String generateID_() {
        StringBuilder id = new StringBuilder();
        
        id.append(SDF_YYMMDDHHMMSS.format(new Date()));
        id.append(String.format("%03d", n.incrementAndGet()));
        
        if (n.get() > 990) {
            n.set(0);
        }
        
        id.append(String.format("%01d", new Random().nextInt(10)));

        return id.toString();
    }
    
    public static String newStrId() {
        return UniqId.getInstance().getUniqIDHashString();
    }
    
    public static void main(String[] args) {
        System.out.println(newId());
    }

}
