package com.yesmynet.database.query.core.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import groovy.lang.GroovyClassLoader;

import org.codehaus.groovy.control.CompilationFailedException;
import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.yesmynet.database.query.core.dto.Parameter;
import com.yesmynet.database.query.core.dto.Query;
import com.yesmynet.database.query.core.dto.QueryDefinition;
import com.yesmynet.database.query.core.service.QueryDefinitionService;

public class QueryDefinitionServiceImpl extends SqlMapClientDaoSupport implements QueryDefinitionService
{

	public QueryDefinition getQueryParameters(String queryId)
	{
		QueryDefinition re=null;
		if(!StringUtils.hasText(queryId))
		{
		    re=getDefaultQuery();
		}
		else
		{
		    re=(QueryDefinition)this.getSqlMapClientTemplate().queryForObject("getQueryDefinitionById", queryId);    
		}
		
		return re;
	}
	/**
	 * 得到默认查询。
	 * 当要没有指定要显示或查看哪个查询时，就得到默认查询。
	 * @return
	 * @author 刘庆志
	 */
	private QueryDefinition getDefaultQuery()
	{
	    QueryDefinition re=null;
	    
	    re=(QueryDefinition)this.getSqlMapClientTemplate().queryForObject("getQueryDefinitionDefault");  
	    
	    return re;
	}
	public Query getQueryInstance(String id) 
	{
		QueryDefinition queryDefinition=getQueryParameters(id);
		
		String javaCode = queryDefinition.getJavaCode();
		String notNullId=replaceNullId(id);
		javaCode="package com.yesmynet.database.query."+ notNullId +";"+javaCode;
		
		Query myObject;
        try
        {
            GroovyClassLoader gcl = new GroovyClassLoader();
            Class clazz = gcl.parseClass(javaCode, notNullId);
            Object aScript = clazz.newInstance();
            myObject = (Query) aScript;
        } catch (Exception e)
        {
            throw new RuntimeException(e);
        } 
		return myObject;
	}
	/**
	 * 对空的Id进行处理，以保证返回的一定不是空的，如果原来的id不是空的，则直接返回，否则返回一个随机的字符串
	 *TODO:还没有实现
	 * @param id
	 * @return
	 * @author 刘庆志
	 */
	private String replaceNullId(String id)
	{
	    String re=id;
	    if(!StringUtils.hasText(re))
	        re="aaa";
	    return re;
	}
    public void save(QueryDefinition queryDefinition)
    {
        String queryId=queryDefinition.getId();
        String sqlId="";
        
        sqlId=StringUtils.hasText(queryId)?"updateQueryDefinition":"insertQueryDefinition";
        if(StringUtils.hasText(queryId))
        {
            sqlId="updateQueryDefinition";
            this.getSqlMapClientTemplate().update(sqlId, queryDefinition);
        }
        else
        {
            //使用insert是为了得到新插入的数据的ID，使用update我试过了，得不到ID
            sqlId="insertQueryDefinition";
            this.getSqlMapClientTemplate().insert(sqlId, queryDefinition);
            
            queryId=queryDefinition.getId();//得到新插入数据的Id
        }
        
        List<Parameter> parameters = queryDefinition.getParameters();
        List<String> toDeleteParamIds=new ArrayList<String>();
        Map<String,Object> paramMap=new HashMap<String,Object>();
        
        paramMap.put("toDeleteParameterIds", toDeleteParamIds);
        paramMap.put("queryId", queryId);
        
        
        if(!CollectionUtils.isEmpty(parameters))
        {
            for(Parameter p:parameters)
            {
                toDeleteParamIds.add(p.getId());
            }
        }
        this.getSqlMapClientTemplate().update("deleteQueryParameter", paramMap);
        
        if(!CollectionUtils.isEmpty(parameters))
        {
            for(Parameter p:parameters)
            {
                sqlId=StringUtils.hasText(p.getId())?"updateQueryParameter":"insertQueryParameter";
                this.getSqlMapClientTemplate().update(sqlId, p);
            }
        }
        
    }

}
