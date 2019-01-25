package com.sdk4.common.util;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

public class StrUtils {
	/** 默认时间格式 **/
	public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	/** Date默认时区 **/
	public static final String DATE_TIMEZONE = "GMT+8";
	/** UTF-8字符集 **/
	public static final String CHARSET_UTF8 = "UTF-8";
	/** GBK字符集 **/
	public static final String CHARSET_GBK = "GBK";
	public static final TimeZone TZ_GMT8 = TimeZone.getTimeZone(DATE_TIMEZONE);
	
	private static final String QUOT = "&quot;";
	private static final String AMP = "&amp;";
	private static final String APOS = "&apos;";
	private static final String GT = "&gt;";
	private static final String LT = "&lt;";
	
	/** 正则表达式：验证手机号 */
    private static final String REGEX_MOBILE = "^(1)\\d{10}$";
	
   /**
     * 默认缓冲区大小。
     */
    public static final int bufferSize = 4096;
	
	/**
	 * 对字符串按指定分隔符进行分割，并返回分隔后字符串组成的数组。
	 * 如果分隔符separator是单个字符，请使用更高效的split(String, char)方法。
	 * 该方法不支持正则表达式，因此较String.split方法具有更高的性能。
	 * @param string 需要分隔的字符串。
	 * @param separator 分隔符。
	 * @return 分隔字符串组成的数组。
	 * @see com.StrUtils.util.StringUtil#split(String, char)
	 * @see java.lang.String#split(String)
	 */
	public static String[] split(String string, String separator) {
		return split(string, separator, false);
	}

	/**
	 * 对字符串按指定分隔符进行分割，并返回分隔后字符串组成的数组。
	 * 如果分隔符separator是单个字符，请使用更高效的split(String, char, boolean)方法。
	 * 该方法允许对每个分隔的字符串执行trim操作。
	 * 该方法不支持正则表达式，因此较String.split方法具有更高的性能。
	 * @param string 需要分隔的字符串。
	 * @param separator 分隔符。
	 * @param trim 是否对每个分隔的字符串执行trim操作。
	 * @return 分隔字符串组成的数组。
	 * @see com.StrUtils.util.StringUtil#split(String, char, boolean)
	 * @see java.lang.String#split(String)
	 */
	public static String[] split(String string, String separator, boolean trim) {
		int pos = 0, oldPos = 0, index = 0, separatorLen = separator.length();
		ArrayList<Integer> posData = new ArrayList<Integer>();
		if (string == null)
			string = "";
		while ((pos = string.indexOf(separator, pos)) != -1) {
			posData.add(pos);
			pos += separatorLen;
		}
		posData.add(string.length());
		String[] result = new String[posData.size()];
		for (int p : posData) {
			if (trim)
				result[index] = string.substring(oldPos, p).trim();
			else
				result[index] = string.substring(oldPos, p);
			oldPos = p + separatorLen;
			index++;
		}
		return result;
	}

	/**
	 * 对字符串按指定分隔符进行分割，并返回分隔后字符串组成的数组。
	 * 该方法不支持正则表达式，因此较String.split方法具有更高的性能。
	 * @param string 需要分隔的字符串。
	 * @param separator 分隔符。
	 * @return 分隔字符串组成的数组。
	 * @see com.StrUtils.util.StringUtil#split(String, String)
	 * @see java.lang.String#split(String)
	 */
	public static String[] split(String string, char separator) {
		return split(string, separator, false);
	}

	/**
	 * 对字符串按指定分隔符进行分割，并返回分隔后字符串组成的数组。
	 * 该方法允许对每个分隔的字符串执行trim操作。
	 * 该方法不支持正则表达式，因此较String.split方法具有更高的性能。
	 * @param string 需要分隔的字符串。
	 * @param separator 分隔符。
	 * @param trim 是否对每个分隔的字符串执行trim操作。
	 * @return 分隔字符串组成的数组。
	 * @see com.StrUtils.util.StringUtil#split(String, String, boolean)
	 * @see java.lang.String#split(String)
	 */
	public static String[] split(String string, char separator, boolean trim) {
		int pos = 0, oldPos = 0, index = 0;
		ArrayList<Integer> posData = new ArrayList<Integer>();
		if (string == null)
			string = "";
		while ((pos = string.indexOf(separator, pos)) != -1) {
			posData.add(pos);
			pos++;
		}
		posData.add(string.length());
		String[] result = new String[posData.size()];
		for (int p : posData) {
			if (trim)
				result[index] = string.substring(oldPos, p).trim();
			else
				result[index] = string.substring(oldPos, p);
			oldPos = p + 1;
			index++;
		}
		return result;
	}
	
