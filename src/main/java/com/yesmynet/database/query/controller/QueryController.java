package com.yesmynet.database.query.controller;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.yesmynet.database.query.core.dto.Parameter;
import com.yesmynet.database.query.core.dto.QueryDefinition;
import com.yesmynet.database.query.core.service.DataSourceService;
import com.yesmynet.database.query.core.service.QueryDefinitionService;
import com.yesmynet.database.query.core.service.QueryRenderService;
import com.yesmynet.database.query.dto.DataSourceConfig;

/**
 * 显示出查询的界面，并执行查询
 * @author 刘庆志
 *
 */
@Controller
public class QueryController
{
	@Resource(name = "dataSourceService")
	private DataSourceService dataSourceService;
	@Resource(name = "queryDefinitionService")
	private QueryDefinitionService queryDefinitionService;
	@Resource(name = "queryRenderService")
	private QueryRenderService queryRenderService;
	
	private enum SystemParameterName
	{
		/**
		 * 表示要查看或要执行的查询的Id
		 */
		QueryId("SystemQueryId"),
		/**
		 * 表示要执行查询的命令，当本参数有值，不管值是什么都表示要执行查询
		 */
		QueryExecute("SystemQueryExecute")
		;
		/**
		 * http参数名称
		 */
		private String paramerName;
		private SystemParameterName(String httpParamerName)
		{
			this.paramerName=httpParamerName;
		}
		public String getParamerName()
		{
			return paramerName;
		}
		
	}
	/**
	 * 显示查询的界面
	 * @param queryId
	 * @param model
	 * @return
	 */
	@RequestMapping(value = "/query.do")/*@RequestMapping(value="/query/{ownerId}/view.do", method=RequestMethod.GET)*/
	public String showQuery(HttpServletRequest request,Model model)
    {
		List<DataSourceConfig> allDataSources = dataSourceService.getDataSources();
		String queryHtml=getQueryShowHtml(request);
		
		//判断是否要查询；String command=request.getParameter("SystemQueryCommand");
		
		model.addAttribute("dataSources", allDataSources);
		model.addAttribute("queryHtml", queryHtml);
		
        return "showQuery";
    }
	/**
	 * 显示一个查询
	 * @param request
	 * @return
	 */
	private String getQueryShowHtml(HttpServletRequest request)
	{
		String queryId=request.getParameter(SystemParameterName.QueryId.getParamerName());
		
		
		QueryDefinition queryParameters = queryDefinitionService.getQueryParameters(queryId);
		setHttpParameterValue(queryParameters,request);
		String queryHtml = queryRenderService.getQueryHtml(queryParameters);
		
		return queryHtml;
	}
	/**
	 * 把httpRequest中请求的参数值设置到查询的参数中
	 * @param queryParameters
	 */
	private void setHttpParameterValue(QueryDefinition queryParameters,HttpServletRequest request)
	{
		List<Parameter> parameters = queryParameters.getParameters();
		if(!CollectionUtils.isEmpty(parameters))
		{
			for(Parameter p:parameters)
			{
				String parameterName = p.getParameterName();
				String parameterValue = request.getParameter(parameterName);
				p.setValue(parameterValue);
			}
		}
	}
	public DataSourceService getDataSourceService()
	{
		return dataSourceService;
	}
	public void setDataSourceService(DataSourceService dataSourceService)
	{
		this.dataSourceService = dataSourceService;
	}
	public QueryDefinitionService getQueryDefinitionService()
	{
		return queryDefinitionService;
	}
	public void setQueryDefinitionService(QueryDefinitionService queryDefinitionService)
	{
		this.queryDefinitionService = queryDefinitionService;
	}
	public QueryRenderService getQueryRenderService()
	{
		return queryRenderService;
	}
	public void setQueryRenderService(QueryRenderService queryRenderService)
	{
		this.queryRenderService = queryRenderService;
	}
	
}
