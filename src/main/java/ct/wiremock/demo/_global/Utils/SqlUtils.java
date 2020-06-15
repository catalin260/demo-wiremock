package ct.wiremock.demo._global.Utils;

import org.apache.commons.lang3.StringUtils;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SqlUtils {
	public static String list_includes_elements = "in";
	public static String list_not_includes_elements = "not in";
	public static String skip_crt_bind_character = "SKIP_BIND_";
	
	public static StringBuilder returnDynamicWhere(Map<String, Object> filters, boolean exactMatch) {
		Set<String> keys = filters.keySet();
		StringBuilder where = new StringBuilder();
		
		Iterator<String> iterator = keys.iterator();
		
		while (iterator.hasNext()) {
			String k = iterator.next();
			String _where = "";
			boolean isFilterInstanceOfString = false;
			
			if (k.startsWith(skip_crt_bind_character)) continue;
			
			if (filters.get(k) == null) {
				_where = " " + k + " is null ";
			} else if (filters.get(k) instanceof List) {
				String[] key_operator = k.split(":");
				String values = "(";
				for ( Object val: (List) filters.get(k)) {
					values += (StringUtils.isNumeric(String.valueOf(val))) ? String.valueOf(val) : "'" + val + "'";
					values += ",";
				}
				
				values = values.substring(0, values.length() - 1) + ")";
				_where = " " + key_operator[0] + " " + key_operator[1] + " " + values;
			} else if (Utils.isInteger(String.valueOf(filters.get(k)))) {
				_where = " " + k + "=" + String.valueOf(filters.get(k)) + " ";
				//isFilterInstanceOfString = true;
			} else if (filters.get(k) instanceof Boolean) {
				_where = " " + k + "=" + String.valueOf(filters.get(k)) + " ";
				//isFilterInstanceOfString = true;
			} else if (((String) filters.get(k)).equals("")) {
				_where = "";
			} else {
				String _filterValue = parseBindValueEnclosedInSingleQuotes(String.valueOf(filters.get(k)));
				if (_filterValue.equals("'null'")) {
					_where = " " + k + " is null ";
				} else {
					if (exactMatch) {
//					_where = k + "='" + (String) filters.get(k) + "'";
						_where = " " + k + "=" + _filterValue;
					} else {
						_filterValue = "'%" + _filterValue.substring(1);
						_filterValue = _filterValue.substring(0, _filterValue.length() - 1) + "%'";
//					_where = " LOWER(" + k + ") like LOWER('%" + (String) filters.get(k) + "%') ";
						_where = " LOWER(" + k + ") like LOWER(" + _filterValue + ") ";
					}
				}
				
				isFilterInstanceOfString = false;
			}
			
			/*if (filters.get(k) instanceof String) {
				_where = " LOWER(" + k + ") like LOWER('%" + (String) filters.get(k) + "%') ";
				isFilterInstanceOfString = true;
			} else if (filters.get(k) instanceof Integer) {
				_where = " " + k + "=" + (int) filters.get(k) + " ";
				isFilterInstanceOfString = false;
			} else if (filters.get(k) == null) {
				_where = " " + k + " is null ";
			}*/

//			if (keys.size() > 1) {
			if (keys.size() > 1 && iterator.hasNext()) {
				if (isFilterInstanceOfString && !(String.valueOf(filters.get(k))).equals("")) {
					where.append(_where).append(" and ");
				} else if (!isFilterInstanceOfString) {
					where.append(_where).append(" and ");
				}
			}
			
			if (!iterator.hasNext()) {
				where.append(_where);
			}
		}
		
		if (where.toString().equals("")) {
			where = new StringBuilder(" 1 = 1 ");
		}
		
		return where;
	}

	public static String parseBindValueEnclosedInSingleQuotes(String bind) {
		String _crtBindValueReplacement = "";
		if (bind.equals(list_includes_elements) || bind.equals(list_not_includes_elements)) {
			_crtBindValueReplacement = bind;
		} else if (bind.startsWith("''") && bind.endsWith("''")) {
			_crtBindValueReplacement = bind.replace("''", "'");
		} else if (bind.startsWith("'") && bind.endsWith("'")) {
			_crtBindValueReplacement = bind;
		} else {
			_crtBindValueReplacement = "'" + bind + "'";
		}

		return _crtBindValueReplacement;
	}
	
	/*public static StringBuilder returnDynamicWhere(Map<String, Object> filters) {
		Set<String> keys = filters.keySet();
		StringBuilder where = new StringBuilder();
		
		Iterator<String> iterator = keys.iterator();
		
		while (iterator.hasNext()) {
			String k = iterator.next();
			String _where = "";
			boolean isFilterInstanceOfString = false;
			
			if (filters.get(k) == null) {
				_where = " " + k + " is null ";
			} else if (Utils.isInteger(String.valueOf(filters.get(k)))) {
				_where = " " + k + "=" + String.valueOf(filters.get(k)) + " ";
				isFilterInstanceOfString = true;
			} else if (((String) filters.get(k)).equals("")) {
				_where = "";
			} else {
				_where = " LOWER(" + k + ") like LOWER('%" + (String) filters.get(k) + "%') ";
				isFilterInstanceOfString = false;
			}
			
			*//*if (filters.get(k) instanceof String) {
				_where = " LOWER(" + k + ") like LOWER('%" + (String) filters.get(k) + "%') ";
				isFilterInstanceOfString = true;
			} else if (filters.get(k) instanceof Integer) {
				_where = " " + k + "=" + (int) filters.get(k) + " ";
				isFilterInstanceOfString = false;
			} else if (filters.get(k) == null) {
				_where = " " + k + " is null ";
			}*//*
			
//			if (keys.size() > 1) {
			if (keys.size() > 1 && iterator.hasNext()) {
				if (isFilterInstanceOfString && !(String.valueOf(filters.get(k))).equals("")) {
					where.append(_where).append(" and ");
				} else if (!isFilterInstanceOfString) {
					where.append(_where).append(" and ");
				}
			}
			
			if (!iterator.hasNext()) {
				where.append(_where);
			}
		}
		
		if (where.toString().equals("")) {
			where = new StringBuilder(" 1 = 1 ");
		}
		
		return where;
	}*/
}
