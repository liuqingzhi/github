package com.yesmynet.database.query.core.dto;
/**
 * 查询参数的定义和该参数对应的值
 * @author 刘庆志
 *
 */
public class ParameterRuntime extends BaseDto
{
	/**
	 * 参数的定义
	 */
	private ParameterDefinition parameterDefine;
	/**
	 * 参数值
	 */
	private String value;
	/**
	 * 用来在html中显示这个参数的html代码,如,可能显示为:<input ...>
	 */
	private String renderByHtml;
	public ParameterDefinition getParameterDefine()
	{
		return parameterDefine;
	}
	public void setParameterDefine(ParameterDefinition parameterDefine)
	{
		this.parameterDefine = parameterDefine;
	}
	public String getValue()
	{
		return value;
	}
	public void setValue(String value)
	{
		this.value = value;
	}
	public String getRenderByHtml()
	{
		return renderByHtml;
	}
	public void setRenderByHtml(String renderByHtml)
	{
		this.renderByHtml = renderByHtml;
	}
	
}
