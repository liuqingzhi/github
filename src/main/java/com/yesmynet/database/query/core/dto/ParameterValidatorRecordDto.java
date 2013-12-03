package com.yesmynet.database.query.core.dto;

import java.util.Map;

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
}
