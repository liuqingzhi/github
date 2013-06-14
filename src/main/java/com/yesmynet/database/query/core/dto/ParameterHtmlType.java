package com.yesmynet.database.query.core.dto;

/**
 * 表示参数的hmtl显示类型，如一个参数可能显示为单行文本框或多行文本框等
 * 
 * @author 刘庆志
 *
 */
//TODO:可以在这里定义参数在界面上显示时的字符串
public  enum ParameterHtmlType
{
	/**
	 * 单行文本框
	 */
	inputText("单行文本","<input type=''text'' name=''{1}'' style=''{2}'' value=''{0}'' >"),
	/**
	 * 多行文本框
	 */
	textArea("多行文本","<textarea name=''{1}'' style=''{2}''>{0}</textarea>"),
	/**
     * html中的hidden输入控件 
     */
    inputHidden("Hidden输入控件","<input type=''hidden'' name=''{1}''  style=''{2}'' value=''{0}'' >")
    ;
	/**
	 * 显示在界面上的类型名称
	 */
    private String title;
    /**
     * 显示本类型的参数时的html模板
     */
    private String htmlTemplate;
    private ParameterHtmlType(String title,String htmlTemplate)
    {
        this.title=title;
        this.htmlTemplate=htmlTemplate;
    }
    /**
     * 得到显示的名称
     * @return
     * @author 刘庆志
     */
    public String getTitle()
    {
        return title;
    }
    /**
     * 得到显示本类型的参数时的html模板
     *
     * @return
     */
	public String getHtmlTemplate() {
		return htmlTemplate;
	}
    
}