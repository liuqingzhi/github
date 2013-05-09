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
	 * 查询定义的所有参数
	 */
	private List<ParameterDefinition> parameters;

	public List<ParameterDefinition> getParameters()
	{
		return parameters;
	}

	public void setParameters(List<ParameterDefinition> parameters)
	{
		this.parameters = parameters;
	}
	
}
