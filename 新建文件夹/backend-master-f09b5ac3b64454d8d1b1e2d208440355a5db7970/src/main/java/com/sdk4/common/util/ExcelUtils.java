package com.sdk4.common.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * Excel导入/导出工具类
 * 
 * @author CnJun
 */
public class ExcelUtils {
	public final static String FIELD_STATUS = "_STATUS_";//状态字段
	public final static String FIELD_STATUSTEXT = "_STATUSTEXT_";//状态描述字段
	
	public static String getColName(int colIndex) {
		String ch;

		if (colIndex < 26) {
			ch = String.valueOf((char) ((colIndex) + 65));
		} else {
			ch = String.valueOf((char) ((colIndex) / 26 + 65 - 1)) + String.valueOf((char) ((colIndex) % 26 + 65));
		}
		return ch;
	}
	
	/**
	 * 导入Excel返回数据
	 */
	public static class ExcelDataResult {
		private int sheetIndex;//sheet序号
		private String sheetName;//sheet名称
		private int rows;//数据行数
		private int errorRows;//错误数据行数
		private String condition;//导入条件
		private Map<String, String> header;//
		private List<Map<String, Object>> data;//导入数据
		private List<Map<String, Object>> errorData;//条件测试失败数据
		
		public ExcelDataResult() {
			data = new ArrayList<Map<String, Object>>();
			errorData = new ArrayList<Map<String, Object>>();
			header = new HashMap<String, String>();
		}
		
		public void addRow(Map<String, Object> row) {
			data.add(row);
		}
		
		public void addErrorRow(Map<String, Object> row) {
			errorData.add(row);
		}
		
		public int getSheetIndex() {
			return sheetIndex;
		}
		
		public void setSheetIndex(int sheetIndex) {
			this.sheetIndex = sheetIndex;
		}
		
		public String getSheetName() {
			return sheetName;
		}
		
		public void setSheetName(String sheetName) {
			this.sheetName = sheetName;
		}
		
		public int getRows() {
			return rows;
		}
		
		public void setRows(int rows) {
			this.rows = rows;
		}
		
		public int getErrorRows() {
			return errorRows;
		}

		public void setErrorRows(int errorRows) {
			this.errorRows = errorRows;
		}

		public String getCondition() {
			return condition;
		}

		public void setCondition(String condition) {
			this.condition = condition;
		}

		public Map<String, String> getHeader() {
			return header;
		}

		public void setHeader(String dataIndex, String title) {
			this.header.put(dataIndex, title);
		}

		public List<Map<String, Object>> getData() {
			return data;
		}
		
		public void setData(List<Map<String, Object>> data) {
			this.data = data;
		}
		
		public List<Map<String, Object>> getErrorData() {
			return errorData;
		}

		public void setErrorData(List<Map<String, Object>> errorData) {
			this.errorData = errorData;
		}

		public int size() {
			return this.data.size();
		}
		
		public int errorSize() {
			return this.errorData.size();
		}
	}
	
	private static class ExcelImportConfig_Column {
		private String text;//标题
		private String dataIndex;//对应数据字段
		private String type;//类型
		private String format;//类型
		
		public String getText() {
			return text;
		}
		
		public void setText(String text) {
			this.text = text;
		}
		
		public String getDataIndex() {
			return dataIndex;
		}
		
		public void setDataIndex(String dataIndex) {
			this.dataIndex = dataIndex;
		}
		
		public String getType() {
			return type;
		}
		
		public void setType(String type) {
			this.type = type;
		}

		public String getFormat() {
			return format;
		}

		public void setFormat(String format) {
			this.format = format;
		}
	}
	
	/**
	 * 导入数据
	 * 
	 * 增加对.xlsx格式的支持
	 */
	public static List<ExcelDataResult> importExcel(File excelFile) throws Exception {
		return importExcel(excelFile, null);
	}
	
