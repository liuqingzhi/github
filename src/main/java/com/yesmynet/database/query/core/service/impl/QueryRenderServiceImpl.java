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
import com.yesmynet.database.query.dto.DataSourceConfig;

public class QueryRenderServiceImpl implements QueryRenderService
{
	public String getQueryHtml(QueryDefinition query,List<DataSourceConfig> allDataSources,String selectedDataSourceId)
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
	        
	        re.append("<br>");
	        re.append(showDataSourceSelect(allDataSources,selectedDataSourceId));
	        re.append("<br>");
	        if(query.getShowExecuteButton())
	            re.append("<input type='submit' value='执行查询' name='executeButton'>");
		}
		
		return re.toString();
	}
	/**
	 * 输出显示数据源的选择框
	 * @param allDataSources
	 * @param selectedDataSourceId
	 * @return
	 * @author 刘庆志
	 */
	private String showDataSourceSelect(List<DataSourceConfig> allDataSources,String selectedDataSourceId)
	{
	    StringBuilder re=new StringBuilder();
	    
	    re.append("选择数据库：");
	    re.append("<select name=\"SystemDataSourceId\">\n");
	    
	    if(!CollectionUtils.isEmpty(allDataSources))
	    {
	        for(DataSourceConfig d:allDataSources)
	        {
	            re.append("<option value='").append(d.getId()).append("'");
	            
	            if(d.getId().equals(selectedDataSourceId)) re.append(" selected "); 
	            
	            re.append(" >").append(d.getName()).append("</option>");
	        }
	    }
        re.append("</select>");
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
		String tempalte=parameterHtmlType.getHtmlTemplate();
		String parameterName=parameter.getParameterName();
		String style = parameter.getStyle();
		String value=parameter.getValue();
		
		if(!ParameterHtmlType.inputHidden.equals(parameterHtmlType))
		{
			re=parameter.getTitle()+"：";	
		}
		if(!StringUtils.hasText(value)) value="";
		
		re+=MessageFormat.format(tempalte, value,parameterName,style);
		
		if(!ParameterHtmlType.inputHidden.equals(parameterHtmlType))
		{
			re+="<br>"+parameter.getDescription();
			re+="<br>";
		}
		
		return re;
	}
}
