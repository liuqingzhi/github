package com.yesmynet.database.query.core.service.impl;

import java.text.MessageFormat;
import java.util.HashMap;
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
    protected Map<ParameterHtmlType, String> parameterHtmlTemplate = new HashMap<ParameterHtmlType, String>()
    {
        {
            put(ParameterHtmlType.inputText, "<input type=''text'' name=''{0}'' value=''{1}'' >");
            put(ParameterHtmlType.textArea, "<textarea rows=''6'' cols=''20'' name=''{0}''>{1}</textarea>");
        }
    };
	public String getQueryHtml(QueryDefinition query)
	{
		StringBuilder re=new StringBuilder();
		if(query!=null)
		{
		    List<Parameter> parameters = query.getParameters();
	        if(!CollectionUtils.isEmpty(parameters))
	        {
	            for(Parameter define:parameters)
	            {
	                String oneParameterHtml=getParameterHtml(define);
	                
	                re.append(oneParameterHtml);
	                
	            }
	        }
	        re.append(query.getAfterParameterHtml());
	        
	        if(query.getShowExecuteButton())
	            re.append("<input type='submit' value='执行查询' name='executeButton'>");
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
		String re=parameter.getTitle()+"：";
		ParameterHtmlType parameterHtmlType = parameter.getHtmlType();
		String tempalte=parameterHtmlTemplate.get(parameterHtmlType);
		String parameterName=parameter.getParameterName();
		String value=parameter.getValue();
		if(!StringUtils.hasText(value)) value="";
		
		re+=MessageFormat.format(tempalte, parameterName,value);
		
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