	@SuppressWarnings("resource")
	public static List<ExcelDataResult> importExcel(File excelFile, JSONObject config) throws Exception {
		ArrayList<ExcelDataResult> result = new ArrayList<ExcelDataResult>();
		
		Workbook wb;
		Sheet sheet;
		Row row;
		Cell cell;
		Map<String, SimpleDateFormat> dataFormat = new HashMap<String, SimpleDateFormat>();//日期格式
		
		Iterator<?> rows, cells;
		Object obj;
		String val;
		
		ArrayList<String> arr_header = new ArrayList<String>();
		
		InputStream input = new FileInputStream(excelFile);
		if (!input.markSupported()) {
			input = new PushbackInputStream(input, 8);
		}
		
		if (POIFSFileSystem.hasPOIFSHeader(input)) {
			wb = new HSSFWorkbook(input);
		} else if (POIXMLDocument.hasOOXMLHeader(input)) {
			wb = new XSSFWorkbook(input);
		} else {
			// 非法的输入流：当前输入流非OLE2流或OOXML流（不是一个有效的office2003或 office2007文件）
			// WorkbookFactory.create(input);
			throw new Exception("输入文件不是一个有效的office2003或 office2007文件。");
		}
		int sheetCount = wb.getNumberOfSheets();
		if (sheetCount < 1) {
			throw new Exception("空白的Excel文件。");
		}
		
		JSONArray ja_config = null;
		if (config != null && config.containsKey("sheets")) {
			ja_config = config.getJSONArray("sheets");
		}
		
		for (int n = 0; n < sheetCount; n++) {
			ExcelDataResult dataRowResult = new ExcelDataResult();
			
			int startRow = 1;// 开始行数(默认1)
	        int startCol = 1;// 开始列数(默认1)
	        boolean hasHeader = true;//是否包含Header头
	        boolean atLeastHeaderMatched = false;//至少有一个标题头配置项存在
	        //Expression compiledExpr = null;
	        
	        List<ExcelImportConfig_Column> columns_config = new ArrayList<ExcelImportConfig_Column>();
	        if (ja_config != null) {
	        	if (ja_config.size() <= n) {
	        		break;
	        	}
	        	
	        	JSONObject jo = ja_config.getJSONObject(n);
				
				if (jo.containsKey("startRow")) {
		        	startRow = jo.getIntValue("startRow");
		        }
		        if (jo.containsKey("startCol")) {
		        	startCol = jo.getIntValue("startCol");
		        }
		        if (jo.containsKey("hasHeader")) {
		        	hasHeader = jo.getBooleanValue("hasHeader");
		        }
		        if (jo.containsKey("condition")) {
		        	String condition = jo.getString("condition");
		        	if (condition != null && condition.length() > 0) {
		        		//compiledExpr = AviatorEvaluator.compile(condition);
		        	}
		        	dataRowResult.setCondition(condition);
		        } else {
		        	dataRowResult.setCondition("");
		        }
		        if (jo.containsKey("columns")) {
		        	JSONArray columns = jo.getJSONArray("columns");
		        	for (int j = 0; j < columns.size(); j++) {
		        		JSONObject jo_temp = columns.getJSONObject(j);
		        		ExcelImportConfig_Column eicc = new ExcelImportConfig_Column();
			        	eicc.setText(jo_temp.getString("text"));
			        	eicc.setDataIndex(jo_temp.getString("dataIndex"));
			        	eicc.setType(jo_temp.getString("type"));
			        	eicc.setFormat(jo_temp.getString("format"));
			        	columns_config.add(eicc);
			        	
			        	arr_header.add(eicc.getText());
			        	dataRowResult.setHeader(eicc.getDataIndex(), eicc.getText());
		        	}
		        }
	        }
	        
	        sheet = wb.getSheetAt(n);
	        rows = sheet.rowIterator();
	        
	        dataRowResult.setSheetIndex(n + 1);
	        dataRowResult.setSheetName(sheet.getSheetName());
	        
	        int col_index = 0;
	        Map<Integer, String> header = new HashMap<Integer, String>();
	        
	        // 
	        if (startRow > 1) {
	        	int j = startRow;
	        	while (rows.hasNext() && (--j > 0)) {
	        		rows.next();
	        	}
	        }
	        
	        // Header
	        while (hasHeader && rows.hasNext()) {
	        	row = (Row) rows.next();
				cells = row.cellIterator();
				col_index = 0;
				if (startCol > 1) {
					int j = startCol;
					while (cells.hasNext() && (--j > 0)) {
						cell = (Cell) cells.next();
					}
				}
				while (cells.hasNext()) {
					cell = (Cell) cells.next();
					col_index = cell.getColumnIndex() + 1;
					
					//val = cell.getStringCellValue();
					obj = getCellValue(cell);
					
					if (obj == null) {
						val = "C_" + (col_index - startCol + 1);
					} else {
						val = obj.toString();
						
						if (!atLeastHeaderMatched) {
							if (arr_header.contains(val)) {
								atLeastHeaderMatched = true;
							}
						}
					}
					header.put(col_index, val);
				}
				
				if (atLeastHeaderMatched) {
					break;
				}
	        }
	        // 数据行
			while (rows.hasNext()) {
				// 增加判断是否是空行，所有列都为NULL，则判定为空行
				boolean isAllCellNull = true;
				row = (Row) rows.next();
				cells = row.cellIterator();
				
				col_index = 0;
				Map<String, Object> rowData = new HashMap<String, Object>();
				if (startCol > 1) {
					int j = startCol;
					while (cells.hasNext() && (--j > 0)) {
						cells.next();
					}
				}
				while (cells.hasNext()) {
					cell = (Cell) cells.next();
					col_index = cell.getColumnIndex() + 1;
					
					String fieldName = "C_" + col_index;
					if (header.containsKey(col_index)) {
						fieldName = header.get(col_index);
					}
					obj = getCellValue(cell);
					rowData.put(fieldName, obj);
					if (isAllCellNull && obj != null) {
						isAllCellNull = false;
					}
				}
				
				if (isAllCellNull) {
					continue;
				}
				
				// 格式化处理行数据
				for (ExcelImportConfig_Column eicc : columns_config) {
					String text = eicc.getText();
					String dataIndex = eicc.getDataIndex();
					String type = eicc.getType();
					String format = eicc.getFormat();
					if (format != null && !dataFormat.containsKey(format)) {
						dataFormat.put(format, new SimpleDateFormat(format));
					}
					SimpleDateFormat sdf = dataFormat.get(format);
					if (StringUtils.isNotEmpty(text) && StringUtils.isNotEmpty(dataIndex)) {
						try {
							if (rowData.containsKey(text)) {
								Object col_v = rowData.remove(text);
								if ("int".equalsIgnoreCase(type)) {
                                    //rowData.put(dataIndex, Integer.parseInt(col_v == null ? "0" : col_v.toString()));
								    Float F = Float.parseFloat(col_v == null ? "0" : col_v.toString());
								    rowData.put(dataIndex, F.intValue());
								} else if ("float".equalsIgnoreCase(type)) {
									rowData.put(dataIndex, Float.parseFloat(col_v.toString()));
								} else if ("string".equalsIgnoreCase(type)) {
									rowData.put(dataIndex, String.valueOf(col_v));
								} else if ("date".equalsIgnoreCase(type)) {
									if (sdf != null && col_v != null) {
										rowData.put(dataIndex, sdf.parse(String.valueOf(col_v)));
									} else {
										if (col_v == null) {
											rowData.put(dataIndex, 0);
										} else {
											rowData.put(dataIndex, String.valueOf(col_v));
										}
									}
								} else {
									rowData.put(dataIndex, col_v);
								}
							} else {
								if ("int".equalsIgnoreCase(type)) {
									rowData.put(dataIndex, 0);
								} else if ("float".equalsIgnoreCase(type)) {
									rowData.put(dataIndex, 0.0);
								} else if ("string".equalsIgnoreCase(type)) {
									rowData.put(dataIndex, "");
								}
							}
						} catch (Exception e) {
							rowData.put(FIELD_STATUS, 500);
							String err = "[" + text + "]格式化(" + type + ")失败:" + e.getMessage();
							if (rowData.containsKey(FIELD_STATUSTEXT)) {
								rowData.put(FIELD_STATUSTEXT, rowData.get(FIELD_STATUSTEXT) + "\n" + err);
							} else {
								rowData.put(FIELD_STATUSTEXT, err);
							}
						}
					}
				}
				
				if (rowData.size() <= 0) {
					continue;
				}
				
				// 导入条件
				/*
				if (compiledExpr != null) {
					Boolean ret = (Boolean) compiledExpr.execute(rowData);
					if (!ret) {
						rowData.put(FIELD_STATUS, 500);
						String err = "条件测试失败：" + dataRowResult.getCondition();
						if (rowData.containsKey(FIELD_STATUSTEXT)) {
							rowData.put(FIELD_STATUSTEXT, rowData.get(FIELD_STATUSTEXT) + "\n" + err);
						} else {
							rowData.put(FIELD_STATUSTEXT, err);
						}
					}
				}*/
				
				if (rowData.containsKey(FIELD_STATUS) && Integer.parseInt(rowData.get(FIELD_STATUS).toString()) != 0) {
					dataRowResult.addErrorRow(rowData);
				} else {
					dataRowResult.addRow(rowData);
				}
			}
			
			dataRowResult.setErrorRows(dataRowResult.errorSize());
			dataRowResult.setRows(dataRowResult.size());
			
			result.add(dataRowResult);
		}
		
		return result;
	}
	
