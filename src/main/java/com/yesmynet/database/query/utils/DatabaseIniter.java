package com.yesmynet.database.query.utils;

import java.sql.SQLException;
import java.util.Map;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.ibatis.SqlMapClientCallback;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.ibatis.sqlmap.client.SqlMapExecutor;
/**
 * 初始始化数据库
 * @author 刘庆志
 *
 */
public class DatabaseIniter extends SqlMapClientDaoSupport 
{
	private Logger logger = LoggerFactory.getLogger(this.getClass());
	/**
	 * 初始化数据库的方法
	 */
	public void init()
	{
		boolean inited=dbinited();
		if(!inited)
		{
			initDB();
		}
	}
	private boolean dbinited()
	{
		boolean re=false;
		
		Map queryForObject=null;
		try
		{
			queryForObject = (Map)this.getSqlMapClientTemplate().queryForObject("getDBInitedData");
		}
		catch (Exception e)
		{
			logger.debug("查询出的数据=",e);//JSONObject.fromObject(queryForObject));
			re=false;
		}
		logger.debug("查询出的数据={}",JSONObject.fromObject(queryForObject));
		if(queryForObject!=null)
		{
			Object object = queryForObject.get("INITED_FLAG");
			logger.debug("初始化标志={}",object);
			if("1".equals(object.toString()))
			{
				re=true;
			}
		}
		
		return re;
		
	}
	private void initDB()
	{
		this.getSqlMapClientTemplate().update("createTableSysInit");
		this.getSqlMapClientTemplate().insert("insertSysInit");
		
	}
	
}
