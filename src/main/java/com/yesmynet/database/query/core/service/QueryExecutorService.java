package com.yesmynet.database.query.core.service;

import java.util.List;

import com.yesmynet.database.query.core.dto.Parameter;
import com.yesmynet.database.query.core.dto.Query;
import com.yesmynet.database.query.core.dto.QueryDefinition;

/**
 * 执行查询的Service,也就是对于得到的一个{@link com.yesmynet.database.query.core.dto.Query}实例，
 * 运行这个实例，得到结果，并且显示。
 * 
 * @author 刘庆志
 *
 */
public interface QueryExecutorService
{
	/**
	 * 运行一个查询实例
	 * @param query 要运行的查询的实例
	 * @param parameters 运行查询时的参数
	 */
	public void executeQuery(Query query,QueryDefinition queryDefinition);
}
