package com.yesmynet.database.query.core.dto;
/**
 * 表示查询参数的定义。
 * 查询有两个阶段:1、定义阶段，2、运行阶段。当定义查询时，可以定义查询所使用的参数，例如：定义一个查询有
 * 多少个参数，每个参数在展示时使用单行文本框还是多行文本框，是否必填等；在运行阶段，这些参数的值被从界面上收集来
 * 并传给查询使用。
 * @author 刘庆志
 *
 */
public class ParameterDefinition extends BaseDto
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 默认参数名前面的前缀。
	 * 查询参数在运行时为了收集参数值，要在HttpRequest中取一个参数，如：request.getParameter("");
	 * 其中的参数名如果没有定义则使用默认参数，即本属性+id,形成如：param123这样的参数名称，如果自定义了参数名
	 * 则直接使用定义的参数名
	 */
	private String defaltNamePrifix="param";
	/**
	 * 参数名，表示本查询参数在运行时通过HttpRequest收集参数时，在HttpRequest中的参数名。
	 * 自定义参数名可以更方便的通过其它方式传参数，如：直接通过URL传参数。
	 * 本属性可以为null,表示使用默认的参数名
	 */
	private String name;
}
