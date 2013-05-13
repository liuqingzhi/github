package com.yesmynet.database.query.core.service.impl;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.yesmynet.database.query.core.dto.ParameterDefinition;
import com.yesmynet.database.query.core.dto.ParameterHtmlType;
import com.yesmynet.database.query.core.dto.QueryDefinition;
import com.yesmynet.database.query.core.service.QueryRenderService;

public class QueryRenderServiceImpl implements QueryRenderService
{
	/**
	 * 不同类型的参数类型与对应的html代码的映射
	 * key是参数类型，value是该类型参数要显示的html代码
	 */
	private Map<ParameterHtmlType,String> parameterHtmlTemplate;
	public String getQueryHtml(QueryDefinition query)
	{
		String re="";
		List<ParameterDefinition> parameters = query.getParameters();
		if(!CollectionUtils.isEmpty(parameters))
		{
			for(ParameterDefinition define:parameters)
			{
				String value=define.getValue();
			}
		}
		return re;
	}
	/**
	 * 得到在html中显示一个查询参数的html。
	 * @param parameter 查询参数的定义
	 * @return 用来显示参数的html字符串，如，< input ...>
	 */
	protected String getParameterHtml(ParameterDefinition parameter)
	{
		String re="";
		ParameterHtmlType parameterHtmlType = parameter.getHtmlType();
		String tempalte=parameterHtmlTemplate.get(parameterHtmlType);
		String parameterName=getParameterName(parameter);
		String value=parameter.getValue();
		
		re=MessageFormat.format(tempalte, parameterName,value);
		
		return re;
	}
	/**
	 * 得到参数的html页面上显示的名称
	 * @param parameterDefine
	 * @return
	 */
	protected String getParameterName(ParameterDefinition parameterDefine) 
	{
		String re=parameterDefine.getCustomName();
		if(!StringUtils.hasText(re))
		{
			re=parameterDefine.getDefaltNamePrifix();
			re+=parameterDefine.getId();
		}
		return re;
	}
}
