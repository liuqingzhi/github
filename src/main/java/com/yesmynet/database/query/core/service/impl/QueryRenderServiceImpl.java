package com.yesmynet.database.query.core.service.impl;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.yesmynet.database.query.core.dto.Parameter;
import com.yesmynet.database.query.core.dto.ParameterHtmlType;
import com.yesmynet.database.query.core.dto.QueryDefinition;
import com.yesmynet.database.query.core.service.QueryRenderService;

public class QueryRenderServiceImpl implements QueryRenderService
{
	/**
	 * 不同类型的参数类型与对应的html代码的映射
	 * key是参数类型，value是该类型参数要显示的html代码
	 */
	protected Map<ParameterHtmlType,String> parameterHtmlTemplate;
	public String getQueryHtml(QueryDefinition query)
	{
		StringBuilder re=new StringBuilder();
		List<Parameter> parameters = query.getParameters();
		if(!CollectionUtils.isEmpty(parameters))
		{
			for(Parameter define:parameters)
			{
				String value=define.getValue();
				String paraName=getParaName(define);
				ParameterHtmlType htmlType = define.getHtmlType();
				String template=parameterHtmlTemplate.get(htmlType);
				
				String html=MessageFormat.format(template, paraName,value);
				re.append(html);
				
			}
		}
		return re.toString();
	}
	/**
	 * 得到参数在http请求时要使用的名字,在java中可以通过这个名字从request中得到参数的值
	 * 如：request.getParameter("名字")
	 * @param parameter
	 * @return
	 */
	protected String getParaName(Parameter parameter)
	{
		String re=parameter.getCustomName();
		if(!StringUtils.hasText(re))
		{
			re=parameter.getDefaltNamePrifix()+""+parameter.getId();
		}
		return re;
	}
	/**
	 * 得到在html中显示一个查询参数的html。
	 * @param parameter 查询参数的定义
	 * @return 用来显示参数的html字符串，如，< input ...>
	 */
	protected String getParameterHtml(Parameter parameter)
	{
		String re="";
		ParameterHtmlType parameterHtmlType = parameter.getHtmlType();
		String tempalte=parameterHtmlTemplate.get(parameterHtmlType);
		String parameterName=parameter.getParameterName();
		String value=parameter.getValue();
		
		re=MessageFormat.format(tempalte, parameterName,value);
		
		return re;
	}
	public Map<ParameterHtmlType, String> getParameterHtmlTemplate()
	{
		return parameterHtmlTemplate;
	}
	public void setParameterHtmlTemplate(Map<ParameterHtmlType, String> parameterHtmlTemplate)
	{
		this.parameterHtmlTemplate = parameterHtmlTemplate;
	}
}