	public static byte[] exportExcelToBytes(JSONObject config, JSONArray data) throws Exception {
		Workbook book = createWorkbook(null, config, data, new String[]{}, new String[]{});
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		book.write(os);
		return os.toByteArray();
	}
	
	//
	public static boolean exportExcelToFile(String file, JSONObject config, JSONArray data) throws Exception {
		return exportExcelToFile(new File(file), config, data);
	}
	
	public static boolean exportExcelToFile(File file, JSONObject config, JSONArray data) throws Exception {
		return exportExcelToFile(file, config, data, new String[]{}, new String[]{});
	}
	
	public static boolean exportExcelToFile(File file, JSONObject config, JSONArray data, String[] arr_title, String[] arr_sheetName) throws Exception {
		Workbook book = createWorkbook(getFileExt(file.getName()), config, data, arr_title, arr_sheetName);
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		book.write(os);
		FileUtils.writeByteArrayToFile(file, os.toByteArray());
		return true;
	}
	
	/**
	 * 创建并获取Excel指定版本的workbook对象。
	 * 
	 * @param type
	 * @return 新创建的workbook对象。
	 */
	public static Workbook newWorkbook(String type) {
		Workbook book;
		if ("xlsx".equalsIgnoreCase(type)) {
			book = new XSSFWorkbook();
		} else {
			book = new HSSFWorkbook();
		}
		return book;
	}
	
