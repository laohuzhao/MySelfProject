package com.sdk4.common.util;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * 
 * 
 * @author CnJun
 */
public class JsonUtils {
    
    static {
        // 配置使用 java.beans.Transient 注解的字段，在 toJSONString 时不要输出
        // - 全局配置
        // - 也可在调用 JSON.toJSONString 方法时增加 SerializerFeature.SkipTransientField 参数
        JSON.DEFAULT_GENERATE_FEATURE = SerializerFeature.config(JSON.DEFAULT_GENERATE_FEATURE, SerializerFeature.SkipTransientField, true);
    }
	
	public static String toJSONString(Object object, String dateFormat, boolean prettyFormat) {
		String result = "";
		
		SerializerFeature[] sf = null;
		
		if (dateFormat == null) {
		    if (prettyFormat) {
                sf = new SerializerFeature[] {
                    SerializerFeature.PrettyFormat
                };
            } else {
                sf = new SerializerFeature[] {
                };
            }
			result = JSON.toJSONString(object, sf);
		} else {
			if (prettyFormat) {
			    sf = new SerializerFeature[] {
		            SerializerFeature.WriteDateUseDateFormat, 
		            
		            SerializerFeature.PrettyFormat, 
		            SerializerFeature.SortField
			    };
			} else {
                sf = new SerializerFeature[] {
                    SerializerFeature.WriteDateUseDateFormat
                };
			}
            result = JSON.toJSONStringWithDateFormat(object, dateFormat, sf);
		}
		
		return result;
	}
	
	public static String toJSONString(Object object, boolean prettyFormat) {
		return toJSONString(object, null, prettyFormat);
	}
	
	public static String toJSONString(Object object, String dateFormat) {
		return toJSONString(object, dateFormat, false);
	}
	
	public static String toJSONString(Object object) {
		return toJSONString(object, null, false);
	}
	
	public static JSONArray toJSONArray(List<?> listData) {
		JSONArray arr = new JSONArray();
		
		for (Object item : listData) {
			arr.add(item);
		}
		
		return arr;
	}
	
	public static String formatJSONString(String jsonString) {
		JSONObject jo = JSON.parseObject(jsonString);
		return toJSONString(jo, true);
	}
	
	public static <T> T parse(String jsonString, Class<T> type) {
		return JSON.parseObject(jsonString, type);
	}
	
	public static JSONObject parse(String jsonString) {
		return JSON.parseObject(jsonString, Feature.OrderedField);
	}
	
	public static String getString(com.alibaba.fastjson.JSONObject jo, String key) {
		return getString(jo, key, "");
	}
	
	public static String getString(com.alibaba.fastjson.JSONObject jo, String key, String defaultValue) {
		String value = defaultValue;
		
		if (key != null && jo.containsKey(key)) {
			value = jo.getString(key);
		}
		
		return value;
	}
	
	public static int getInt(com.alibaba.fastjson.JSONObject jo, String key) {
		return getInt(jo, key, 0);
	}
	
	public static int getInt(com.alibaba.fastjson.JSONObject jo, String key, int defaultValue) {
		int value = defaultValue;
		
		if (key != null && jo.containsKey(key)) {
			Integer i = jo.getInteger(key);
			if (i != null) {
				value = i.intValue();
			}
		}
		
		return value;
	}
	
	public static int getDouble(com.alibaba.fastjson.JSONObject jo, String key) {
		return getInt(jo, key, 0);
	}
	
	public static double getDouble(com.alibaba.fastjson.JSONObject jo, String key, double defaultValue) {
		double value = defaultValue;
		
		if (key != null && jo.containsKey(key)) {
			value = jo.getDoubleValue(key);
		}
		
		return value;
	}
	
	/**
	 * 获取JSONObject对象指定路径的值。如果值不存在将返回null。
	 * @param object JSONObject对象。
	 * @param path 路径。
	 * @param separator 路径分隔符。
	 * @return 获取的值。
	 */
	public static Object getValue(JSONObject object, String path, char separator) {
		if (path == null) {
			throw new RuntimeException("null path value");
		}
		if (path.isEmpty()) {
			return object;
		}
		String item, items[] = StrUtils.split(path, separator);
		JSONObject obj = object;
		int i, j = items.length - 1;

		for (i = 0; i < j; i++) {
			item = items[i];
			obj = obj.getJSONObject(item);
			if (obj == null) {
				return null;
			}
		}
		return obj.get(items[j]);
	}

	/**
	 * 设置JSONObject对象指定路径属性的值，如果属性不存在将创建该属性。如果值为null，将删除该属性。
	 * @param object 需要设置的JSONObject对象。
	 * @param path 属性路径。
	 * @param separator 路径分隔符。
	 * @param value 设置的值。
	 * @return object本身。
	 */
	public static JSONObject setValue(JSONObject object, String path,
			char separator, Object value) {
		if (StrUtils.isEmpty(path))
			throw new RuntimeException("Path is null or empty");
		String item, items[] = StrUtils.split(path, separator);
		JSONObject obj = object;
		int i, j = items.length - 1;

		for (i = 0; i < j; i++) {
			item = items[i];
			obj = obj.getJSONObject(item);
			if (obj == null) {
				throw new RuntimeException("Path \"" + path
						+ "\" does not exist.");
			}
		}
		obj.put(items[j], value);
		return object;
	}
	
