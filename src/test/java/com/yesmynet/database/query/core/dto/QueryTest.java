package com.yesmynet.database.query.core.dto;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import junit.framework.TestCase;

public class QueryTest extends TestCase
{

    protected void setUp() throws Exception
    {
        super.setUp();
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    public void testDoInQuery()
    {
        fail("Not yet implemented");
    }

    
    private class QueryTestDef implements Query
    {

        public QueryReult doInQuery(JdbcTemplate jdbcTemplate,
                QueryDefinition queryDefinition)
        {
            Gson gson = new GsonBuilder()
            //.setExclusionStrategies(MyExclusionStrategy)
            .serializeNulls()
            .create();
            
            QueryReult re=new QueryReult();
            String sql="select * From m_sys_query";
            
            List<Map<String,Object>> queryForList = jdbcTemplate.queryForList(sql);
            
            Type type = new TypeToken<Collection<Map<String,Object>>>(){}.getType();
            String json=gson.toJson(queryForList, type);
            
            re.setContent(json);
            return re;
        }

        
        
    }
    
}