	/**
	 * 导出Excel
	 */
	private static Workbook createWorkbook(String type, JSONObject config, JSONArray arr_records, String[] arr_title, String[] arr_sheetName) throws Exception {
		String filetype;
		if ("xlsx".equalsIgnoreCase(type)) {
			filetype = "xlsx";
		} else if ("xls".equalsIgnoreCase(type)) {
			filetype = "xls";
		} else {
			filetype = JsonUtils.getString(config, "filetype", "xls");
		}
		Workbook book = newWorkbook(filetype);
		
		// Excel 格式来源: config-配置；file-模板文件
		String xlstype = JsonUtils.getString(config, "xlstype");
		if ("config".equalsIgnoreCase(xlstype)) {
			if (!config.containsKey("sheets")) {
				throw new RuntimeException("excel export config error, not has 'sheets' item.");
			}
			
			JSONArray sheets = config.getJSONArray("sheets");
			for (int n = 0; n < sheets.size(); n++) {
				JSONObject jo_sheet_config = sheets.getJSONObject(n);
				
				JSONArray records;
				if (arr_records.size() > n) {
					records = arr_records.getJSONArray(n);
				} else {
					records = new JSONArray();
				}
				
				// 全局定义
				String sheetName = JsonUtils.getString(jo_sheet_config, "sheetName");//工作表
				boolean showTitle = jo_sheet_config.containsKey("showTitle") && jo_sheet_config.getBoolean("showTitle");//是否显示标题
				int startRow = jo_sheet_config.getIntValue("startRow");//开始行，从1开始计数
				int startCol = jo_sheet_config.getIntValue("startCol");//开始列，从1开始计数
				
				// 工作表
				Sheet sheet = book.createSheet("Sheet" + (n + 1));
				
				// 是否动态名称
				if (arr_sheetName != null && arr_sheetName.length > n && StringUtils.isNotEmpty(arr_sheetName[n])) {
					sheetName = arr_sheetName[n];
				}
				// 设置工作表名称
				if (StringUtils.isNotEmpty(sheetName)) {
					book.setSheetName(n, sheetName);
				}
				
				if (startRow > 0) {
					startRow--;
				} else {
					startRow = 0;
				}
				if (startCol > 0) {
					startCol --;
				} else {
					startCol = 0;
				}
				
				// 显示标题
				String title = "";
				int titleRow = 0;
				if (showTitle) {
					startRow = startRow + 1;
					titleRow = startRow - 1;
					sheet.createRow(titleRow);
				}
				if (arr_title != null && arr_title.length > n && StringUtils.isNotEmpty(arr_title[n])) {
					title = arr_title[n];
				}
				
				// 生成数据头
				Object values[] = createHeaders(sheet, jo_sheet_config.getJSONArray("columns"), startRow, startCol, jo_sheet_config.getJSONObject("header"));
				
				int headerCols = (Integer) values[0];
				int headerRows = (Integer) values[1];
				JSONArray fields = (JSONArray) values[2];
				
				boolean freezeHeader = false;
				if (freezeHeader) {
					sheet.createFreezePane(0, startRow);
				}
				if (showTitle) {
					createTitle(sheet, titleRow, startCol, title, headerCols, jo_sheet_config.getJSONObject("title"));
				}
				
				startRow += headerRows;
				
				// freezePane
				// sheet.createFreezePane(0, startRow);
				
				String dateFormat = "";
				String timeFormat = "";
				
				createRecords(sheet, records, fields, startRow, startCol, dateFormat, timeFormat, jo_sheet_config.getJSONObject("row"));
			}
		}
		
		return book;
	}
	
	private static void createTitle(Sheet sheet, int titleRowIndex, int startCol, String title, int headerCols, JSONObject config) {
		if (config == null) {
			config = new JSONObject();
		}
		
		if (StrUtils.isEmpty(title) && config.containsKey("name")) {
			title = config.getString("name");
		}
		
		if (config.containsKey("height")) {
			config.put("rowHeight", config.getIntValue("height"));
		}
		
		Cell cell;
		Row row = sheet.getRow(titleRowIndex);
		Object styles[] = createCellStyle(sheet.getWorkbook(), "title", config);
		
		row.setHeight((Short) styles[1]);
		sheet.addMergedRegion(new CellRangeAddress(titleRowIndex, titleRowIndex, startCol, headerCols - 1 + startCol));
		cell = row.createCell(startCol);
		cell.setCellStyle((CellStyle) styles[0]);
		cell.setCellValue(title);
	}
	
