package com.yesmynet.database.query.core.dto;

import java.io.InputStream;
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
	private InputStream contentInputStream;
	/**
	 * 要直接显示的查询结果
	 */
	private String content;
    public InputStream getContentInputStream()
    {
        return contentInputStream;
    }
    public void setContentInputStream(InputStream contentInputStream)
    {
        this.contentInputStream = contentInputStream;
    }
    public String getContent()
    {
        return content;
    }
    public void setContent(String content)
    {
        this.content = content;
    }
}
