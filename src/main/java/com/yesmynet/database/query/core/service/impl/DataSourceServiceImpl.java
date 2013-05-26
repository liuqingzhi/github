package com.yesmynet.database.query.core.service.impl;

import java.util.List;

import org.springframework.util.CollectionUtils;

import com.yesmynet.database.query.core.service.DataSourceService;
import com.yesmynet.database.query.dto.DataSourceConfig;

public class DataSourceServiceImpl implements DataSourceService
{
	List<DataSourceConfig> dataSourceConfigList;
	public DataSourceConfig getDataSourceById(String id)
	{
		DataSourceConfig re=null;
		if(!CollectionUtils.isEmpty(dataSourceConfigList))
		{
			for(DataSourceConfig d:dataSourceConfigList)
			{
				if(d.getId().equals(id))
				{
					re=d;
					break;
				}	
			}
		}
		return re;
	}
	public List<DataSourceConfig> getDataSources()
	{
		return dataSourceConfigList;
	}
	public List<DataSourceConfig> getDataSourceConfigList()
	{
		return dataSourceConfigList;
	}
	public void setDataSourceConfigList(List<DataSourceConfig> dataSourceConfigList)
	{
		this.dataSourceConfigList = dataSourceConfigList;
	}

}