	/**
	 * 创建列标题。
	 * @param sheet Sheet对象。
	 * @param headers 原始列标题列表。
	 * @param startRow 开始行索引号。
	 * @param neptune 客户端是否为海王星主题。
	 * @return 对象数组，0项列宽度，1项列高度，2项字段元数据。
	 */
	private static Object[] createHeaders(Sheet sheet, JSONArray headers, int startRow, int startCol, JSONObject config) {
		if (config.containsKey("height")) {
			config.put("rowHeight", config.getIntValue("height"));
		}
		
		int i, j, x, y, colspan, rowspan;
		Workbook book = sheet.getWorkbook();
		Object result[];
		JSONObject header;
		JSONArray processedHeaders = new JSONArray();
		Object values[] = prepareHeaders(sheet, headers, processedHeaders, startRow, startCol, false, config);
		Cell cell, cells[][] = (Cell[][]) values[0];
		
		Object[] styles = createCellStyle(book, "header", config);
		CellStyle baseStyle = (CellStyle) styles[0], style;
		
		j = processedHeaders.size();
		for (i = 0; i < j; i++) {
			header = processedHeaders.getJSONObject(i);
			
			x = header.getIntValue("x");
			y = header.getIntValue("y");
			colspan = Math.max(header.getIntValue("colspan"), 0);
			rowspan = Math.max(header.getIntValue("rowspan"), 0);
			if (colspan > 0 || rowspan > 0) {
				//CellRangeAddress(int firstRow, int lastRow, int firstCol, int lastCol)
				sheet.addMergedRegion(new CellRangeAddress(y + startRow, y + startRow + rowspan, x + startCol, x + startCol + colspan));
			}
			cell = cells[x][y];
			style = book.createCellStyle();
			style.cloneStyleFrom(baseStyle);
			
			// 标题对齐方式
			short titleAlign = CellStyle.ALIGN_CENTER;
			if (config.containsKey("align")) {
				titleAlign = getAlignment(config.getString("align"), titleAlign);
			}
			style.setAlignment(getAlignment(header.getString("titleAlign"), header.containsKey("child") ? CellStyle.ALIGN_CENTER : titleAlign));
			cell.setCellStyle(style);
			cell.setCellValue(header.getString("text"));
		}
		result = new Object[3];
		result[0] = cells.length;
		result[1] = cells[0].length;
		result[2] = values[1];
		return result;
	}
	
	/**
	 * 预处理列标题，创建单元格方阵，并标记每个header的x,y,colspan,rowspan属性。
	 * @param sheet Excel的Sheet对象。
	 * @param rawHeaders 原始列标题列表。
	 * @param processedHeaders 处理后的列标题列表。
	 * @param startRow 开始行索引号。
	 * @param neptune 客户端是否为海王星主题。
	 * @return 对象数组，0项单元格方阵，1项字段元数据。
	 */
	private static Object[] prepareHeaders(Sheet sheet, JSONArray rawHeaders, JSONArray processedHeaders, int startRow, int startCol, boolean neptune, JSONObject config) {
		JSONArray leafs = new JSONArray();
		JSONObject node;
		Object[] result = new Object[2];
		int i, j, maxDepth, k, l, width;
		int flexWidth = 100;//flexColumnMaxWidth
		Row row;
		Object[] styles = createCellStyle(sheet.getWorkbook(), "header", config);
		CellStyle style = (CellStyle) styles[0];
		Cell cell, cells[][];
		short rowHeight = (Short) styles[1];
		double rate;

		if (neptune) {
			rate = 32.06;
		} else {
			rate = 36.55;
		}
		leafs.add(0);// 0项为headers最大深度
		markParents(leafs, rawHeaders, null, 0);
		maxDepth = leafs.getIntValue(0);
		leafs.remove(0);// 移除深度值
		j = leafs.size();
		for (i = 0; i < j; i++) {
			node = leafs.getJSONObject(i);
			if (node.containsKey("width")) {
				width = node.getIntValue("width");
			} else if (node.containsKey("flex")) {
				width = flexWidth;
			} else {
				width = 100;
			}
			sheet.setColumnWidth(i + startCol, (int) (width * rate));
			node.put("rowspan", maxDepth - node.getIntValue("y"));
			do {
				node.put("colspan", node.getIntValue("colspan") + 1);
				if (!node.containsKey("x")) {
					node.put("x", i);
					processedHeaders.add(node);
				}
			} while ((node = node.getJSONObject("parent")) != null);
		}
		maxDepth++;// maxDepth设置为以1为开始索引
		cells = new Cell[j][maxDepth];
		for (k = 0; k < maxDepth; k++) {
			row = sheet.createRow(k + startRow);
			row.setHeight(rowHeight);
			for (l = 0; l < j; l++) {
				cell = row.createCell(l + startCol);
				cell.setCellStyle(style);
				cells[l][k] = cell;
			}
		}
		result[0] = cells;
		result[1] = leafs;
		return result;
	}
	
	/**
	 * 标识header所有节点的父节点及其节点深度。
	 * @param leafs 子节点列表，系统遍历节点后把所有子节点添加到该列表中。
	 * @param headers 列标头列表。
	 * @param parent 父节点对象。
	 * @param depth 节点深度。
	 */
	private static void markParents(JSONArray leafs, JSONArray headers, JSONObject parent, int depth) {
		int i, j = headers.size();
		JSONObject header;
		JSONArray items;
		leafs.set(0, Math.max(leafs.getIntValue(0), depth));
		for (i = 0; i < j; i++) {
			header = headers.getJSONObject(i);
			header.put("y", depth);
			header.put("colspan", -1);
			header.put("rowspan", -1);
			if (parent != null) {
				header.put("parent", parent);
				parent.put("child", header);
			}
			items = header.getJSONArray("items");
			if (items != null) {
				markParents(leafs, items, header, depth + 1);
			} else {
				leafs.add(header);
			}
		}
	}
	
