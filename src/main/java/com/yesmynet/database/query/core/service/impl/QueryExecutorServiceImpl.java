package com.yesmynet.database.query.core.service.impl;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;

import com.yesmynet.database.query.core.dto.Query;
import com.yesmynet.database.query.core.dto.QueryDefinition;
import com.yesmynet.database.query.core.dto.QueryReult;
import com.yesmynet.database.query.core.service.QueryExecutorService;
import com.yesmynet.database.query.dto.DataSourceConfig;

public class QueryExecutorServiceImpl implements QueryExecutorService
{

    public QueryReult executeQuery(Query query,
            QueryDefinition queryDefinition, DataSourceConfig datasource)
    {
        QueryReult re=null;
        re=query.doInQuery(datasource, queryDefinition);
        return re;
    }

}
