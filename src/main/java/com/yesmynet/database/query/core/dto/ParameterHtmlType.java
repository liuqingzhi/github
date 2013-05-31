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
	inputText("单行文本"),
	/**
	 * 多行文本框
	 */
	textArea("多行文本"),
	/**
     * html中的hidden输入控件 
     */
    inputHidden("Hidden输入控件")
    ;
	/**
	 * 显示在界面上的类型名称
	 */
    private String title;
    private ParameterHtmlType(String title)
    {
        this.title=title;
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
    
}