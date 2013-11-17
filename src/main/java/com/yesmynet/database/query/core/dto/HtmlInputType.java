package com.yesmynet.database.query.core.dto;

import java.util.List;

import org.springframework.util.StringUtils;

/**
 * 表示显示的html元素的类型，如：单行文本框、多行文本框、下拉框等.
 * @author liuqingzhi
 *
 * @param <T>
 */
public abstract class HtmlInputType<T> extends BaseDto{
	/**
	 * 表示html输入框的枚举
	 * @author liuqingzhi
	 *
	 */
	public enum InputTypeEnum
	{
		/**
		 * 单行文本框
		 */
		InputText("<input type=''text'' name=''{1}'' value=''{0}'' style=''{2}'' class=''{3}''/>"),
		/**
		 * 多行文本框
		 */
		TextArea("<textarea name=''{1}'' style=''{2}'' class=''{3}''>{0}</textarea>"),
		/**
	     * html中的hidden输入控件 
	     */
	    InputHidden("<input type=''hidden'' name=''{1}'' value=''{0}'' style=''{2}'' class=''{3}''/>"),
	    /**
	     * html中的select输入控件 
	     */
	    Select("<select name=''{1}''  style=''{2}'' class=''{3}''>{0}</select>"),
	    /**
		 * 单选radio
		 */
		Radio("<input type=''radio'' name=''{1}'' value=''{0}'' style=''{2}'' class=''{3}'' {5}>{4}</input>\n"),
		/**
		 * 多选checkbox
		 */
		Checkbox("<input type=''checkbox'' name=''{1}'' value=''{0}'' style=''{2}'' class=''{3}'' {5}>{4}</input>\n")
		;
		/**
		 * 生成html中时的模板
		 * 目前来看，模板中需要4个动态数据，按顺序依次是：value,name,style,calss，其中select比较特殊它要把所有选项作为value.
		 * radio checkbox多需要2个动态数据，就是最后多了个每个选项的文本、每个选项是否选中。
		 */
		private String htmlTemplate;
		private InputTypeEnum(String htmlTemplate)
		{
			this.htmlTemplate=htmlTemplate;
		}
		/**
		 * 得到生成html时的模板
		 * 目前来看，模板中需要3个动态数据，按顺序依次是：value,name,style,calss，其中select比较特殊
		 * 它要把所有选项作为value.
		 * @return
		 */
		public String getHtmlTemplate() {
			return htmlTemplate;
		}
		
	}
	/**
	 * 构造函数
	 * @param inputType
	 */
	public HtmlInputType(InputTypeEnum inputType) {
		super();
		this.inputType = inputType;
	}
	/**
	 * 默认参数名前面的前缀。
	 * 查询参数在运行时为了收集参数值，要在HttpRequest中取一个参数，如：request.getParameter("");
	 * 其中的参数名如果没有定义则使用默认参数，即本属性+id,形成如：param123这样的参数名称，如果自定义了参数名
	 * 则直接使用定义的参数名
	 */
	protected final String defaltNamePrifix="param";
	/**
	 * 输入框的类型
	 */
	protected InputTypeEnum inputType;
	/**
	 * 标题
	 */
	protected String title;
	/**
	 * 描述
	 */
	protected String description;
	/**
	 * 自定义参数名，如果本参数为空，则系统会使用默认规则生成参数名称。
	 * 表示本查询参数在运行时通过HttpRequest收集参数时，在HttpRequest中的参数名。
	 * 自定义参数名可以更方便的通过其它方式传参数，如：直接通过URL传参数。
	 * 本属性可以为null,表示使用默认的参数名
	 */
	protected String customName;
	/**
	 * 在页面上显示参数时的样式，如：可以使用css定义输入框的大小.
	 * 本参数只要style="..."，双引号里的内容，不要带上style=""，如下是好的样式：width:150px;font-size:24px;
	 */
	protected String style;
	/**
	 * 在页面上显示参数时的样式的class，
	 * 本参数只要class="..."，双引号里的内容，不要带上class=""。
	 */
	protected String styleClass;
	/**
	 * 参数运行时的值
	 */
	protected T value;
	/**
	 * 参数的可选值，对于文本框就是默认值，对于下拉框则可以有多个可选择值
	 */
	protected T OptionValues;
	/**
	 * 是否擦除提交的值，如果为true 表示擦除，即不回显
	 */
	protected Boolean eraseValue;
	
	public InputTypeEnum getInputType() {
		return inputType==null?inputType=InputTypeEnum.InputText:inputType;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getCustomName() {
		return customName;
	}
	public void setCustomName(String customName) {
		this.customName = customName;
	}
	public String getStyleClass() {
		return styleClass;
	}
	public void setStyleClass(String styleClass) {
		this.styleClass = styleClass;
	}
	public String getStyle() {
		return style;
	}
	public void setStyle(String style) {
		this.style = style;
	}
	public T getValue() {
		return value;
	}
	public void setValue(T value) {
		this.value = value;
	}
	public T getOptionValues() {
		return OptionValues;
	}
	public void setOptionValues(T optionValues) {
		OptionValues = optionValues;
	}
	public Boolean getEraseValue() {
		return eraseValue;
	}
	public void setEraseValue(Boolean eraseValue) {
		this.eraseValue = eraseValue;
	}
	/**
	 * 得到参数的html页面上显示的名称，先看customName，如果不空则返回，如果为空，则生成一个参数名返回。
	 * @param parameterDefine
	 * @return
	 */
	public String getParameterName() 
	{
		String re=getCustomName();
		if(!StringUtils.hasText(re))
		{
			re=defaltNamePrifix;
			re+=getId();
		}
		return re;
	}
	/**
	 * 转成html代码
	 * @return
	 */
	public abstract String toHtml();
	
}
