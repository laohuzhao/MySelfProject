package com.sdk4.common.util;

import org.apache.commons.lang.StringUtils;

public class GPSFormatUtils {

    public static String format(String degree, String minute, String second) {
        String result = "";

        if (StringUtils.isNotEmpty(degree) && StringUtils.isNotEmpty(minute) && StringUtils.isNotEmpty(second)) {
            Double d = Double.parseDouble(degree) + Double.parseDouble(minute) / 60 + Double.parseDouble(second) / 3600;
            //result = String.format("%.6f", d);
            result = String.valueOf(d);
        }

        return result;
    }

}
