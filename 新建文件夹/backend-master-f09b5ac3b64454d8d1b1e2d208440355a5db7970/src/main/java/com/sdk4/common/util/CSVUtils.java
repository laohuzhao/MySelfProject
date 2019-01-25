package com.sdk4.common.util;

import java.util.ArrayList;
import java.util.List;

import com.csvreader.CsvReader;

public class CSVUtils {

	public static class Data {
		public int colcount = 0;
		public String[] headers = new String[0];
		public List<String[]> rows = new ArrayList<String[]>();
	}
	
	public static Data parse(CsvReader reader) {
		Data data = new Data();
		
		try {
			if (reader.readHeaders()) {
				data.colcount = reader.getHeaderCount();
				data.headers = new String[data.colcount];
				for (int i = 0; i < data.colcount; i++) {
					data.headers[i] = reader.getHeader(i);
				}
				
				while (reader.readRecord()) {
					String[] row = new String[data.colcount];
					int n = reader.getColumnCount();
					int min = Math.min(n, data.colcount);
					for (int i = 0; i < min; i++) {
						row[i] = reader.get(i);
					}
					data.rows.add(row);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return data;
	}

}
