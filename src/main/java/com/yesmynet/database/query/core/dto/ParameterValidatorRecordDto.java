package com.yesmynet.database.query.core.dto;

import java.util.List;
import java.util.Map;

import org.springframework.util.CollectionUtils;

import com.yesmynet.database.query.core.service.ParameterValidatorDefine;

/**
 * 表示在数据库中一个参数要使用一个验证器的记录。
 * @author liuqingzhi
 *
 */
public class ParameterValidatorRecordDto extends BaseDto{
	/**
	 * 参数ID
	 */
	private String parameterId;
	/**
	 * 验证器的Type
	 * @seee com.yesmynet.database.query.core.service.ParameterValidatorDefine#get#getValidatorType()
	 * 
	 */
	private String validatorType;
	/**
	 * 验证器使用的数据
	 */
	private Map<String,String> validatorDatas;
	/**
	 * 对应的参数验证器定义
	 */
	private ParameterValidatorDefine parameterValidatorDefine;
	/**
	 * 在界面上显示验证器时的html
	 * @return
	 */
	public String getShowHtml()
	{
		String re=null;
		if(parameterValidatorDefine!=null)
		{
			List<ParameterInput> inputs = parameterValidatorDefine.getInputs();
	        StringBuilder sb=new StringBuilder();
	        if(!CollectionUtils.isEmpty(inputs))
	        {
	        	for(ParameterInput p:inputs)
	        	{
	        		String string = validatorDatas.get(p.getParameterName());
	        		p.setValue(new String[]{string});
	        		sb.append(p.toHtml());
	        	}
	        }
	        
	        re=sb.toString();
		}
			
		return re;
	}
	public String getParameterId() {
		return parameterId;
	}
	public void setParameterId(String parameterId) {
		this.parameterId = parameterId;
	}
	public String getValidatorType() {
		return validatorType;
	}
	public void setValidatorType(String validatorType) {
		this.validatorType = validatorType;
	}
	public Map<String, String> getValidatorDatas() {
		return validatorDatas;
	}
	public void setValidatorDatas(Map<String, String> validatorDatas) {
		this.validatorDatas = validatorDatas;
	}
	public ParameterValidatorDefine getParameterValidatorDefine() {
		return parameterValidatorDefine;
	}
	public void setParameterValidatorDefine(
			ParameterValidatorDefine parameterValidatorDefine) {
		this.parameterValidatorDefine = parameterValidatorDefine;
	}
}
