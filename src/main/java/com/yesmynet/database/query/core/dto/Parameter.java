package com.yesmynet.database.query.core.dto;
/**
 * 表示一个查询参数。
 * 查询有两个阶段:1、定义阶段，2、运行阶段。当定义查询时，可以定义查询所使用的参数，例如：定义一个查询有
 * 多少个参数，每个参数在展示时使用单行文本框还是多行文本框，是否必填等；在运行阶段，这些参数的值被从界面上收集来
 * 并传给查询使用。
 * @author 刘庆志
 *
 */
public class Parameter extends BaseDto
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 标题
	 */
	private String title;
	/**
	 * 描述
	 */
	private String description;
	/**
	 * 参数显示时使用的html类型，如：一个单行文本框或一个多行文本框。
	 */
	private ParameterHtmlType htmlType;
	/**
	 * 默认参数名前面的前缀。
	 * 查询参数在运行时为了收集参数值，要在HttpRequest中取一个参数，如：request.getParameter("");
	 * 其中的参数名如果没有定义则使用默认参数，即本属性+id,形成如：param123这样的参数名称，如果自定义了参数名
	 * 则直接使用定义的参数名
	 */
	private String defaltNamePrifix="param";
	/**
	 * 自定义参数名，如果本参数为空，则系统会使用默认规则生成参数名称。
	 * 表示本查询参数在运行时通过HttpRequest收集参数时，在HttpRequest中的参数名。
	 * 自定义参数名可以更方便的通过其它方式传参数，如：直接通过URL传参数。
	 * 本属性可以为null,表示使用默认的参数名
	 */
	private String customName;
	/**
	 * 参数运行时的值
	 */
	private String value;
	public String getTitle()
	{
		return title;
	}
	public void setTitle(String title)
	{
		this.title = title;
	}
	public String getDescription()
	{
		return description;
	}
	public void setDescription(String description)
	{
		this.description = description;
	}
	/**
	 * 参数显示时的html类型，如:一个文本框或者一个多行文本框。
	 * @author 刘庆志
	 *
	 */
	public ParameterHtmlType getHtmlType()
	{
		return htmlType;
	}
	public void setHtmlType(ParameterHtmlType htmlType)
	{
		this.htmlType = htmlType;
	}
	public String getDefaltNamePrifix()
	{
		return defaltNamePrifix;
	}
	public void setDefaltNamePrifix(String defaltNamePrifix)
	{
		this.defaltNamePrifix = defaltNamePrifix;
	}
	public String getCustomName()
	{
		return customName;
	}
	public void setCustomName(String customName)
	{
		this.customName = customName;
	}
	public String getValue()
	{
		return value;
	}
	public void setValue(String value)
	{
		this.value = value;
	}
	
}
