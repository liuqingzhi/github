package com.yesmynet.database.query.controller;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.yesmynet.database.query.core.dto.Parameter;
import com.yesmynet.database.query.core.dto.ParameterHtmlType;
import com.yesmynet.database.query.core.dto.Query;
import com.yesmynet.database.query.core.dto.QueryDefinition;
import com.yesmynet.database.query.core.dto.QueryReult;
import com.yesmynet.database.query.core.service.DataSourceService;
import com.yesmynet.database.query.core.service.QueryDefinitionService;
import com.yesmynet.database.query.core.service.QueryExecutorService;
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
    /**
     * 数据源配置的service
     */
	@Resource(name = "dataSourceService")
	private DataSourceService dataSourceService;
	/**
	 * 得到一个查询的定义的service
	 */
	@Resource(name = "queryDefinitionService")
	private QueryDefinitionService queryDefinitionService;
	/**
	 * 显示一个查询的service
	 */
	@Resource(name = "queryRenderService")
	private QueryRenderService queryRenderService;
	/**
	 * 执行一个查询的Service
	 */
	@Resource(name = "queryExecutorService")
	private QueryExecutorService queryExecutorService;
	/**
	 * 系统使用的request参数名，在查询定义时，应该不要让查询的参数名与
	 * 这里使用的参数名有一样的。
	 * @author 刘庆志
	 *
	 */
	private enum SystemParameterName
	{
		/**
		 * 表示要查看或要执行的查询的Id
		 */
		QueryId("SystemQueryId"),
		/**
		 * 表示要执行查询的命令，当本参数有值，不管值是什么都表示要执行查询
		 */
		QueryExecute("SystemQueryExecute"),
		/**
         * 表示要使用哪个数据源
         * 在执行查询时，可以从多个数据源中选择一个。
         */
        DataSourceId("SystemDataSourceId")
        
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
	 * @throws IOException 
	 */
	@RequestMapping(value = "/query.do")/*@RequestMapping(value="/query/{ownerId}/view.do", method=RequestMethod.GET)*/
	public String showQuery(HttpServletRequest request,HttpServletResponse response,Model model) throws IOException
    {
	    String queryId=request.getParameter(SystemParameterName.QueryId.getParamerName());//要使用的查询的ID
	    String queryExecute=request.getParameter(SystemParameterName.QueryExecute.getParamerName());//是否要执行查询
        String dataSourceId=request.getParameter(SystemParameterName.DataSourceId.getParamerName());//使用的数据源

	    boolean executeQuery=(queryExecute==null)?false:true;//是否要执行查询
	    
	    List<DataSourceConfig> allDataSources = dataSourceService.getDataSources();
	    
	    QueryDefinition queryParameters = queryDefinitionService.getQueryParameters(queryId);
        setHttpParameterValue(queryParameters,request);
        String queryHtml = queryRenderService.getQueryHtml(queryParameters,allDataSources,dataSourceId);
        QueryReult queryResult=null;
		
        Exception queryExecuteException=null;
		try
        {
            if(executeQuery)
            {
                /*要执行查询*/
                Query queryInstance = queryDefinitionService.getQueryInstance(queryId);
                DataSourceConfig dataSourceById = dataSourceService.getDataSourceById(dataSourceId);
                queryResult = queryExecutorService.executeQuery(queryInstance, queryParameters,dataSourceById);
            }
            
            if(queryResult!=null && queryResult.getContentInputStream()!=null)
            {
                FileCopyUtils.copy(queryResult.getContentInputStream(), response.getOutputStream());
                return null;
            }
        } catch (Exception e)
        {
            queryExecuteException=e;
            e.printStackTrace();
        }
		String queryExecuteExceptionString = printException(queryExecuteException);
		
		
		model.addAttribute("dataSources", allDataSources);
		model.addAttribute("queryHtml", queryHtml);
		model.addAttribute("queryResult", queryResult);
		model.addAttribute("queryExecuteException", queryExecuteException);
		model.addAttribute("queryExecuteExceptionString", queryExecuteExceptionString);
		
        return "showQuery";
    }
	/**
	 * 编辑一个查询
	 * @param request
	 * @param response
	 * @param model
	 * @return
	 * @author 刘庆志
	 */
	@RequestMapping(value = "/editQuery.do")
	public String editQuery(HttpServletRequest request,HttpServletResponse response,QueryDefinition queryDefinition,Model model)
	{
	    String queryId=queryDefinition.getId();
	    QueryDefinition queryParameters=null;
	    
	    if(StringUtils.hasText(queryId))
	    {
	        queryParameters = queryDefinitionService.getQueryParameters(queryId);
	    }
	    
	    model.addAttribute("allHtmlTypes", ParameterHtmlType.values());
	    model.addAttribute("query", queryParameters);
	    return "editQuery";
	}
	/**
	 * 保存查询
	 * @param request
	 * @param response
	 * @param queryDefinition
	 * @param model
	 * @return
	 * @author 刘庆志
	 */
	@RequestMapping(value = "/saveQuery.do")
    public String saveQuery(HttpServletRequest request,HttpServletResponse response,QueryDefinition queryDefinition,Model model)
    {
        String queryId="";
        QueryDefinition queryParameters=null;
        
        queryDefinitionService.save(queryDefinition);
        
        queryId=queryDefinition.getId();
        
        model.addAttribute("query", queryParameters);
        return "redirect:/editQuery.do?id="+queryId;
    }
	private String printException(Exception e)
	{
	    String re="";
	    if(e!=null)
	        re=ExceptionUtils.getStackTrace(e);
	    
	    return re;
	}
	/**
	 * 把httpRequest中请求的参数值设置到查询的参数中
	 * @param queryParameters
	 */
	private void setHttpParameterValue(QueryDefinition queryParameters,HttpServletRequest request)
	{
	    if(queryParameters!=null)
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
    public QueryExecutorService getQueryExecutorService()
    {
        return queryExecutorService;
    }
    public void setQueryExecutorService(QueryExecutorService queryExecutorService)
    {
        this.queryExecutorService = queryExecutorService;
    }
	
}
