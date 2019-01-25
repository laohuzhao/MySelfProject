package com.sdk4.common.util;

public class PageUtils {

    public static int pageCount(int total, int page_size) {
        int page_count = 1;

        if (total > page_size) {
            page_count = total / page_size + (total % page_size > 0 ? 1 : 0);
        }

        return page_count;
    }
    
    public static int calcStart(int page_index, int page_size) {
        int start = (page_index - 1) * page_size;
        
        return start;
    }
    

}