	/**
	 * 查找目标字符在源字符串中出现的次数。
	 * @param source 源字符串。
	 * @param dest 目标字符。
	 * @return 源字符出现的次数。
	 */
	public static int stringOccur(String source, char dest) {
		return stringOccur(source, dest, 0, source.length())[0];
	}

	/**
	 * 查找目标字符在源字符串中出现的次数。
	 * @param source 源字符串。
	 * @param dest 目标字符。
	 * @param startIndex 查找的开始位置。
	 * @param endIndex 查找的结束位置。
	 * @return 查找结果数组，0项出现个数，1项目标字符在源字符串最后一次出现的位置末尾。
	 */
	public static int[] stringOccur(String source, char dest, int startIndex,
			int endIndex) {
		int result[] = new int[2], newPos, pos = startIndex, count = 0;

		while ((newPos = source.indexOf(dest, pos)) != -1) {
			if (newPos > endIndex)
				break;
			pos = newPos + 1;
			count++;
		}
		result[0] = count;
		result[1] = count == 0 ? (source.lastIndexOf(dest, endIndex) + 1) : pos;
		return result;
	}
	
	/**
	 * 判断指定字符串是否是一个合法的数字。
	 * @param string 需要判断的字符串。
	 * @param decimal 是否允许小数格式。
	 * @return true是一个合法的数字，false不是。
	 */
	public static boolean isNumeric(String string, boolean decimal) {
		int i, j;
		String ts;
		char ch;

		ts = string.trim();
		if (decimal && stringOccur(string, '.') > 1)
			return false;
		if (ts.startsWith("-"))
			ts = ts.substring(1);
		j = ts.length();
		if (j == 0)
			return false;
		for (i = 0; i < j; i++) {
			ch = ts.charAt(i);
			if (!(ch >= '0' && ch <= '9') && (!decimal || ch != '.'))
				return false;
		}
		return true;
	}
	
	public static boolean isSame(String string1, String string2) {
		String s1, s2;

		if (string1 == null)
			s1 = "";
		else
			s1 = string1;
		if (string2 == null)
			s2 = "";
		else
			s2 = string2;
		return s1.equalsIgnoreCase(s2);
	}

	public static boolean isEqual(String string1, String string2) {
		String s1, s2;

		if (string1 == null)
			s1 = "";
		else
			s1 = string1;
		if (string2 == null)
			s2 = "";
		else
			s2 = string2;
		return s1.equals(s2);
	}
	
	/**
	 * 检查指定的字符串是否为空。
	 * <ul>
	 * <li>SysUtils.isEmpty(null) = true</li>
	 * <li>SysUtils.isEmpty("") = true</li>
	 * <li>SysUtils.isEmpty("   ") = true</li>
	 * <li>SysUtils.isEmpty("abc") = false</li>
	 * </ul>
	 * 
	 * @param value 待检查的字符串
	 * @return true/false
	 */
	public static boolean isEmpty(String value) {
		int strLen;
		if (value == null || (strLen = value.length()) == 0) {
			return true;
		}
		for (int i = 0; i < strLen; i++) {
			if ((Character.isWhitespace(value.charAt(i)) == false)) {
				return false;
			}
		}
		return true;
	}
	
	/**
	 * 检查对象是否为数字型字符串,包含负数开头的。
	 */
	public static boolean isNumeric(Object obj) {
		if (obj == null) {
			return false;
		}
		char[] chars = obj.toString().toCharArray();
		int length = chars.length;
		if(length < 1)
			return false;
		
		int i = 0;
		if(length > 1 && chars[0] == '-')
			i = 1;
		
		for (; i < length; i++) {
			if (!Character.isDigit(chars[i])) {
				return false;
			}
		}
		return true;
	}
	
	public static boolean isMobile(String mobile) {
	    return isEmpty(mobile) ? false : Pattern.matches(REGEX_MOBILE, mobile);
	}
	
	/**
	 * 检查指定的字符串列表是否不为空。
	 */
	public static boolean areNotEmpty(String... values) {
		boolean result = true;
		if (values == null || values.length == 0) {
			result = false;
		} else {
			for (String value : values) {
				result &= !isEmpty(value);
			}
		}
		return result;
	}
	/**
     * 把以字符串形式表达的布尔值转换为对应的数字。字符串比较将忽略大小写。
     * @param value 以字符串表达的布尔值。
     * @return 布尔值对应的数字，"true"转换为"1", "false"转换为"0"，其他值保持不变。
     */
    public static String convertBool(String value) {
        if ("true".equalsIgnoreCase(value))
            return "1";
        else if ("false".equalsIgnoreCase(value))
            return "0";
        else
            return value;
    }
    
