package com.yesmynet.database.query.core.service;

import com.yesmynet.database.query.core.dto.ParameterDefinition;
import com.yesmynet.database.query.core.dto.QueryDefinition;

/**
 * 显示一个查询。在执行查询前，要先把查询显示出来，让用户输入查询相关的参数。
 * 我目前考虑的就是以html的方式展示查询。
 * @author 刘庆志
 *
 */
public interface QueryRenderService
{
	/**
	 * 得到在html中显示一个查询参数的html。
	 * @param parameter 查询参数的定义
	 * @return 用来显示参数的html字符串，如，< input ...>
	 */
	public String getParameterHtml(ParameterDefinition parameter);
	/**
	 * 得到在html中显示一个查询的html。
	 * 每个查询在显示时可能有些特殊要求，通过定义每个查询我html，可以对每个查询的界面做一些调整，如：显示一些额外的提示信息，显示一些链接等。
	 * @param query
	 * @return
	 */
	public String getQueryHtml(QueryDefinition query);
}
