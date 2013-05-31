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
	inputText,
	/**
	 * 多行文本框
	 */
	textArea,
	/**
     * html中的hidden输入控件 
     */
    inputHidden
    ;
    
}