	/**
	 * 在Excel Sheet中添加正文记录内容。
	 * @param sheet Excel的Sheet对象。
	 * @param records 记录集数据。
	 * @param fields 字段元数据列表。
	 * @param startRow 开始行索引号。
	 */
	private static void createRecords(Sheet sheet, JSONArray records, JSONArray fields, int startRow, int startCol, String defaultDateFormat, String defaultTimeFormat, JSONObject config) {
		if (config.containsKey("height")) {
			config.put("rowHeight", config.getIntValue("height"));
		}
		
		int i, j = records.size(), k, l = fields.size();
		Cell cell;
		Row row;
		JSONObject record, field;
		String format, keyName, dataTypeStr, fieldNames[] = new String[l];
		Workbook book = sheet.getWorkbook();
		Object value, cellStyles[] = createCellStyle(book, "text", config);
		CellStyle style, dateStyle, dateTimeStyle;
		CellStyle baseStyle = (CellStyle) cellStyles[0];
		CellStyle colStyles[] = new CellStyle[l], dateTimeStyles[][] = new CellStyle[l][2];
		short rowHeight = (Short) cellStyles[1];
		String boolString = JsonUtils.getString(config, "boolText", "true,false");
		String boolStrings[], trueText = null, falseText = null, dateTimeStr;
		boolean useBoolString, hasTime, isRate[] = new boolean[l];
		int dataTypes[] = new int[l], dataType;
		double number;
		Date date;
		Object keyMaps[] = new Object[l];

		useBoolString = !boolString.isEmpty();
		if (useBoolString) {
			boolStrings = boolString.split(",");
			trueText = boolStrings[0];
			falseText = boolStrings[1];
		}
		for (k = 0; k < l; k++) {
			field = fields.getJSONObject(k);
			fieldNames[k] = (field.containsKey("field") ? field.getString("field") : field.getString("dataIndex"));
			
			style = book.createCellStyle();
			style.cloneStyleFrom(baseStyle);
			style.setAlignment(getAlignment(field.getString("align"), CellStyle.ALIGN_LEFT));
			if (Boolean.TRUE.equals(field.get("autoWrap"))) {
				style.setWrapText(true);
			}
			keyName = field.getString("keyName");
			if (keyName == null || keyName.isEmpty()) {
				keyMaps[k] = null;
				dataTypeStr = field.getString("type").toLowerCase();
			} else {
				//keyMaps[k] = KVBuffer.buffer.get(keyName);
				dataTypeStr = "string";
			}
			format = JsonUtils.getString(field, "format", "");
			isRate[k] = format.endsWith("%");
			if (dataTypeStr.equals("string")) {
				dataType = 1;
			} else if (dataTypeStr.startsWith("int")
					|| dataTypeStr.equals("float")
					|| dataTypeStr.equals("number")) {
				dataType = 2;
				if (!StrUtils.isEmpty(format)) {
					style.setDataFormat(book.createDataFormat().getFormat(format));
				}
			} else if (dataTypeStr.equals("date")) {
				dataType = 3;
				if (StrUtils.isEmpty(format)) {
					// 未指定格式创建默认的日期，时间和日期时间格式
					dateStyle = book.createCellStyle();
					dateStyle.cloneStyleFrom(style);
					dateTimeStyle = book.createCellStyle();
					dateTimeStyle.cloneStyleFrom(style);
					format = toExcelDateFormat(defaultDateFormat, true);
					dateStyle.setDataFormat(book.createDataFormat().getFormat(format));
					dateTimeStyles[k][0] = dateStyle;
					format = toExcelDateFormat(defaultDateFormat + " " + defaultTimeFormat, true);
					dateTimeStyle.setDataFormat(book.createDataFormat().getFormat(format));
					dateTimeStyles[k][1] = dateTimeStyle;
					style = dateStyle;// 使空单无格默认为日期格式
				} else {
					dateTimeStyles[0][0] = null;
					format = toExcelDateFormat(format, false);
					if (format == null) {
						format = toExcelDateFormat(defaultDateFormat, true);
					}
					style.setDataFormat(book.createDataFormat().getFormat(format));
				}
			} else if (dataTypeStr.startsWith("bool")) {// bool&boolean
				dataType = 4;
			} else {
				dataType = 5;// auto
			}
			dataTypes[k] = dataType;
			colStyles[k] = style;
		}
		for (i = 0; i < j; i++) {
			row = sheet.createRow(startRow + i);
			row.setHeight(rowHeight);
			record = records.getJSONObject(i);
			for (k = 0; k < l; k++) {
				cell = row.createCell(k + startCol);
				cell.setCellStyle(colStyles[k]);
				value = JsonUtils.opt(record, fieldNames[k]);
				if (value == null) {
					continue;
				}
				if (keyMaps[k] != null) {
					// 键值转换
					// value = KVBuffer.getValue((ConcurrentHashMap<?, ?>) keyMaps[k], request, value);
				}
				if (dataTypes[k] == 5) {
					// 对自动类型进行归类
					if (value instanceof Number) {
						dataTypes[k] = 2;
					} else if (value instanceof Date) {
						dataTypes[k] = 3;
					} else if (value instanceof Boolean) {
						dataTypes[k] = 4;
					} else {
						dataTypes[k] = 1;
					}
				}
				switch (dataTypes[k]) {
				case 2:// number
					if (value instanceof Number) {
						number = ((Number) value).doubleValue();
					} else {
						number = Double.parseDouble(value.toString());
					}
					if (isRate[k]) {
						number = number / 100;
					}
					cell.setCellValue(number);
					break;
				case 3:// date
					if (dateTimeStyles[k][0] == null) {
						// 使用指定的格式
						if (value instanceof Date) {
							date = (Date) value;
						} else {
							date = Timestamp.valueOf(value.toString());
						}
					} else {
						// 根据值判断使用的默认日期时间格式，并覆盖样式
						if (value instanceof Date) {
							date = (Date) value;
							hasTime = !dateToStr(date).endsWith("00:00:00.0");
						} else {
							dateTimeStr = value.toString();
							date = Timestamp.valueOf(dateTimeStr);
							// 字符串需要判断以下3种情景：
							// 无小数，Java以".0"结尾，JS以".000"结尾
							hasTime = !(dateTimeStr.endsWith("00:00:00.0") || dateTimeStr.endsWith("00:00:00") || dateTimeStr.endsWith("00:00:00.000"));
						}
						if (hasTime) {
							cell.setCellStyle(dateTimeStyles[k][1]);
						} else {
							cell.setCellStyle(dateTimeStyles[k][0]);
						}
					}
					cell.setCellValue(date);
					break;
				case 4:// bool
					if (useBoolString) {
						cell.setCellValue(StrUtils.getBool(value.toString()) ? trueText : falseText);
					} else {
						cell.setCellValue(StrUtils.getBool(value.toString()));
					}
					break;
				default:// string
					cell.setCellValue(value.toString());
					break;
				}
			}
		}
	}
	
