package ct.wiremock.demo._global.config;

import ct.wiremock.demo._global.Utils.JWTUtils;
import ct.wiremock.demo._global.Utils.RegExUtils;
import ct.wiremock.demo._global.Utils.SqlUtils;
import ct.wiremock.demo._global.Utils.Utils;
import ct.wiremock.demo._global.enums.Roles;
import ct.wiremock.demo.service.user.UserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.postgresql.util.PGobject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;

@Component
public class DB {
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	
	@Autowired
	private Config config;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private JWTUtils jwtUtils;
	
	private int generatedId = 0;
	
	private boolean _checkPermissions = true;
	private boolean _replaceComparisonSignsWithoutSingleQuotes = false;
	
	private String _getLastID = "SELECT LAST_INSERT_ID()";
	
//	private String solveBinds(String sql, Map<String, Object> params) {
//		return returnSQLSolvedBinds(sql, params);
//	}
	
	private String solveBinds(String sql, Map<String, Object> params, boolean checkPermissions, boolean replaceComparisonSignsWithoutSingleQuotes) {
		if (checkPermissions == true) {
			String jwtJson = jwtUtils.decodeJWT();
			if (jwtJson != null) {
				Map<String, Object> jwt = jwtUtils.getJWTDecodedMap();
				String authority = ((List<String>) jwt.get("authorities")).get(0);
				if (authority.equals(Roles.ROLE_SUPERADMIN.getText())) {
					params.put("user_id", null);
				} else {
					params.put("user_id", jwt.get("user_id"));
				}
			}
		}
		
		
		
		return returnSQLSolvedBinds(sql, params, replaceComparisonSignsWithoutSingleQuotes);
	}
	
	private String returnSQLSolvedBinds(String sql, Map<String, Object> params, boolean replaceComparisonSignsWithoutSingleQuotes) {
		String _sql = sql;
		String[] replaceCharsAsIs = new String[]{"'<'","'>'","'<='","'>='","'='"};
		List<String> replaceCharsAsIsList = Arrays.asList(replaceCharsAsIs);
		
		if (params != null && params.size() > 0) {
			List<String> binds = RegExUtils.getSQLBindings(sql);
			for (String bind: binds) {
				String _bind = ":" + bind;
				String paramsValueBind = String.valueOf(params.get(bind));
				if (paramsValueBind.startsWith(SqlUtils.skip_crt_bind_character)) {
					paramsValueBind = paramsValueBind.split(SqlUtils.skip_crt_bind_character)[1];
				}
				String valueOfBind = paramsValueBind;
				
				if (params.get(bind) instanceof List) {
					List<Object> lst = (List<Object>) params.get(bind);
					String bindValue = StringUtils.join(lst, ",");
					_sql = _sql.replaceAll("\\Q"+ _bind + "\\E\\b", bindValue);
				} else if (params.get(bind) instanceof Boolean) {
					_sql = _sql.replaceAll("\\Q"+ _bind + "\\E\\b", Matcher.quoteReplacement(valueOfBind));
				} else if (!valueOfBind.startsWith("0") && (NumberUtils.isParsable(valueOfBind))) {
					_sql = _sql.replaceAll("\\Q"+ _bind + "\\E\\b", Matcher.quoteReplacement(valueOfBind));
				} else if (Utils.isInteger(valueOfBind) || StringUtils.isNumeric((CharSequence) params.get(bind))) {
					_sql = _sql.replaceAll("\\Q"+ _bind + "\\E\\b", Matcher.quoteReplacement(valueOfBind));
				} else if (params.get(bind) instanceof String || StringUtils.isAlphanumericSpace((CharSequence) params.get(bind))) {
					String parsedBind = SqlUtils.parseBindValueEnclosedInSingleQuotes(valueOfBind);
					if (replaceComparisonSignsWithoutSingleQuotes && replaceCharsAsIsList.contains(parsedBind)) {
						parsedBind = parsedBind.replace("'","");
					}
					_sql = _sql.replaceAll("\\Q"+ _bind + "\\E\\b", Matcher.quoteReplacement(parsedBind));
				} else {
					_sql = _sql.replaceAll("\\Q"+ _bind + "\\E\\b", Matcher.quoteReplacement(valueOfBind));
				}
				
			}
		}
		
		_sql = _sql.replace("'null'", "null");
		return _sql;
	}
	
