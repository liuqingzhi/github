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
import com.yesmynet.database.query.core.dto.ParameterValidatorRecordDto;
import com.yesmynet.database.query.core.dto.Query;
import com.yesmynet.database.query.core.dto.QueryDefinition;
import com.yesmynet.database.query.core.service.ParameterValidatorDefine;
import com.yesmynet.database.query.core.service.QueryDefinitionService;
import com.yesmynet.database.query.service.QueryDefaultImpl;

public class QueryDefinitionServiceImpl extends SqlMapClientDaoSupport implements QueryDefinitionService
{
	/**
	 * 默认查询的实例类，当没有指定query的ID时会使用本默认查询
	 */
	private QueryDefaultImpl defaultQueryInstance;
	/**
	 * 所有参数验证器的定义
	 */
	private List<ParameterValidatorDefine> parameterValidatorDefineList;
	public QueryDefinition getQueryParameters(String queryId)
	{
		QueryDefinition re=null;
		if(StringUtils.hasText(queryId))
		{
		    re=(QueryDefinition)this.getSqlMapClientTemplate().queryForObject("getQueryDefinitionById", queryId);
		    settingParameterValidatorDefine(re);
		}
		else
		{
		    //re=getDefaultQuery();
			re=defaultQueryInstance.getQueryDefinition();
		}
		return re;
	}
	/**
	 * 设置参数验证器
	 * @param queryDefinition
	 */
	private void settingParameterValidatorDefine(QueryDefinition queryDefinition)
	{
		List<Parameter> parameters = queryDefinition.getParameters();
		if(!CollectionUtils.isEmpty(parameters))
		{
			for(Parameter parameter:parameters)
			{
				String id = parameter.getId();
				List<ParameterValidatorRecordDto> parameterValidators = getParameterValidators(id);
				
				parameter.setParameterValidatorRecordDtos(parameterValidators);
			}
		}	
	}
	/**
	 * 根据参数ID得到参数使用的所有验证器定义
	 * @param parameterId 参数ID
	 * @return
	 */
	private List<ParameterValidatorRecordDto> getParameterValidators(String parameterId)
	{
		Map<String,String> params=new HashMap<String,String>();
		params.put("parameterId", parameterId);
		List<ParameterValidatorRecordDto> queryForList = this.getSqlMapClientTemplate().queryForList("getParameterValidator",params);
		if(!CollectionUtils.isEmpty(queryForList))
		{
			for(ParameterValidatorRecordDto validator:queryForList)
			{
				String id = validator.getId();
				Map<String, String> parameterValidatorDatas = getParameterValidatorDatas(id);
				ParameterValidatorDefine parameterValidatorDefineByType = getParameterValidatorDefineByType(validator.getValidatorType());
				
				validator.setValidatorDatas(parameterValidatorDatas);
				validator.setParameterValidatorDefine(parameterValidatorDefineByType);
			}
		}
		return queryForList;
	}
	/**
	 * 得到参数验证器使用的数据
	 * @param queryDefinition
	 */
	private Map<String,String> getParameterValidatorDatas(String vallidatorId)
	{
		Map<String,String> re=new HashMap<String,String>();
		Map<String,String> params=new HashMap<String,String>();
		params.put("parameterValidatorId", vallidatorId);
		List<Map<String,String>> queryForList = this.getSqlMapClientTemplate().queryForList("getParameterValidatorData",params);
		if(!CollectionUtils.isEmpty(queryForList))
		{
			for(Map<String,String> map:queryForList)
			{
				String key = map.get("DATA_KEY");
				String value = map.get("DATA_VALUE");
				
				re.put(key, value);
			}
		}
		return re;
	}
	/**
	 * 根据验证器类型得到验证器定义
	 * @param type
	 * @return
	 */
	private ParameterValidatorDefine getParameterValidatorDefineByType(String type)
	{
		ParameterValidatorDefine re=null;
		if(StringUtils.hasText(type) && !CollectionUtils.isEmpty(parameterValidatorDefineList))
		{
			for(ParameterValidatorDefine v:parameterValidatorDefineList)
			{
				if(type.equals(v.getValidatorType()))
				{
					re=v;
					break;
				}
			}
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
	    if(StringUtils.hasText(id))
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
	    else
	    {
	        return defaultQueryInstance; 
	    }
		
	}
	@Override
	public List<ParameterValidatorDefine> getAllValidatorDefines() {
		return parameterValidatorDefineList;
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
            	if(StringUtils.hasText(p.getId()))
            		toDeleteParamIds.add(p.getId());
            }
        }
        this.getSqlMapClientTemplate().update("deleteQueryParameterByNotIn", paramMap);
        
        if(!CollectionUtils.isEmpty(parameters))
        {
            for(Parameter p:parameters)
            {
                p.setQueryDefinition(queryDefinition);
                sqlId=StringUtils.hasText(p.getId())?"updateQueryParameter":"insertQueryParameter";
                this.getSqlMapClientTemplate().update(sqlId, p);
            }
        }
        
    }
	public QueryDefaultImpl getDefaultQueryInstance() {
		return defaultQueryInstance;
	}
	public void setDefaultQueryInstance(QueryDefaultImpl defaultQueryInstance) {
		this.defaultQueryInstance = defaultQueryInstance;
	}
	public List<ParameterValidatorDefine> getParameterValidatorDefineList() {
		return parameterValidatorDefineList;
	}
	public void setParameterValidatorDefineList(
			List<ParameterValidatorDefine> parameterValidatorDefineList) {
		this.parameterValidatorDefineList = parameterValidatorDefineList;
	}
	

}