	/**
	 * 查找特定的包含某个键值的子JSONObject对象。
	 * @param jo 需要查找的JSONObject对象。
	 * @param itemsKey 子列表项的属性名称。
	 * @param key 查找的属性名称。
	 * @param value 查找的属性值。
	 * @return JSONObject对象。
	 */
	public static JSONObject findObject(JSONObject jo, String itemsKey, String key, String value) {
		if (jo.getString(key).equals(value)) {
			return jo;
		}
		JSONArray ja = jo.getJSONArray(itemsKey);
		if (ja != null) {
			int i, j = ja.size();
			JSONObject item, result;

			for (i = 0; i < j; i++) {
				item = ja.getJSONObject(i);
				if (item != null) {
					result = findObject(item, itemsKey, key, value);
					if (result != null)
						return result;
				}
			}
		}
		return null;
	}
	
	/**
	 * 查找特定的包含某个键值的子JSONObject对象的上级JSONArray对象。
	 * @param jo 需要查找的JSONObject对象。
	 * @param itemsKey 子列表项的属性名称。
	 * @param key 查找的属性名称。
	 * @param value 查找的属性值。
	 * @return JSONArray对象。
	 */
	public static JSONArray findArray(JSONObject jo, String itemsKey, String key, String value) {
		JSONArray ja = jo.getJSONArray(itemsKey);
		if (ja != null) {
			int i, j = ja.size();
			JSONObject item;
			JSONArray result;

			for (i = 0; i < j; i++) {
				item = ja.getJSONObject(i);
				if (item != null) {
					if (item.getString(key).equals(value))
						return ja;
					result = findArray(item, itemsKey, key, value);
					if (result != null)
						return result;
				}
			}
		}
		return null;
	}
	
	/**
	 * 合并JSON数组中的所有项为字符串，每项之间以指定分隔符分隔。
	 * @param array 需要合并的JSON数组对象。
	 * @param separator 分隔符。
	 * @return 合并后组成的字符串。
	 */
	public static String join(JSONArray array, String separator) {
		int i, j = array.size();
		if (j == 0)
			return "";
		StringBuilder buf = new StringBuilder();
		for (i = 0; i < j; i++) {
			if (i > 0)
				buf.append(separator);
			buf.append(array.getString(i));
		}
		return buf.toString();
	}
	
	/**
	 * 替换首个包含某个键值的子JSONObject对象为另一Object对象。
	 * @param jo 需要替换某个子项的JSONObject对象。
	 * @param itemsKey 子列表项的属性名称。
	 * @param key 查找的属性名称。
	 * @param value 查找的属性值。
	 * @param data 替换的对象。
	 * @return 找到对象并被替换返回true，否则返回false。
	 */
	public static boolean replace(JSONObject jo, String itemsKey, String key, String value, Object data) {
		JSONArray ja = jo.getJSONArray(itemsKey);
		if (ja != null) {
			int i, j = ja.size();
			JSONObject item;

			for (i = 0; i < j; i++) {
				item = ja.getJSONObject(i);
				if (item != null) {
					if (StrUtils.isEqual(item.getString(key), value)) {
						ja.add(i, data);
						return true;
					} else if (replace(item, itemsKey, key, value, data))
						return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * 删除特定的包含某个键值的子JSONObject对象。
	 * @param jo 需要删除某个子项的JSONObject对象。
	 * @param itemsKey 子列表项的属性名称。
	 * @param key 查找的属性名称。
	 * @param value 查找的属性值。
	 * @return 以字符串形式表示的被删除的JSONObject对象。
	 */
	public static String remove(JSONObject jo, String itemsKey, String key,
			String value) {
		JSONArray ja = jo.getJSONArray(itemsKey);

		if (ja != null) {
			int i, j = ja.size();
			JSONObject item;
			String result;

			for (i = j - 1; i >= 0; i--) {
				item = ja.getJSONObject(i);
				if (item != null) {
					if (StrUtils.isEqual(item.getString(key), value)) {
						result = item.toString();
						ja.remove(i);
						return result;
					} else {
						result = remove(item, itemsKey, key, value);
						if (result != null)
							return result;
					}
				}
			}
		}
		return null;
	}

	/**
	 * 获取JSONObject对象中指定名称的对象值，如果值为JSON.null或null则返回null。
	 * @param jo 需要获取值的JSON对象。
	 * @param key 名称。
	 * @return 指定名称的对象值。
	 */
	public static Object opt(JSONObject jo, String key) {
		if (key == null)
			return null;
		else
			return jo.get(key);
	}
	
	public static JSONObject fromObject(Map<String, Object> map) {
		JSONObject jo = new JSONObject();
		
		for (Entry<String, Object> entry : map.entrySet()) {
			jo.put(entry.getKey(), entry.getValue());
		}
		
		return jo;
	}
	
	public static final Object NULL = new Null();
	
	private static final class Null {
		protected final Object clone() {
			return this;
		}
		
		public boolean equals(Object object) {
			return (object == null) || (object == this);
		}
		
		public String toString() {
			return "null";
		}
	}
}