	/**
	 * 根据变量的设置获取单元格样式对象。
	 * @param book 工作簿。
	 * @param type 类型。titile标题，header列标题，text正文。
	 * @return 样式对象数组：0项样式，1项行高度。
	 */
	public static Object[] createCellStyle(Workbook book, String type, JSONObject config) {
		CellStyle style = book.createCellStyle();
		Font font = book.createFont();
		String fontName = JsonUtils.getString(config, "fontName");
		int fontHeight = JsonUtils.getInt(config, "fontHeight", 12);
		double rowHeight = JsonUtils.getDouble(config, "rowHeight", 20);
		
		Object result[] = new Object[2];

		if (!fontName.isEmpty()) {
			font.setFontName(fontName);
		}
		font.setBoldweight((short) JsonUtils.getInt(config, "fontWeight", 600));
		font.setFontHeight((short) fontHeight);
		if (rowHeight < 10) {
			rowHeight = rowHeight * fontHeight;// 小于10定义为倍数
		}
		if (!"text".equals(type)) {
			if (config.containsKey("wrapText") && config.getBooleanValue("wrapText")) {
				style.setWrapText(true);
			}
		}
		if ("title".equals(type)) {
			String align = JsonUtils.getString(config, "align", "居中");
			if (!align.isEmpty()) {
				Object[][] alignments = {
						{ "居中",    CellStyle.ALIGN_CENTER },
						{ "左",      CellStyle.ALIGN_LEFT },
						{ "右",      CellStyle.ALIGN_RIGHT },
						{ "居中选择", CellStyle.ALIGN_CENTER_SELECTION },
						{ "填充",    CellStyle.ALIGN_FILL },
						{ "常规",    CellStyle.ALIGN_GENERAL },
						{ "两端对齐", CellStyle.ALIGN_JUSTIFY }
					};
				
				style.setAlignment((Short) StrUtils.getValue(alignments, align));
			}
		} else if (config.containsKey("border") && config.getBooleanValue("border")) {
			style.setBorderTop(CellStyle.BORDER_THIN);
			style.setBorderBottom(CellStyle.BORDER_THIN);
			style.setBorderLeft(CellStyle.BORDER_THIN);
			style.setBorderRight(CellStyle.BORDER_THIN);
		}
		if ("header".equals(type)) {
			String backColor = JsonUtils.getString(config, "backColor", "默认");
			if (!"默认".equals(backColor)) {
				// HSSFColor兼容XSSF
				Object[][] colors = {
						{ "默认", -1 },
						{ "金色", HSSFColor.GOLD.index },
						{ "灰色", HSSFColor.GREY_25_PERCENT.index },
						{ "浅黄", HSSFColor.LIGHT_YELLOW.index }
					};
				style.setFillForegroundColor((Short) StrUtils.getValue(colors, backColor));
				style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			}
		}
		
		// 背景颜色
		if (config.containsKey("bgColor")) {
			String bgColor = JsonUtils.getString(config, "bgColor", "默认");
			if (!"默认".equals(bgColor)) {
				// HSSFColor兼容XSSF
				Object[][] colors = {
						{ "默认", -1 },
						{ "金色", HSSFColor.GOLD.index },
						{ "灰色", HSSFColor.GREY_25_PERCENT.index },
						{ "浅黄", HSSFColor.LIGHT_YELLOW.index }
					};
				Short nBgColor = (Short) StrUtils.getValue(colors, bgColor);
				if (nBgColor == null) {
					nBgColor = Short.parseShort(bgColor);
				}
				style.setFillForegroundColor(nBgColor);
				style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			}
		}
		
		style.setVerticalAlignment((short) 1);// HSSFCellStyle.VERTICAL_CENTER
		style.setFont(font);
		result[0] = style;
		result[1] = ((Double) rowHeight).shortValue();
		return result;
	}
	
