package com.yesmynet.database.query.controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
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
import com.yesmynet.database.query.core.service.ParameterValidatorDefine;
import com.yesmynet.database.query.core.service.QueryDefinitionService;
import com.yesmynet.database.query.core.service.QueryExecutorService;
import com.yesmynet.database.query.core.service.QueryRenderService;
import com.yesmynet.database.query.dto.DataSourceConfig;
import com.yesmynet.database.query.dto.Role;
import com.yesmynet.database.query.dto.SystemParameterName;
import com.yesmynet.database.query.dto.User;

/**
 * 显示出查询的界面，并执行查询
 * @author 刘庆志
 *
 */
@Controller
public class QueryController
{
	Logger logger=LoggerFactory.getLogger(this.getClass());
	/**
	 * 表示查询参数的ID是空的值 ，因为在客户端添加的查询参数时，使用spring mvc数据绑定时，绑定到List
	 * 中直接使用客户端提交的数据的序号，如：当提交parameters[1５].htmlType参数时，会导致 生成一个有16个
	 * 元素的List,List前15个对象没有设置属性，为了表示只有第16个元素是有效的数据，我在客户端提交时，把ID设置成
	 * 本属性的值，提交上来再检查id的值，如果等于本属性的值，则再把ID赋为null.
	 */
	public static final String QueryParameterNullId="-1";
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
	 * 显示查询的界面
	 * @param queryId
	 * @param model
	 * @return
	 * @throws IOException 
	 */
	@RequestMapping(value = "/query.do")/*@RequestMapping(value="/query/{ownerId}/view.do", method=RequestMethod.GET)*/
	public String showQuery(HttpServletRequest request,HttpServletResponse response,Model model) throws IOException
    {
		String viewName="showQuery";
	    String queryId=request.getParameter(SystemParameterName.QueryId.getParamerName());//要使用的查询的ID
	    String queryExecute=request.getParameter(SystemParameterName.QueryExecute.getParamerName());//是否要执行查询
        String dataSourceId=request.getParameter(SystemParameterName.DataSourceId.getParamerName());//使用的数据源

	    boolean executeQuery=(queryExecute==null)?false:true;//是否要执行查询
	    User currentUser = getCurrentUser();
	    List<DataSourceConfig> allDataSources = dataSourceService.getDataSources(currentUser);
	    
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
                DataSourceConfig dataSourceById = dataSourceService.getDataSourceById(dataSourceId,currentUser);
                if(dataSourceById==null)
                {
                	throw new RuntimeException("您无权操作该数据源");
                }
                queryResult = queryExecutorService.executeQuery(queryInstance, queryParameters,dataSourceById);
            }
            
            if(queryResult!=null )
            {
            	if(queryResult.getContentInputStream()!=null)
                {	
            		FileCopyUtils.copy(queryResult.getContentInputStream(), response.getOutputStream());
            		return null;
                }
            	else if(queryResult.getOnlyShowContent()!=null && queryResult.getOnlyShowContent())
            	{
            		viewName="showQueryOnlyResult";	
            	}
                	
            }
            
        } catch (Exception e)
        {
            queryExecuteException=e;
            logger.debug("执行用户请求的sql出错了",e);
        }
		String queryExecuteExceptionString = printException(queryExecuteException);
		
		
		model.addAttribute("dataSources", allDataSources);
		model.addAttribute("queryHtml", queryHtml);
		model.addAttribute("queryResult", queryResult);
		model.addAttribute("queryExecuteException", queryExecuteException);
		model.addAttribute("queryExecuteExceptionString", queryExecuteExceptionString);
		
        return viewName;
    }
	
	private User getCurrentUser()
	{
		User re=new User();
		SecurityContext securityContext = SecurityContextHolder.getContext();
		Authentication authentication = securityContext.getAuthentication();
		String userName=((org.springframework.security.core.userdetails.User)authentication.getPrincipal()).getUsername();
		Collection<? extends GrantedAuthority> authorities = securityContext.getAuthentication().getAuthorities();
		
		
		
		List<Role> roles=new ArrayList<Role>();
		if(!CollectionUtils.isEmpty(authorities))
		{
			for(GrantedAuthority g:authorities)
			{
				String authority = g.getAuthority();
				Role role=new Role();
				role.setRoleCode(authority);
				
				roles.add(role);
			}
			
		}
		re.setLoginName(userName);
		re.setRoles(roles);
		
		
		return re;
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
	    List<ParameterValidatorDefine> allValidatorDefines = queryDefinitionService.getAllValidatorDefines();
	    
	    model.addAttribute("allHtmlTypes", ParameterHtmlType.values());
	    model.addAttribute("query", queryParameters);
	    model.addAttribute("allValidatorDefines",allValidatorDefines);
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
        List<Parameter> parameters = queryDefinition.getParameters();
        
        
        trimParameterList(parameters);
        replaceParameterNullIdParameterList(parameters);
        queryDefinitionService.save(queryDefinition);
        
        queryId=queryDefinition.getId();
        
        model.addAttribute("query", queryParameters);
        return "redirect:/editQuery.do?id="+queryId;
    }
	/**
	 * 把查询参数中空的数据删除。
	 * 因为从客户端提交上来的查询参数列表是一个List，spring mvc在绑定数据时，直接根据提交的数据的序号生成List，如：
	 * 对于客户端提交的参数：parameters[0].htmlType，parameters[15].htmlType，会直接生成一个
	 * List，包含16个元素，事实上，此时只提交了2个查询参数，中间的1到14的元素应该删除。
	 * @param parameters
	 */
	private void trimParameterList(List<Parameter> parameters)
	{
		if(!CollectionUtils.isEmpty(parameters))
		{
			List<Parameter> toRemoveParameters=new ArrayList<Parameter>();
			for(Parameter p:parameters)
			{
				if(!StringUtils.hasText(p.getId()))
				{
					toRemoveParameters.add(p);
				}
			}
			parameters.removeAll(toRemoveParameters);
		}
	}
	/**
	 * 把查询参数中ID应该为空的设置为null
	 * @param parameters
	 */
	private void replaceParameterNullIdParameterList(List<Parameter> parameters)
	{
		if(!CollectionUtils.isEmpty(parameters))
		{
			for(Parameter p:parameters)
			{
				if(QueryParameterNullId.equals(p.getId()))
				{
					p.setId(null);
				}
			}
		}
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
	                String parameterName = p.getParameterInput().getParameterName();
	                String[] parameterValue = request.getParameterValues(parameterName);
	                p.getParameterInput().setValue(parameterValue);
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
