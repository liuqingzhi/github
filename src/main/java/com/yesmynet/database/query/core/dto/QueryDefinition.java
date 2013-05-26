package com.yesmynet.database.query.core.dto;

import java.util.List;

/**
 * 表示定义一个查询。
 * 查询有两个阶段:1、定义阶段，2、运行阶段。当定义查询时，可以定义查询所使用的参数，例如：定义一个查询有
 * 多少个参数，每个参数在展示时使用单行文本框还是多行文本框，是否必填等；在运行阶段，这些参数的值被从界面上收集来
 * 并传给查询使用。
 * 
 * @author 刘庆志
 *
 */
public class QueryDefinition  extends BaseDto
{
	/**
	 * 名称
	 */
	private String name;
	/**
	 * 描述
	 */
	private String description;
	/**
	 * 查询定义的所有参数
	 */
	private List<Parameter> parameters;
	/**
	 * 显示查询定义后要额外显示的html
	 */
	private String afterParameterHtml;
	/**
	 * 是否显示“执行查询”的按钮
	 */
	private Boolean showExecuteButton;
	/**
	 * 定义查询的java代码
	 */
	private String javaCode;
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name = name;
	}
	public String getDescription()
	{
		return description;
	}
	public void setDescription(String description)
	{
		this.description = description;
	}
	public List<Parameter> getParameters()
	{
		return parameters;
	}
	public void setParameters(List<Parameter> parameters)
	{
		this.parameters = parameters;
	}
	public String getAfterParameterHtml()
	{
		return afterParameterHtml;
	}
	public void setAfterParameterHtml(String afterParameterHtml)
	{
		this.afterParameterHtml = afterParameterHtml;
	}
    public Boolean getShowExecuteButton()
    {
        return showExecuteButton;
    }
    public void setShowExecuteButton(Boolean showExecuteButton)
    {
        this.showExecuteButton = showExecuteButton;
    }
	public String getJavaCode()
	{
		return javaCode;
	}
	public void setJavaCode(String javaCode)
	{
		this.javaCode = javaCode;
	}
}