	private Map<String, Object> returnTrimmedValues(Map<String, Object> params) {
		Map<String, Object> _params = new HashMap<>();
		for (Map.Entry<String, Object> entry: params.entrySet()) {
			if (entry.getValue() instanceof Boolean) {
				_params.put(entry.getKey(), entry.getValue());
			} else if (!Utils.isInteger(String.valueOf(entry.getValue())) && (!(entry.getValue() instanceof List))) {
				String value = String.valueOf(entry.getValue()).trim().replace("'", "''");
//				String value = String.valueOf(entry.getValue()).trim();
				_params.put(entry.getKey(), value);
			} else {
				_params.put(entry.getKey(),  entry.getValue());
			}
		}
		
		return _params;
	}
	
	private void logSql(String sql) {
		if (Boolean.parseBoolean(config.getProperty("log_sqls"))) {
			System.out.println(sql);
		}
	}
	
	/**
	 * curata valorile de tip json
	 * @param lst
	 * @return
	 */
	private List<Map<String, Object>> cleanJsonValues(List<Map<String, Object>> lst) {
		List<Map<String, Object>> lst2 = new ArrayList<>();
		
		for (Map<String, Object> map : lst) {
			for (Map.Entry<String, Object> entry: map.entrySet()) {
				Object obj = entry.getValue();
				if (obj instanceof PGobject) {
					PGobject pGobject = (PGobject) obj;
					String s = pGobject.getValue();
					map.put(entry.getKey(), s);
				}
			}
			lst2.add(map);
		}
		
		return lst2;
	}
	
	/**
	 * clean json values
	 * @param map
	 * @return
	 */
	private Map<String, Object> cleanJsonValues(Map<String, Object> map) {
		Map<String, Object> map2 = new HashMap<>();
		
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			Object obj = entry.getValue();
			if (obj instanceof PGobject) {
				PGobject pGobject = (PGobject) obj;
				String s = pGobject.getValue();
				entry.setValue(s);
			}
			map2.put(entry.getKey(), entry.getValue());
		}
		