	/**
	 * 把通用字符编码的字符串转化为汉字编码。
	 */
	public static String unicodeToChinese(String unicode) {
		StringBuilder out = new StringBuilder();
		if (!isEmpty(unicode)) {
			for (int i = 0; i < unicode.length(); i++) {
				out.append(unicode.charAt(i));
			}
		}
		return out.toString();
	}
	
	/**
	 * 验证name的合法性。name必须由字母，数字和下划线组成，其中首字符不能是数字。
	 * 否则方法将抛出异常。
	 * @param {String} name 需要被验证的字符串对象。
	 */
	public static void checkName(String name) {
		int i, j = name.length();
		char c;
		for (i = 0; i < j; i++) {
			c = name.charAt(i);
			if (!(c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c == '_' || i > 0
					&& (c >= '0' && c <= '9')))
				throw new IllegalArgumentException("Invalid name \"" + name
						+ "\".");
		}
	}
	
	public static String concat(String s, String... more) {
		StringBuilder buf = new StringBuilder(s);
		for (String t : more) {
			buf.append(t);
		}
		return buf.toString();
	}
	
	public static String quote(String string) {
		if (string == null)
			return "\"\"";
		int len = string.length();
		if (len == 0)
			return "\"\"";
		char b, c = 0;
		int i;
		String t;
		StringBuilder sb = new StringBuilder(len + 4);

		sb.append('"');
		for (i = 0; i < len; i++) {
			b = c;
			c = string.charAt(i);
			switch (c) {
			case '\\':
			case '"':
				sb.append('\\');
				sb.append(c);
				break;
			case '/':
				if (b == '<')
					sb.append('\\');
				sb.append(c);
				break;
			case '\b':
				sb.append("\\b");
				break;
			case '\t':
				sb.append("\\t");
				break;
			case '\n':
				sb.append("\\n");
				break;
			case '\f':
				sb.append("\\f");
				break;
			case '\r':
				sb.append("\\r");
				break;
			case 0x0b:
				sb.append("\\u000b");
				break;
			default:
				if (c < ' ' || (c >= '\u0080' && c < '\u00a0')
						|| (c >= '\u2000' && c < '\u2100')) {
					t = "000" + Integer.toHexString(c);
					sb.append("\\u");
					sb.append(t.substring(t.length() - 4));
				} else
					sb.append(c);
			}
		}
		sb.append('"');
		return sb.toString();
	}
	
	public static String repeat(String src, int num, String separator) {
        StringBuffer str = new StringBuffer();
        separator = separator == null ? "" : separator.trim();
        for (int i = 0; i < num; i++) {
            if (i > 0) {
                str.append(separator);
            }
            str.append(src);
        }
        return str.toString();
    }
	
