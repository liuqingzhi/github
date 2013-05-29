package com.yesmynet.database.query.core.service.impl;

import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;
import org.springframework.util.StringUtils;

import com.yesmynet.database.query.core.dto.Query;
import com.yesmynet.database.query.core.dto.QueryDefinition;
import com.yesmynet.database.query.core.service.QueryDefinitionService;

public class QueryDefinitionServiceImpl extends SqlMapClientDaoSupport implements QueryDefinitionService
{

	public QueryDefinition getQueryParameters(String queryId)
	{
		QueryDefinition re=null;
		if(!StringUtils.hasText(queryId))
		{
		    re=getDefaultQuery();
		}
		else
		{
		    re=(QueryDefinition)this.getSqlMapClientTemplate().queryForObject("getQueryDefinitionById", queryId);    
		}
		
		return re;
	}
	/**
	 * 得到默认查询。
	 * 当要没有指定要显示或查看哪个查询时，就得到默认查询。
	 * @return
	 * @author 刘庆志
	 */
	private QueryDefinition getDefaultQuery()
	{
	    QueryDefinition re=null;
	    
	    re=(QueryDefinition)this.getSqlMapClientTemplate().queryForObject("getQueryDefinitionDefault");  
	    
	    return re;
	}
	public Query getQueryInstance(String id)
	{
		QueryDefinition queryDefinition=getQueryParameters(id);
		
		
		return null;
	}

}
