package com.yesmynet.database.query.core.dto;
/**
 * 查询参数的定义和该参数对应的值
 * @author 刘庆志
 *
 */
public class ParameterWithValue extends BaseDto
{
	/**
	 * 参数的定义
	 */
	private ParameterDefinition parameterDefine;
	/**
	 * 参数值
	 */
	private String value;
}
