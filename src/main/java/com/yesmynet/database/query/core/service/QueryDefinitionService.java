package com.yesmynet.database.query.core.service;

import com.yesmynet.database.query.core.dto.Query;
import com.yesmynet.database.query.core.dto.QueryDefinition;

/**
 * 查询定义的服务类。
 * 如：可以得到一个查询的定义，创建查询对应的实例，以准备执行这个查询。
 * @author 刘庆志
 *
 */
public interface QueryDefinitionService
{
	/**
	 * 根据ID得到表示查询定义的对象。
	 * @param queryId
	 * @return
	 */
	public QueryDefinition getQueryParameters(String queryId);
	/**
	 * 保存查询。
	 * @param queryDefinition 要保存的数据
	 * @author 刘庆志
	 */
	public void save(QueryDefinition queryDefinition);
	/**
	 * 根据ID得到表示查询的实例
	 * @param id
	 * @return
	 */
	public Query getQueryInstance(String id);
	
}
