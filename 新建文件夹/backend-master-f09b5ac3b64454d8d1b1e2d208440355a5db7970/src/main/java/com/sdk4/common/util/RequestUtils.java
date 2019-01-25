package com.sdk4.common.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.servlet.http.HttpServletRequest;

public class RequestUtils {
    private static final String[] IP_HEADERS = { "X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP",
            "X-Real-IP", "NS-Client-IP" };

    /**
     * 获取客户端IP地址，此方法用在proxy环境中
     * 
     * @param req
     * @return
     */
    public static final String getRemoteAddress(HttpServletRequest request) {
        String ip = null;
        boolean valid = false;

        for (String header : IP_HEADERS) {
            ip = request.getHeader(header);
            valid = checkIP(ip);
            if (valid) {
                break;
            }
        }

        if (!valid) {
            ip = request.getRemoteAddr();
        }

        int index = ip.indexOf(',');
        if (index != -1) {
            String firstIp = ip.substring(0, index).trim();
            if (checkIP(ip)) {
                ip = firstIp;
            }
        }

        return ip;
    }

    private static final boolean checkIP(String ip) {
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            return false;
        }

        return true;
    }

    public static String getStreamAsString(InputStream stream, String charset) throws IOException {
        try {
            Reader reader = new InputStreamReader(stream, charset);
            StringBuilder response = new StringBuilder();

            final char[] buff = new char[1024];
            int read = 0;
            while ((read = reader.read(buff)) > 0) {
                response.append(buff, 0, read);
            }

            return response.toString();
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }
}
