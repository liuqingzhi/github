package com.yesmynet.database.query.core.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.springframework.orm.ibatis.support.SqlMapClientDaoSupport;
import org.springframework.util.CollectionUtils;

import com.yesmynet.database.query.core.dto.ParameterInput;
import com.yesmynet.database.query.core.dto.ParameterValidatorRecordDto;
import com.yesmynet.database.query.core.dto.QueryDefinition;

/**
 * 参数验证器定义器。
 * 在把一个{@link #ParameterValidator}验证器用到一个参数时，可能要定义一些
 * 验证时的数据，如：对于正则表达式验证器，要定义验证使用的正则表达式，对于数字
 * 范围的验证器，要定义验证时的最小值、最大值。 
 * @author 刘庆志
 *
 */
public abstract class ParameterValidatorDefine extends SqlMapClientDaoSupport 
{
	/**
	 * 得到验证器的Key
	 * @return
	 */
	public abstract String getValidatorType();
    /**
     * 得到验证器的名称。如：非空验证器、邮件地址验证器。
     * @return
     * @author 刘庆志
     */
	public abstract String getName();
    /**
     * 得到设置验证器的所有输入框，所有输入框的名称作为{@link #showHtml(Map)}和{@link #save(Map)}中的map中的key.
     * @return
     * @author 刘庆志
     */
	public abstract List<ParameterInput> getInputs();
    /**
     * 保存数据前先验证数据
     * @param validateRuleDatas 所有要保存的数据
     * @return true表示验证通过，可以保存；false表示验证不通过，不保存。
     */
    protected abstract boolean validParameterBeforeSave(Map<String,String> validateRuleDatas);
    /**
     * 得到参数使用本验证器的数据
     * @param parameterId
     * @return
     */
    protected ParameterValidatorRecordDto getValidateDatas(String parameterId)
    {
    	ParameterValidatorRecordDto re=null;
    	Map<String,Object> param=new HashMap<String,Object>();
    	param.put("parameterId", parameterId);
    	param.put("validatorType", getValidatorType());
    	
    	re=(ParameterValidatorRecordDto)this.getSqlMapClientTemplate().queryForObject("getParameterValidator", param);
    	return re;
    }
    /**
     * 保存验证器的设置。
     * @param validateRuleDatas 用户提交的所有的参数
     * @return 保存是否成功 
     * @author 刘庆志
     */
    public boolean save(Map<String,String> validateRuleDatas,String parameterId)
    {
        boolean re=validParameterBeforeSave(validateRuleDatas);
        if(re)
        {
        	Map<String,Object> param=new HashMap<String,Object>();
        	param.put("parameterId", parameterId);
        	param.put("validatorType", getValidatorType());
        	
        	this.getSqlMapClientTemplate().delete("deleteParameterValidatorData", param);
        	this.getSqlMapClientTemplate().delete("deleteParameterValidator", param);
        	
        	ParameterValidatorRecordDto parameterValidatorRecordDto=new ParameterValidatorRecordDto();
        	parameterValidatorRecordDto.setParameterId(parameterId);
        	parameterValidatorRecordDto.setValidatorType(getValidatorType());
        	this.getSqlMapClientTemplate().insert("saveParameterValidator", param);
        	Set<Entry<String,String>> entrySet = validateRuleDatas.entrySet();
        	for(Entry<String,String> entry:entrySet)
        	{
        		String key = entry.getKey();
        		String value = entry.getValue();
        		
        		Map<String,Object> param1=new HashMap<String,Object>();
            	param1.put("validatorId", parameterValidatorRecordDto.getId());
            	param1.put("dataKey", key);
            	param1.put("dataValue", value);
            	
        		this.getSqlMapClientTemplate().insert("saveParameterValidatorData", param1);
        	}
        }
        return re;
    }
    
}
