package com.yesmynet.database.query.core.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.util.CollectionUtils;

import com.yesmynet.database.query.core.service.DataSourceService;
import com.yesmynet.database.query.dto.DataSourceConfig;
import com.yesmynet.database.query.dto.Role;
import com.yesmynet.database.query.dto.User;

public class DataSourceServiceImpl implements DataSourceService
{
	List<DataSourceConfig> dataSourceConfigList;
	public DataSourceConfig getDataSourceById(String id,User user)
	{
		DataSourceConfig re=null;
		if(!CollectionUtils.isEmpty(dataSourceConfigList))
		{
			for(DataSourceConfig d:dataSourceConfigList)
			{
				if(d.getId().equals(id))
				{
					if(isUserCanUseDatasourceConfig(d,user))
					{
						re=d;
						break;	
					}
					
				}	
			}
		}
		return re;
	}
	/**
	 * 判断用户是否有权操作指定的数据源配置
	 */
	private boolean isUserCanUseDatasourceConfig(DataSourceConfig datasource,User user)
	{
		boolean re=false;
		List<Role> permitRoles = datasource.getPermitRoles();
		
		
		if(CollectionUtils.isEmpty(permitRoles))
		{	
			re=true;
		}
		else
		{
			List<Role> roles = user.getRoles();
			for(Role r:permitRoles)
			{
				if(!CollectionUtils.isEmpty(roles))
				{
					for(Role ur:roles)
					{
						if (r.getRoleCode().equals(ur.getRoleCode()))
						{
							re=true;
							break;
						}
					}
				}
				if(re)
					break;
			}
		}
		
		
		return re;
	}
	public List<DataSourceConfig> getDataSources(User user)
	{
		List<DataSourceConfig> re=new ArrayList<DataSourceConfig>();
		if(!CollectionUtils.isEmpty(dataSourceConfigList))
		{
			for(DataSourceConfig d:dataSourceConfigList)
			{
				if(isUserCanUseDatasourceConfig(d,user))
				{
					re.add(d);
				}
			}
		}
		return re;
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
