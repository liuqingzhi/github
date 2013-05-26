package com.yesmynet.database.query.core.service.impl;

import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;

import com.yesmynet.database.query.core.dto.Query;
import com.yesmynet.database.query.core.dto.QueryDefinition;
import com.yesmynet.database.query.core.service.QueryDefinitionService;

public class QueryDefinitionServiceImpl extends SqlMapClientDaoSupport implements QueryDefinitionService
{

	public QueryDefinition getQueryParameters(String queryId)
	{
		QueryDefinition re=null;
		re=(QueryDefinition)this.getSqlMapClientTemplate().queryForObject("getQueryDefinitionById", queryId);
		return re;
	}

	public Query getQueryInstance(String id)
	{
		QueryDefinition queryDefinition=getQueryParameters(id);
		
		
		return null;
	}

}
