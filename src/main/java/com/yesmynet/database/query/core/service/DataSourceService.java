package com.yesmynet.database.query.core.service;

import java.util.List;

import javax.sql.DataSource;

import com.yesmynet.database.query.dto.DataSourceConfig;
import com.yesmynet.database.query.dto.User;

public interface DataSourceService
{
	/**
	 * 根据 ID得到得到数据源
	 * @param id
	 * @return
	 */
	public DataSourceConfig getDataSourceById(String id,User user);
	/**
	 * 得到系统中配置的所有数据源
	 * @return
	 */
	public List<DataSourceConfig> getDataSources(User user);
	
}
