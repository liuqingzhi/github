package com.yesmynet.database.query.core.dto;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;

/**
 * 表示一个查询在运行时要执行的数据库操作。
 * 查询有两个阶段:1、定义阶段，2、运行阶段。当定义查询时，可以定义查询所使用的参数，例如：定义一个查询有
 * 多少个参数，每个参数在展示时使用单行文本框还是多行文本框，是否必填等；在运行阶段，这些参数的值被从界面上收集来
 * 并传给查询使用。
 * 
 * @author 刘庆志
 *
 */
public interface Query
{
	/**
	 * 要在数据库中执行的操作。
	 * @param jdbcTemplate 利用本参数操作数据库
	 * @return
	 */
	public QueryReult doInQuery(JdbcTemplate jdbcTemplate);
}
