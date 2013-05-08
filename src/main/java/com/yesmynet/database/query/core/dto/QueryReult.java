package com.yesmynet.database.query.core.dto;

import java.io.OutputStream;

/**
 * 表示查询结果的类。
 * @author 刘庆志
 *
 */
public class QueryReult extends BaseDto
{
	/**
	 * 要使用流输出的查询结果
	 */
	private OutputStream outputStream;
	/**
	 * 要直接显示的查询结果
	 */
	private String content;
}