		return map2;
	}
	
	public List<Map<String, Object>> getAll(String sql, Map<String, Object> params) {
		params = returnTrimmedValues(params);
		String propSQL = config.getProperty(sql);
		propSQL = solveBinds(propSQL, params, _checkPermissions, _replaceComparisonSignsWithoutSingleQuotes);
		logSql(propSQL);
		return cleanJsonValues(jdbcTemplate.queryForList(propSQL));
	}
	
	public List<Map<String, Object>> getAll(String sql, Map<String, Object> params, boolean checkPermissions) {
		params = returnTrimmedValues(params);
		String propSQL = config.getProperty(sql);
		propSQL = solveBinds(propSQL, params, checkPermissions, _replaceComparisonSignsWithoutSingleQuotes);
		logSql(propSQL);
		return cleanJsonValues(jdbcTemplate.queryForList(propSQL));
	}
	
	public List<Map<String, Object>> getAll(String sql, Map<String, Object> params, boolean checkPermissions, boolean replaceComparisonSignsWithoutSingleQuotes) {
		params = returnTrimmedValues(params);
		String propSQL = config.getProperty(sql);
		propSQL = solveBinds(propSQL, params, checkPermissions, replaceComparisonSignsWithoutSingleQuotes);
		logSql(propSQL);
		return cleanJsonValues(jdbcTemplate.queryForList(propSQL));
	}
	
	public List<Map<String, Object>> getAll(String sql) {
		String propSQL = config.getProperty(sql);
		propSQL = solveBinds(propSQL, new HashMap<>(), _checkPermissions, _replaceComparisonSignsWithoutSingleQuotes);
		logSql(propSQL);
		return cleanJsonValues(jdbcTemplate.queryForList(propSQL));
	}
	
	public Map<String, Object> getRow(String sql) {
		String propSQL = config.getProperty(sql);
		propSQL = solveBinds(propSQL, new HashMap<>(), _checkPermissions, _replaceComparisonSignsWithoutSingleQuotes);
		logSql(propSQL);
		return cleanJsonValues(jdbcTemplate.queryForMap(propSQL));
	}
	
	public Map<String, Object> getRow(String sql, Map<String, Object> params) {
		params = returnTrimmedValues(params);
		String propSQL = config.getProperty(sql);
		propSQL = solveBinds(propSQL, params, _checkPermissions, _replaceComparisonSignsWithoutSingleQuotes);
		Map<String, Object> result = new HashMap<>();
		logSql(propSQL);
		try {
			result = cleanJsonValues(jdbcTemplate.queryForMap(propSQL));
		} catch (EmptyResultDataAccessException e) {}
		return result;
	}
	
	public Map<String, Object> getRow(String sql, Map<String, Object> params, boolean checkPermissions, boolean replaceComparisonSignsWithoutSingleQuotes) {
		params = returnTrimmedValues(params);
		String propSQL = config.getProperty(sql);
		propSQL = solveBinds(propSQL, params, checkPermissions, replaceComparisonSignsWithoutSingleQuotes);
		Map<String, Object> result = new HashMap<>();
		logSql(propSQL);
		try {
			result = cleanJsonValues(jdbcTemplate.queryForMap(propSQL));
		} catch (EmptyResultDataAccessException e) {}
		return result;
	}
	
	public Map<String, Object> getRow(String sql, Map<String, Object> params, boolean replaceComparisonSignsWithoutSingleQuotes) {
		params = returnTrimmedValues(params);
		String propSQL = config.getProperty(sql);
		propSQL = solveBinds(propSQL, params, _checkPermissions, replaceComparisonSignsWithoutSingleQuotes);
		Map<String, Object> result = new HashMap<>();
		logSql(propSQL);
		try {
			result = cleanJsonValues(jdbcTemplate.queryForMap(propSQL));
		} catch (EmptyResultDataAccessException e) {}
		return result;
	}
	
	public List<Map<String, Object>> search(String sql, Map<String, Object> filters) {
		filters = returnTrimmedValues(filters);
		StringBuilder where = SqlUtils.returnDynamicWhere(filters, false);
		String propSQL = config.getProperty(sql);
		propSQL = propSQL.replace("[where]", where);
		propSQL = solveBinds(propSQL, filters, _checkPermissions, _replaceComparisonSignsWithoutSingleQuotes);
		logSql(propSQL);
		return cleanJsonValues(jdbcTemplate.queryForList(propSQL));
	}
	
	public List<Map<String, Object>> searchAndSolveBinds(String sql, Map<String, Object> filters) {
		filters = returnTrimmedValues(filters);
		StringBuilder where = SqlUtils.returnDynamicWhere(filters, false);
		String propSQL = config.getProperty(sql);
		propSQL = propSQL.replace("[where]", where);
		propSQL = solveBinds(propSQL, filters, _checkPermissions, _replaceComparisonSignsWithoutSingleQuotes);
		logSql(propSQL);
		return cleanJsonValues(jdbcTemplate.queryForList(propSQL));
	}
	
	public List<Map<String, Object>> search(String sql, Map<String, Object> filters, boolean exactMatch) {
		filters = returnTrimmedValues(filters);
		StringBuilder where = SqlUtils.returnDynamicWhere(filters, exactMatch);
		String propSQL = config.getProperty(sql);
		propSQL = propSQL.replace("[where]", where);
		propSQL = solveBinds(propSQL, new HashMap<>(), _checkPermissions, _replaceComparisonSignsWithoutSingleQuotes);
		logSql(propSQL);
		return cleanJsonValues(jdbcTemplate.queryForList(propSQL));
	}
	
	public List<Map<String, Object>> search(String sql, Map<String, Object> filters, boolean exactMatch, boolean replaceComparisonSignsWithoutSingleQuotes) {
		filters = returnTrimmedValues(filters);
		StringBuilder where = SqlUtils.returnDynamicWhere(filters, exactMatch);
		String propSQL = config.getProperty(sql);
		propSQL = propSQL.replace("[where]", where);
		propSQL = solveBinds(propSQL, new HashMap<>(), _checkPermissions, replaceComparisonSignsWithoutSingleQuotes);
		logSql(propSQL);
		return cleanJsonValues(jdbcTemplate.queryForList(propSQL));
	}
	
	@Transactional
	public void execute(String sql, Map<String, Object> params) {
		params = returnTrimmedValues(params);
		String propSQL = config.getProperty(sql);
		propSQL = solveBinds(propSQL, params, _checkPermissions, _replaceComparisonSignsWithoutSingleQuotes);
		logSql(propSQL);
		namedParameterJdbcTemplate.execute(propSQL, params, new PreparedStatementCallback() {
			@Override
			public Object doInPreparedStatement(PreparedStatement preparedStatement) throws SQLException, DataAccessException {
				return preparedStatement.executeQuery();
			}
		});
	}
	
	@Transactional
	public void execute(String sql, Map<String, Object> params, boolean replaceComparisonSignsWithoutSingleQuotes) {
		params = returnTrimmedValues(params);
		String propSQL = config.getProperty(sql);
		propSQL = solveBinds(propSQL, params, _checkPermissions, replaceComparisonSignsWithoutSingleQuotes);
		logSql(propSQL);
		namedParameterJdbcTemplate.execute(propSQL, params, new PreparedStatementCallback() {
			@Override
			public Object doInPreparedStatement(PreparedStatement preparedStatement) throws SQLException, DataAccessException {
				return preparedStatement.executeQuery();
			}
		});
	}
	
	@Transactional
	public void execute(String sql) {
		String propSQL = config.getProperty(sql);
		propSQL = solveBinds(propSQL, new HashMap<>(), _checkPermissions, _replaceComparisonSignsWithoutSingleQuotes);
		logSql(propSQL);
		namedParameterJdbcTemplate.execute(propSQL, new PreparedStatementCallback() {
			@Override
			public Object doInPreparedStatement(PreparedStatement preparedStatement) throws SQLException, DataAccessException {
				return preparedStatement.executeQuery();
			}
		});
	}
	
	@Transactional
	public void execute(String sql, boolean replaceComparisonSignsWithoutSingleQuotes) {
		String propSQL = config.getProperty(sql);
		propSQL = solveBinds(propSQL, new HashMap<>(), _checkPermissions, replaceComparisonSignsWithoutSingleQuotes);
		logSql(propSQL);
		namedParameterJdbcTemplate.execute(propSQL, new PreparedStatementCallback() {
			@Override
			public Object doInPreparedStatement(PreparedStatement preparedStatement) throws SQLException, DataAccessException {
				return preparedStatement.executeQuery();
			}
		});
	}
	
	@Transactional
	public void executeUpdate(String sql) {
		String propSQL = config.getProperty(sql);
		propSQL = solveBinds(propSQL, new HashMap<>(), _checkPermissions, _replaceComparisonSignsWithoutSingleQuotes);
		logSql(propSQL);
		namedParameterJdbcTemplate.execute(propSQL, new PreparedStatementCallback() {
			@Override
			public Object doInPreparedStatement(PreparedStatement preparedStatement) throws SQLException, DataAccessException {
				return preparedStatement.executeUpdate();
			}
		});
	}
	
	@Transactional
	public void executeUpdate(String sql, boolean replaceComparisonSignsWithoutSingleQuotes) {
		String propSQL = config.getProperty(sql);
		propSQL = solveBinds(propSQL, new HashMap<>(), _checkPermissions, replaceComparisonSignsWithoutSingleQuotes);
		logSql(propSQL);
		namedParameterJdbcTemplate.execute(propSQL, new PreparedStatementCallback() {
			@Override
			public Object doInPreparedStatement(PreparedStatement preparedStatement) throws SQLException, DataAccessException {
				return preparedStatement.executeUpdate();
			}
		});
	}
	
	@Transactional
	public void executeUpdate(String sql, Map<String, Object> params) {
		params = returnTrimmedValues(params);
		String propSQL = config.getProperty(sql);
		propSQL = solveBinds(propSQL, params, _checkPermissions, _replaceComparisonSignsWithoutSingleQuotes);
		logSql(propSQL);
		namedParameterJdbcTemplate.execute(propSQL, params, new PreparedStatementCallback() {
			@Override
			public Object doInPreparedStatement(PreparedStatement preparedStatement) throws SQLException, DataAccessException {
				return preparedStatement.executeUpdate();
			}
		});
	}
	
	@Transactional
	public void executeUpdate(String sql, Map<String, Object> params, boolean replaceComparisonSignsWithoutSingleQuotes) {
		params = returnTrimmedValues(params);
		String propSQL = config.getProperty(sql);
		propSQL = solveBinds(propSQL, params, _checkPermissions, replaceComparisonSignsWithoutSingleQuotes);
		logSql(propSQL);
		namedParameterJdbcTemplate.execute(propSQL, params, new PreparedStatementCallback() {
			@Override
			public Object doInPreparedStatement(PreparedStatement preparedStatement) throws SQLException, DataAccessException {
				return preparedStatement.executeUpdate();
			}
		});
	}
	
	private void setLastID() {
		namedParameterJdbcTemplate.execute(_getLastID, new PreparedStatementCallback() {
			@Override
			public Object doInPreparedStatement(PreparedStatement preparedStatement) throws SQLException, DataAccessException {
				ResultSet resultSet = preparedStatement.executeQuery();
				if (resultSet != null && resultSet.next()) {
					generatedId = resultSet.getInt(1);
				}
				return generatedId;
			}
		});
	}
	
	/**
	 * https://stackoverflow.com/questions/1665846/identity-from-sql-insert-via-jdbctemplate
	 * @param sql
	 * @param params
	 */
	public int executeGetLastId (String sql, Map<String, Object> params) {
		params = returnTrimmedValues(params);
		String propSQL = config.getProperty(sql);
		propSQL = solveBinds(propSQL, params, _checkPermissions, _replaceComparisonSignsWithoutSingleQuotes);
		logSql(propSQL);
		
		this.executeUpdate(propSQL, params);
		this.setLastID();
		
		return generatedId;
	}
	
	public int executeGetLastId (String sql, Map<String, Object> params, boolean replaceComparisonSignsWithoutSingleQuotes) {
		params = returnTrimmedValues(params);
		String propSQL = config.getProperty(sql);
		propSQL = solveBinds(propSQL, params, _checkPermissions, replaceComparisonSignsWithoutSingleQuotes);
		logSql(propSQL);
		
		this.executeUpdate(propSQL, params);
		this.setLastID();
		
		return generatedId;
	}
	
	public int executeGetLastId (String sql) {
		String propSQL = config.getProperty(sql);
		propSQL = solveBinds(propSQL, new HashMap<>(), _checkPermissions, _replaceComparisonSignsWithoutSingleQuotes);
		logSql(propSQL);
		this.executeUpdate(propSQL);
		this.setLastID();
		
		return generatedId;
	}
	
	public int executeGetLastId (String sql, boolean replaceComparisonSignsWithoutSingleQuotes) {
		String propSQL = config.getProperty(sql);
		propSQL = solveBinds(propSQL, new HashMap<>(), _checkPermissions, replaceComparisonSignsWithoutSingleQuotes);
		logSql(propSQL);
		this.executeUpdate(propSQL);
		this.setLastID();
		
		return generatedId;
	}
	
	public Object executeGetResult (String sql, Map<String, Object> params) {
		params = returnTrimmedValues(params);
		String propSQL = config.getProperty(sql);
		propSQL = solveBinds(propSQL, params, _checkPermissions, _replaceComparisonSignsWithoutSingleQuotes);
		logSql(propSQL);
		final Object[] result = new Object[1];
		namedParameterJdbcTemplate.execute(propSQL, params, new PreparedStatementCallback() {
			@Override
			public Object doInPreparedStatement(PreparedStatement preparedStatement) throws SQLException, DataAccessException {
				ResultSet resultSet = preparedStatement.executeQuery();
				if (resultSet != null && resultSet.next()) {
					result[0] = resultSet.getObject(1);
				}
				return result[0];
			}
		});
		
		return result[0];
	}
	
	public Object executeGetResult (String sql, Map<String, Object> params, boolean replaceComparisonSignsWithoutSingleQuotes) {
		params = returnTrimmedValues(params);
		String propSQL = config.getProperty(sql);
		propSQL = solveBinds(propSQL, params, _checkPermissions, replaceComparisonSignsWithoutSingleQuotes);
		logSql(propSQL);
		final Object[] result = new Object[1];
		namedParameterJdbcTemplate.execute(propSQL, params, new PreparedStatementCallback() {
			@Override
			public Object doInPreparedStatement(PreparedStatement preparedStatement) throws SQLException, DataAccessException {
				ResultSet resultSet = preparedStatement.executeQuery();
				if (resultSet != null && resultSet.next()) {
					result[0] = resultSet.getObject(1);
				}
				return result[0];
			}
		});
		
		return result[0];
	}
	
	public Object executeGetResult (String sql) {
		String propSQL = config.getProperty(sql);
		propSQL = solveBinds(propSQL, new HashMap<>(), _checkPermissions, _replaceComparisonSignsWithoutSingleQuotes);
		logSql(propSQL);
		final Object[] result = new Object[1];
		namedParameterJdbcTemplate.execute(propSQL, new PreparedStatementCallback() {
			@Override
			public Object doInPreparedStatement(PreparedStatement preparedStatement) throws SQLException, DataAccessException {
				ResultSet resultSet = preparedStatement.executeQuery();
				if (resultSet != null && resultSet.next()) {
					result[0] = resultSet.getObject(1);
				}
				return result[0];
			}
		});
		
		return result[0];
	}
	
	public Object executeGetResult (String sql, boolean replaceComparisonSignsWithoutSingleQuotes) {
		String propSQL = config.getProperty(sql);
		propSQL = solveBinds(propSQL, new HashMap<>(), _checkPermissions, replaceComparisonSignsWithoutSingleQuotes);
		logSql(propSQL);
		final Object[] result = new Object[1];
		namedParameterJdbcTemplate.execute(propSQL, new PreparedStatementCallback() {
			@Override
			public Object doInPreparedStatement(PreparedStatement preparedStatement) throws SQLException, DataAccessException {
				ResultSet resultSet = preparedStatement.executeQuery();
				if (resultSet != null && resultSet.next()) {
					result[0] = resultSet.getObject(1);
				}
				return result[0];
			}
		});
		
		return result[0];
	}
	
	public void clearTable(String tableName) {
		this.execute("DELETE FROM " + tableName + " RETURNING *");
	}
}