	public static int indexOf(String list[], String string) {
		int i, j;

		if (list == null) {
			return -1;
		}
		j = list.length;
		for (i = 0; i < j; i++) {
			if (list[i].equals(string)) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * 全部替换字符串中指定子串为另一字符串。该方法不支持正则表达式，
	 *因此较String.replace具有更高效率。
	 * @param string 需要替换的文本。
	 * @param oldString 替换的源字符串。
	 * @param newString 替换的目标字符串。
	 * @return 替换后的文本。
	 * @see java.lang.String#replaceAll(String, String)
	 */
	public static String replaceAll(String string, String oldString,
			String newString) {
		return innerReplace(string, oldString, newString, true);
	}

	/**
	 * 替换字符串中首个指定子串为另一字符串。该方法不支持正则表达式，
	 *因此较String.replace具有更高效率。
	 * @param string 需要替换的文本。
	 * @param oldString 替换的源字符串。
	 * @param newString 替换的目标字符串。
	 * @return 替换后的文本。
	 * @see java.lang.String#replaceFirst(String, String)
	 */
	public static String replaceFirst(String string, String oldString,
			String newString) {
		return innerReplace(string, oldString, newString, false);
	}

	/**
	 * 替换字符串中指定子串为另一字符串。该方法不支持正则表达式，
	 *因此较String.replace方法具有更高效率。
	 * @param string 需要替换的文本。
	 * @param oldString 替换的源字符串。
	 * @param newString 替换的目标字符串。
	 * @param isAll 是否替换全部出现的字符串，true替换全部，false替换首个。
	 * @return 替换后的文本。
	 */
	private static String innerReplace(String string, String oldString,
			String newString, boolean isAll) {
		int index = string.indexOf(oldString);
		if (index == -1)
			return string;
		int start = 0, len = oldString.length();
		if (len == 0)
			return string;
		StringBuilder buffer = new StringBuilder(string.length());
		do {
			buffer.append(string.substring(start, index));
			buffer.append(newString);
			start = index + len;
			if (!isAll)
				break;
			index = string.indexOf(oldString, start);
		} while (index != -1);
		buffer.append(string.substring(start));
		return buffer.toString();
	}
	
	/**
	 * 读取指定输入流数据并转换为使用utf-8编码的字符串。
	 * @param stream 输入流。
	 * @return 读取的字符串。
	 * @throws IOException 读取过程发生异常。
	 */
	public static String getString(InputStream stream) throws IOException {
		return getString(stream, "utf-8");
	}

	/**
	 * 读取指定输入流数据并转换为字符串。完成后将关闭输入流stream。
	 * @param stream 输入流。
	 * @param charset 使用的字符编码。
	 * @return 读取的字符串。
	 * @throws IOException 读取过程发生异常。
	 */
	public static String getString(InputStream stream, String charset)
			throws IOException {
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			IOUtils.copy(stream, os);
			if (StrUtils.isEmpty(charset))
				return new String(os.toByteArray());
			else
				return new String(os.toByteArray(), charset);
		} finally {
			stream.close();
		}
	}
	
	/**
	 * 获取字符串中“=”前面部分的字符串。如果没找到“=”将返回整个字符串。
	 * @param string 字符串。
	 * @return 名称部分字符串。
	 */
	public static String getNamePart(String string) {
		if (string == null)
			return "";
		int index = string.indexOf('=');

		if (index == -1)
			return string;
		else
			return string.substring(0, index);
	}

	/**
	 * 获取字符串中“=”后面部分的字符串。如果没找到“=”将返回空串。
	 * @param string 字符串。
	 * @return 值部分字符串。
	 */
	public static String getValuePart(String string) {
		if (string == null)
			return "";
		int index = string.indexOf('=');

		if (index == -1)
			return "";
		else
			return string.substring(index + 1);
	}
	
	/**
     * 从Reader对象读取字符串。读取完成后关闭reader。
     * @param reader Reader对象。
     * @return 读取的字符串。
     * @throws IOException 读取过程发生异常。
     */
    public static String readString(Reader reader) throws IOException {
        try {
            char buf[] = new char[bufferSize];
            StringBuilder sb = new StringBuilder();
            int len;

            while ((len = reader.read(buf)) > 0) {
                sb.append(buf, 0, len);
            }
            return sb.toString();
        } finally {
            reader.close();
        }
    }
	
	/**
	 * 把指定字符串的文本转换成单行的字符串。文本中的回车、换行和tab都将被替换为空格。
	 * @param string 需要转换的文本。
	 * @return 转换后的单行字符串。
	 */
	public static String toLine(String string) {
		int i, len = string.length();
		if (len == 0)
			return "";
		StringBuilder buffer = new StringBuilder();
		char c;
		for (i = 0; i < len; i++) {
			c = string.charAt(i);
			switch (c) {
			case '\n':
			case '\r':
			case '\t':
				buffer.append(' ');
				break;
			default:
				buffer.append(c);
			}
		}
		return buffer.toString();
	}
	
	/**
	 * 如果指定字符串为null则返回空串，否则返回字符串本身。
	 * @param string 字符串。
	 * @return 获得的字符串。
	 */
	public static String opt(String string) {
		if (string == null)
			return "";
		else
			return string;
	}
	
	/**
	 * 获取字符串的省略文本，如果字符串长过超过指定长度，
	 *将使用“...”省略显示，否则直接返回原字符串。
	 * @param string 需要省略显示的字符串。
	 * @param length 最大显示长度。
	 * @return 省略后的字符串。
	 */
	public static String ellipsis(String string, int length) {
		if (string.length() > length)
			return string.substring(0, length - 3) + "...";
		return string;
	}
	
	public static String ellipsis(String str, String charset, int byteLength, String suffix) {
	    try {
    	    int len = str.getBytes(charset).length;
    	    if (len > byteLength) {
    	        StringBuilder tmp = new StringBuilder();
    	        for (char c : str.toCharArray()) {
    	            byteLength = byteLength - String.valueOf(c).getBytes(charset).length;
    	            tmp.append(c);
    	            if (byteLength <= 0) {
    	                break;
    	            }
    	        }
    	        tmp.append(suffix);
    	        return tmp.toString();
    	    }
	    } catch (Exception e) {
	    }
	    return str;
    }
	
	/**
	 * 使用html textarea标记引用指定文本。
	 * @param text 需要引用的文本。
	 * @return 引用后的文本。
	 */
	public static String textareaQuote(String text) {
		return StrUtils.concat("<textarea>", text, "</textarea>");
	}
	
	private static Pattern pattern_more_spaces = Pattern.compile("\\s+");
	
	/**
	 * 将字符串中的多个空格替换成一个并去掉首尾空格
	 * 
	 * @param str
	 * @return
	 */
	public static String moreSpacesToOne(String str) {
		if (str == null) {
			return "";
		}
		str = str.trim();
		Matcher matcher = pattern_more_spaces.matcher(str);
		return matcher.replaceAll(" ");
	}
	
	private static NumberFormat nf_money = new DecimalFormat(",###.##");
	
	public static String formatMoney(long n, boolean forcePoint) {
		String result = "";
		double money = n * 0.01;
		result = nf_money.format(money);
		if (money % 1 == 0 && forcePoint && result.indexOf('.') < 0) {
			result += ".00";
		}
		return result;
	}
	
	public static String formatMoney(long n) {
		return formatMoney(n, false);
	}
	
	/**
	 * 连接数组中的每个字符串，并以指定分隔符分隔。如果子项为空，则排除该项。
	 * @param strings 需要连接的字符串数组。
	 * @param spliter 分隔字符串。
	 * @return 连接后的字符串。
	 */
	public static String join(String[] strings, char splitter) {
		StringBuilder buf = new StringBuilder();
		boolean added = false;

		for (String s : strings) {
			if (StrUtils.isEmpty(s))
				continue;
			if (added)
				buf.append(splitter);
			else
				added = true;
			buf.append(s);
		}
		return buf.toString();
	}

	/**
	 * 连接列表中的每个对象，并以指定分隔符分隔。如果子项为空，则排除该项。
	 * @param strings 需要连接的列表。
	 * @param spliter 分隔字符串。
	 * @return 连接后的字符串。
	 */
	public static String join(List<String> list, char splitter) {
		StringBuilder buf = new StringBuilder();
		boolean added = false;

		for (String item : list) {
			if (StrUtils.isEmpty(item))
				continue;
			if (added)
				buf.append(splitter);
			else
				added = true;
			buf.append(item);
		}
		return buf.toString();
	}
	
	/**
	 * 如果指定字符串为null或空串则返回null，否则返回字符串本身。
	 * @param string 字符串。
	 * @return 获得的字符串或null。
	 */
	public static String force(String string) {
		if (isEmpty(string))
			return null;
		else
			return string;
	}
	
	/**
	 * 返回第1个非空字符串，如果都为空则返回空串。
	 * @param string 字符串列表。
	 * @return 第1个非空字符串或空串。
	 */
	public static String select(String... string) {
		for (String s : string)
			if (!isEmpty(s))
				return s;
		return "";
	}
	
	/**
	 * 判断指定字符串值逻辑是否为真，'false'(不区分大小写)，'0'，''，null返回false其他返回true。
	 * @param value 判断的字符串值。
	 * @return 布尔值。
	 */
	public static boolean getBool(String value) {
		if (value == null || value.equalsIgnoreCase("false")
				|| value.equals("0") || value.isEmpty())
			return false;
		else
			return true;
	}
	
	/**
	 * 判断指定字符串值逻辑是否为真，value为null返回null，value为'false'(不区分大小写)，'0'，
	 * ''，null返回false其他返回true。
	 * @param value 判断的字符串值。
	 * @return 布尔值。
	 */
	public static Boolean getBoolA(String value) {
		if (value == null)
			return null;
		return getBool(value);
	}
	
	public static boolean getBool(Object object) {
		return getBool(String.valueOf(object));
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getValue(Object[][] data, String key) {
		for (Object[] item : data) {
			if (key.equals(item[0])) {
				return (T) item[1];
			}
		}
		return null;
	}
	
	private static Pattern pattern_format_if = Pattern.compile("(< *if *\\( *[a-zA-Z0-9_]* *\\) *\\>)");
	private static Pattern pattern_format_var = Pattern.compile("(< *[a-zA-Z0-9_]* *\\>)");
	
	/**
	 * 把名称转换为首字母小写的驼峰形式。
	 */
	public static String toCamelStyle(String name) {
		StringBuilder newName = new StringBuilder();
		int len = name.length();
		for (int i = 0; i < len; i++) {
			char c = name.charAt(i);
			if (i == 0) {
				newName.append(Character.toLowerCase(c));
			} else {
				newName.append(c);
			}
		}
		return newName.toString();
	}
	
	/**
	 * 把字符串解释为日期对象，采用yyyy-MM-dd HH:mm:ss的格式。
	 */
	public static Date parseDateTime(String str) {
		DateFormat format = new SimpleDateFormat(DATE_TIME_FORMAT);
		format.setTimeZone(TZ_GMT8);
		try {
			return format.parse(str);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 对日期进行字符串格式化，采用yyyy-MM-dd HH:mm:ss的格式。
	 */
	public static String formatDateTime(Date date) {
		DateFormat format = new SimpleDateFormat(DATE_TIME_FORMAT);
		format.setTimeZone(TZ_GMT8);
		return format.format(date);
	}
	
	/**
	 * 对日期进行字符串格式化，采用指定的格式。
	 */
	public static String formatDateTime(Date date, String pattern) {
		DateFormat format = new SimpleDateFormat(pattern);
		format.setTimeZone(TZ_GMT8);
		return format.format(date);
	}
	
	/**
	 * XML字符转义包括(<,>,',&,")五个字符.
	 * 
	 * @param value 所需转义的字符串
	 * 
	 * @return 转义后的字符串 @
	 */
	public static String escapeXml(String value) {
		StringBuilder writer = new StringBuilder();
		char[] chars = value.trim().toCharArray();
		for (int i = 0; i < chars.length; i++) {
			char c = chars[i];
			switch (c) {
			case '<':
				writer.append(LT);
				break;
			case '>':
				writer.append(GT);
				break;
			case '\'':
				writer.append(APOS);
				break;
			case '&':
				writer.append(AMP);
				break;
			case '\"':
				writer.append(QUOT);
				break;
			default:
				if ((c == 0x9) || (c == 0xA) || (c == 0xD) || ((c >= 0x20) && (c <= 0xD7FF))
						|| ((c >= 0xE000) && (c <= 0xFFFD)) || ((c >= 0x10000) && (c <= 0x10FFFF)))
					writer.append(c);
			}
		}
		return writer.toString();
	}

	/**
	 * 获取类的get/set属性名称集合。
	 * 
	 * @param clazz 类
	 * @param isGet 是否获取读方法，true为读方法，false为写方法
	 * @return 属性名称集合
	 */
	public static Set<String> getClassProperties(Class<?> clazz, boolean isGet) {
		Set<String> propNames = new HashSet<String>();
		try {
			BeanInfo info = Introspector.getBeanInfo(clazz);
			PropertyDescriptor[] props = info.getPropertyDescriptors();
			for (PropertyDescriptor prop : props) {
				String name = prop.getName();
				Method method;
				if (isGet) {
					method = prop.getReadMethod();
				} else {
					method = prop.getWriteMethod();
				}
				if (!"class".equals(name) && method != null) {
					propNames.add(name);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return propNames;
	}
	
	public static String newId() {
		return UniqId.getInstance().getUniqIDHashString();
	}
	
	/**
     * 把名称转换为小写加下划线的形式。
     */
    public static String toUnderlineStyle(String name) {
        StringBuilder newName = new StringBuilder();
        int len = name.length();
        for (int i = 0; i < len; i++) {
            char c = name.charAt(i);
            if (Character.isUpperCase(c)) {
                if (i > 0) {
                    newName.append("_");
                }
                newName.append(Character.toLowerCase(c));
            } else {
                newName.append(c);
            }
        }
        return newName.toString();
    }
    
	public static String encodeURL(String str, String charset) throws UnsupportedEncodingException {
		String result = URLEncoder.encode(str, charset);
		result = result.replace("+", "%20").replace("*", "%2A");
		return result;
	}
	
	public static String encodeURL(String str) throws UnsupportedEncodingException {
		return encodeURL(str, Charset.defaultCharset().toString());
	}
	
	public static String decodeURL(String str, String charset) throws UnsupportedEncodingException {
		String result = URLDecoder.decode(str, charset);
		return result;
	}
	
	public static String decodeURL(String str) throws UnsupportedEncodingException {
		return decodeURL(str, Charset.defaultCharset().toString());
	}
}
