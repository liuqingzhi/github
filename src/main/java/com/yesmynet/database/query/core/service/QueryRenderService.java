package com.yesmynet.database.query.core.service;

import com.yesmynet.database.query.core.dto.ParameterDefinition;

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
}