	public static int getFieldType(JSONObject jo) {
		String types[] = { "auto", "string", "int", "float", "boolean", "date" };
		return StrUtils.indexOf(types, jo.getString("type"));
	}
	
	/**
	 * 单元格对齐方式转换
	 * 
	 * @param align
	 * @return
	 */
	public static short getAlignment(String align) {
		return getAlignment(align, CellStyle.ALIGN_LEFT);
	}
	
	/**
	 * 根据对齐方法字符串返回Excel对齐值。
	 * @param align 对齐字符串。
	 * @return 对齐值。
	 */
	public static short getAlignment(String align, short defaultAlign) {
		if ("right".equals(align)) {
			return CellStyle.ALIGN_RIGHT;
		} else if ("center".equals("align")) {
			return CellStyle.ALIGN_CENTER;
		} else if ("left".equals("align")) {
			return CellStyle.ALIGN_LEFT;
		} else {
			return defaultAlign;
		}
	}
	
	/**
	 * 获取前端日期时间格式对应到Excel的日期时间格式。
	 * @param format 日期时间格式。
	 * @param returnDefault 如果格式不支持是否返回默认格式，true默认格式，false返回null。
	 * @return 转换后的Excel日期时间格式。
	 */
	public static String toExcelDateFormat(String format, boolean returnDefault) {
		String[] unSupportFormats = { "N", "S", "w", "z", "W", "t", "L", "o", "u", "O", "P", "T", "Z", "c", "U", "MS", "time", "timestamp" };
		String[][] supportFormats = { 
				{ "d", "dd" }, 
				{ "D", "aaa" },
				{ "j", "d" }, 
				{ "l", "aaaa" }, 
				{ "F", "mmmm" }, 
				{ "m", "mm" },
				{ "M", "mmm" }, 
				{ "n", "m" }, 
				{ "Y", "yyyy" }, 
				{ "y", "yy" },
				{ "a", "am/pm" }, 
				{ "A", "AM/PM" }, 
				{ "g", "h" },
				{ "G", "hh" }, 
				{ "h", "h" }, 
				{ "H", "hh" }, 
				{ "i", "mm" },
				{ "s", "ss" } 
		};

		for (String s : unSupportFormats) {
			if (format.indexOf(s) != -1) {
				return returnDefault ? "yyyy-mm-dd" : null;
			}
		}
		for (String[] s : supportFormats) {
			format = StrUtils.replaceAll(format, s[0], s[1]);
		}
		return format;
	}
	
	/**
	 * 获取单元格内容
	 * 
	 * @param cell
	 * @return
	 */
	static DecimalFormat df = new DecimalFormat("0");
	/**
	 * 获取单元格的值。
	 * @param cell 单元格。
	 * @return 单元格值。如果为空返回null。
	 */
	public static Object getCellValue(Cell cell) {
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_FORMULA:
		case Cell.CELL_TYPE_NUMERIC:
			try {
				if (org.apache.poi.ss.usermodel.DateUtil.isCellDateFormatted(cell)) {
					return cell.getDateCellValue();
				} else {
					return cell.getNumericCellValue();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		case Cell.CELL_TYPE_STRING:
			return cell.getStringCellValue();
		case Cell.CELL_TYPE_BOOLEAN:
			return cell.getBooleanCellValue();
		default:
			return null;
		}
	}
	
	public static String getFileExt(String fileName) {
		if (fileName != null) {
			int i = fileName.lastIndexOf('.');
			if (i != -1)
				return fileName.substring(i + 1);
		}
		return "";
	}
	
	public static String dateToStr(Date value) {
		if (value == null)
			return null;
		Timestamp t = new Timestamp(value.getTime());
		return t.toString();
	}
	
	/*
	public static Object getCellValue(Cell cell) {
		switch (cell.getCellType()) {
		case Cell.CELL_TYPE_FORMULA:
		case Cell.CELL_TYPE_NUMERIC:
			try {
				double numb = cell.getNumericCellValue();
				if (numb % 1 > 0) {
					String temp = String.valueOf(numb);
					if (temp.indexOf('E') > 0) {
						return Double.parseDouble(temp);
					} else {
						return numb;
					}
				} else {
					return Long.parseLong(df.format(numb));
				}
			} catch (Exception e) {
				return cell.getStringCellValue();
			}
		case Cell.CELL_TYPE_STRING:
			return cell.getStringCellValue();
		case Cell.CELL_TYPE_BOOLEAN:
			try {
				return cell.getBooleanCellValue();
			} catch (Exception e) {
				return cell.getStringCellValue();
			}
		default:
			return cell.getStringCellValue();
		}
	}*/
}
