package com.yesmynet.database.query.core.dto;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import junit.framework.TestCase;

public class QueryTest extends TestCase
{
    private ApplicationContext context;
    private JdbcTemplate jdbcTemplate;
    protected void setUp() throws Exception
    {
        super.setUp();
        
        context = new ClassPathXmlApplicationContext("spring/*.xml");// /spring/spring-base.xml");
        jdbcTemplate =(JdbcTemplate)context.getBean("jdbcTemplate");
        
        jdbcTemplate.execute("delete from sc.m_sys_query");
        jdbcTemplate.execute("delete from sc.m_sys_query_parameter");
        
        
        String sql="insert into sc.m_sys_query (name,description,after_Parameter_Html,show_Execute_Button,system_init_create,system_default,finished,java_code)\n"+
        "values\n"+
        "('数据库查询','系统默认的查询','显示参数后的html',1,1,1,1,?)\n"+
        "";
        String javacode="import java.lang.reflect.Type;\n"+
                "import java.util.Collection;\n"+
                "import java.util.HashMap;\n"+
                "import java.util.List;\n"+
                "import java.util.Map;\n"+
                "\n"+
                "import org.springframework.jdbc.core.JdbcTemplate;\n"+
                "\n"+
                "import com.google.gson.Gson;\n"+
                "import com.google.gson.GsonBuilder;\n"+
                "import com.google.gson.reflect.TypeToken;\n"+
                "import com.yesmynet.database.query.core.dto.*;\n"+
                
                "class QueryTestDef implements Query\n"+
                "    {\n"+
                "\n"+
                "        public QueryReult doInQuery(JdbcTemplate jdbcTemplate,\n"+
                "                QueryDefinition queryDefinition)\n"+
                "        {\n"+
                "            Gson gson = new GsonBuilder()\n"+
                "            //.setExclusionStrategies(MyExclusionStrategy)\n"+
                "            .serializeNulls()\n"+
                "            .create();\n"+
                "            \n"+
                "            QueryReult re=new QueryReult();\n"+
                "            List<Parameter> parameters = queryDefinition.getParameters();\n"+
                "            Map<String,Parameter> parameterMap=new HashMap<String,Parameter>();\n"+
                "            for (Parameter i : parameters) parameterMap.put(i.getCustomName(),i);\n"+
                "            \n"+
                "            String sql=parameterMap.get(\"sqlCode\").getValue();\n"+
                "            \n"+
                "            List<Map<String,Object>> queryForList = jdbcTemplate.queryForList(sql);\n"+
                "            \n"+
                "            Type type = new TypeToken<Collection<Map<String,Object>>>(){}.getType();\n"+
                "            String json=gson.toJson(queryForList, type);\n"+
                "            \n"+
                "            re.setContent(json);\n"+
                "            return re;\n"+
                "        }\n"+
                "    }\n"+
                "    \n"+
                "";
        
        jdbcTemplate.update(sql,javacode);
        
        sql="insert into sc.m_sys_query_parameter (query_id,title,description,html_Type,custom_Name)\n"+
        "values(3,'SQL','要执行的SQL','textArea','sqlCode')\n";
        
        jdbcTemplate.update(sql);
    }

    protected void tearDown() throws Exception
    {
        super.tearDown();
    }

    public void testDoInQuery()
    {
        fail("Not yet implemented");
    }

    
    class QueryTestDef implements Query
    {

        public QueryReult doInQuery(JdbcTemplate jdbcTemplate,
                QueryDefinition queryDefinition)
        {
            Gson gson = new GsonBuilder()
            //.setExclusionStrategies(MyExclusionStrategy)
            .serializeNulls()
            .create();
            
            QueryReult re=new QueryReult();
            List<Parameter> parameters = queryDefinition.getParameters();
            Map<String,Parameter> parameterMap=new HashMap<String,Parameter>();
            for (Parameter i : parameters) parameterMap.put(i.getCustomName(),i);
            
            String sql=parameterMap.get("sql").getValue();
            
            List<Map<String,Object>> queryForList = jdbcTemplate.queryForList(sql);
            
            Type type = new TypeToken<Collection<Map<String,Object>>>(){}.getType();
            String json=gson.toJson(queryForList, type);
            
            re.setContent(json);
            return re;
        }
    }
    
}
