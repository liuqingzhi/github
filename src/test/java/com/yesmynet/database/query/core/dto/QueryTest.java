package com.yesmynet.database.query.core.dto;

import java.lang.reflect.Type;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.StatementCallback;
import org.springframework.util.StringUtils;

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
        String javacode="\n" +
                "import com.yesmynet.database.query.core.dto.*;\n"+
                "\n"+
                "import java.lang.reflect.Type;\n"+
                "import java.sql.SQLException;\n"+
                "import java.sql.Statement;\n"+
                "import java.util.Collection;\n"+
                "import java.util.HashMap;\n"+
                "import java.util.List;\n"+
                "import java.util.Map;\n"+
                "\n"+
                "import org.springframework.context.ApplicationContext;\n"+
                "import org.springframework.context.support.ClassPathXmlApplicationContext;\n"+
                "import org.springframework.dao.DataAccessException;\n"+
                "import org.springframework.jdbc.core.JdbcTemplate;\n"+
                "import org.springframework.jdbc.core.StatementCallback;\n"+
                "import org.springframework.util.StringUtils;\n"+
                "\n"+
                "import com.google.gson.Gson;\n"+
                "import com.google.gson.GsonBuilder;\n"+
                "import com.google.gson.reflect.TypeToken;\n"+
                "\n"+
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
                "            final Map<String,Parameter> parameterMap=new HashMap<String,Parameter>();\n"+
                "            for (Parameter i : parameters) parameterMap.put(i.getCustomName(),i);\n"+
                "            String resultContent=\"\";\n"+
                "            \n"+
                "            resultContent=jdbcTemplate.execute(new StatementCallback<String>(){\n"+
                "\n"+
                "                public String doInStatement(Statement stmt)\n"+
                "                        throws SQLException, DataAccessException\n"+
                "                {\n"+
                "                    String re1=\"\";\n"+
                "                    final String sql=parameterMap.get(\"sqlCode\").getValue();\n"+
                "                    java.sql.ResultSet rs=null;\n"+
                "                    \n"+
                "                    stmt.execute(sql);\n"+
                "                    rs=stmt.getResultSet();\n"+
                "                    try\n"+
                "                    {\n"+
                "                        re1=ShowResultSet(rs);\n"+
                "                    } catch (Exception e)\n"+
                "                    {\n"+
                "                        throw new RuntimeException(e);\n"+
                "                    }\n"+
                "                    return re1;\n"+
                "                }});\n"+
                "            \n"+
                "            re.setContent(resultContent);\n"+
                "            return re;\n"+
                "        }\n"+
                "        private String ShowResultSet(final java.sql.ResultSet rs) throws Exception\n"+
                "        {\n"+
                "            StringBuffer sb=new StringBuffer();\n"+
                "            StringBuffer sbReturnValue=new StringBuffer();\n"+
                "            java.sql.ResultSetMetaData rsmd =null; \n"+
                "            int iColumnCount=0,i=0;\n"+
                "            String strCurrentColumnTypeName=\"\",strCurrentColumnValue=\"\",strColumnLength=\"\";\n"+
                "            int iResultSetCount=0;\n"+
                "            try\n"+
                "            {\n"+
                "                //sb.append(\"<table width=100% border=1 >\\n\");\n"+
                "                if (rs!=null)\n"+
                "                {\n"+
                "                    rsmd =rs.getMetaData();\n"+
                "                    iColumnCount=rsmd.getColumnCount();\n"+
                "                    //输出表头\n"+
                "                    sb.append(\" <tr>\\n\");\n"+
                "                    for (i=1;i<=iColumnCount;i++)\n"+
                "                    {\n"+
                "                        sb.append(\"     <th>\"+rsmd.getColumnName(i)+\"</th>\\n\");\n"+
                "                    }\n"+
                "                    sb.append(\" </tr>\\n\");\n"+
                "                    sb.append(\" <tr>\\n\");\n"+
                "                    strColumnLength=\"\";\n"+
                "                    for (i=1;i<=iColumnCount;i++)\n"+
                "                    {\n"+
                "                        strColumnLength=repalceNull(rsmd.getScale(i)+\"\",\"0\");\n"+
                "                        strColumnLength=rsmd.getPrecision(i)+\"\"+(strColumnLength.equals(\"0\")? \"\":\",\"+strColumnLength);\n"+
                "                        strColumnLength=repalceNull(strColumnLength,\"0\");\n"+
                "                        \n"+
                "                        strColumnLength=strColumnLength.equalsIgnoreCase(\"0\")?rsmd.getColumnDisplaySize(i)+\"\":strColumnLength;\n"+
                "                        sb.append(\"     <th>\"+rsmd.getColumnTypeName(i)+\"(\"+ strColumnLength +\")\"+\"</th>\\n\");\n"+
                "                                        \n"+
                "\n"+
                "                    }\n"+
                "                    sb.append(\" </tr>\\n\");\n"+
                "                    \n"+
                "                    //输出数据\n"+
                "                    iResultSetCount=0;\n"+
                "                    while(rs.next())\n"+
                "                    {\n"+
                "                        sb.append(\" <tr>\\n\");\n"+
                "                        for (i=1;i<=iColumnCount;i++)\n"+
                "                        {\n"+
                "                            strCurrentColumnTypeName=rsmd.getColumnTypeName(i);\n"+
                "                            if (strCurrentColumnTypeName.equalsIgnoreCase(\"blob\"))\n"+
                "                                strCurrentColumnValue=\"&lt;Blob&gt;\";\n"+
                "                            else if (strCurrentColumnTypeName.equalsIgnoreCase(\"clob\"))\n"+
                "                                strCurrentColumnValue=\"&lt;Clob&gt;\";\n"+
                "                            else if (strCurrentColumnTypeName.equalsIgnoreCase(\"text\"))\n"+
                "                                strCurrentColumnValue=\"&lt;Text&gt;\";\n"+
                "                            else if (strCurrentColumnTypeName.equalsIgnoreCase(\"image\"))\n"+
                "                                strCurrentColumnValue=\"&lt;Image&gt;\";\n"+
                "                            else\n"+
                "                                strCurrentColumnValue=rs.getString(i);\n"+
                "                                \n"+
                "                            sb.append(\"     <td>\"+ strCurrentColumnValue +\"</td>\\n\");\n"+
                "                        }\n"+
                "                        sb.append(\" </tr>\\n\");\n"+
                "                        iResultSetCount++;           \n"+   
                "                    }\n"+
                "                    \n"+
                "                }\n"+
                "                \n"+
                "                \n"+
                "                sbReturnValue.append(\"<table width=100% border=1 >\\n\");\n"+
                "                sbReturnValue.append(\"  <tr>\\n\");\n"+
                "                sbReturnValue.append(\"      <th align='left' colspan='\"+ iColumnCount +\"'>共查询到<span style='color:red'>\"+ iResultSetCount +\"</span>条记录</th>\\n\");\n"+
                "                sbReturnValue.append(\"  <tr>\\n\");\n"+
                "                sbReturnValue.append(sb);\n"+
                "                sbReturnValue.append(\"</table>\\n\");\n"+
                "            }\n"+
                "            catch (Exception e)\n"+
                "            {\n"+
                "                e.printStackTrace();\n"+
                "                throw e;\n"+
                "            }\n"+
                "            \n"+
                "            return sbReturnValue.toString();\n"+
                "        }\n"+
                "        private String repalceNull(String src,String toReplaceNull)\n"+
                "        {\n"+
                "            String re=src;\n"+
                "            if(!StringUtils.hasText(src))\n"+
                "                re=toReplaceNull;\n"+
                "            return re;\n"+
                "                \n"+
                "        }\n"+
                "    }\n"+
                "";
        
        jdbcTemplate.update(sql,javacode);
        
        sql="insert into sc.m_sys_query_parameter (query_id,title,description,html_Type,custom_Name)\n"+
        "values(7,'SQL','要执行的SQL','textArea','sqlCode')\n";
        
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
            final Map<String,Parameter> parameterMap=new HashMap<String,Parameter>();
            for (Parameter i : parameters) parameterMap.put(i.getCustomName(),i);
            String resultContent="";
            
            resultContent=jdbcTemplate.execute(new StatementCallback<String>(){

                public String doInStatement(Statement stmt)
                        throws SQLException, DataAccessException
                {
                    String re="";
                    final String sql=parameterMap.get("sqlCode").getValue();
                    java.sql.ResultSet rs=null;
                    
                    stmt.execute(sql);
                    rs=stmt.getResultSet();
                    try
                    {
                        re=ShowResultSet(rs);
                    } catch (Exception e)
                    {
                        throw new RuntimeException(e);
                    }
                    return re;
                }});
            
            re.setContent(resultContent);
            return re;
        }
        private String ShowResultSet(final java.sql.ResultSet rs) throws Exception
        {
            StringBuffer sb=new StringBuffer();
            StringBuffer sbReturnValue=new StringBuffer();
            java.sql.ResultSetMetaData rsmd =null; 
            int iColumnCount=0,i=0;
            String strCurrentColumnTypeName="",strCurrentColumnValue="",strColumnLength="";
            int iResultSetCount=0;
            try
            {
                //sb.append("<table width=100% border=1 >\n");
                if (rs!=null)
                {
                    rsmd =rs.getMetaData();
                    iColumnCount=rsmd.getColumnCount();
                    //输出表头
                    sb.append(" <tr>\n");
                    for (i=1;i<=iColumnCount;i++)
                    {
                        sb.append("     <th>"+rsmd.getColumnName(i)+"</th>\n");
                    }
                    sb.append(" </tr>\n");
                    sb.append(" <tr>\n");
                    strColumnLength="";
                    for (i=1;i<=iColumnCount;i++)
                    {
                        strColumnLength=repalceNull(rsmd.getScale(i)+"","0");
                        strColumnLength=rsmd.getPrecision(i)+""+(strColumnLength.equals("0")? "":","+strColumnLength);
                        strColumnLength=repalceNull(strColumnLength,"0");
                        
                        strColumnLength=strColumnLength.equalsIgnoreCase("0")?rsmd.getColumnDisplaySize(i)+"":strColumnLength;
                        sb.append("     <th>"+rsmd.getColumnTypeName(i)+"("+ strColumnLength +")"+"</th>\n");
                                        

                    }
                    sb.append(" </tr>\n");
                    
                    //输出数据
                    iResultSetCount=0;
                    while(rs.next())
                    {
                        sb.append(" <tr>\n");
                        for (i=1;i<=iColumnCount;i++)
                        {
                            strCurrentColumnTypeName=rsmd.getColumnTypeName(i);
                            if (strCurrentColumnTypeName.equalsIgnoreCase("blob"))
                                strCurrentColumnValue="&lt;Blob&gt;";
                            else if (strCurrentColumnTypeName.equalsIgnoreCase("clob"))
                                strCurrentColumnValue="&lt;Clob&gt;";
                            else if (strCurrentColumnTypeName.equalsIgnoreCase("text"))
                                strCurrentColumnValue="&lt;Text&gt;";
                            else if (strCurrentColumnTypeName.equalsIgnoreCase("image"))
                                strCurrentColumnValue="&lt;Image&gt;";
                            else
                                strCurrentColumnValue=rs.getString(i);
                                
                            sb.append("     <td>"+ strCurrentColumnValue +"</td>\n");
                        }
                        sb.append(" </tr>\n");
                        iResultSetCount++;              
                    }
                    
                }
                
                
                sbReturnValue.append("<table width=100% border=1 >\n");
                sbReturnValue.append("  <tr>\n");
                sbReturnValue.append("      <th align='left' colspan='"+ iColumnCount +"'>共查询到<span style='color:red'>"+ iResultSetCount +"</span>条记录</th>\n");
                sbReturnValue.append("  <tr>\n");
                sbReturnValue.append(sb);
                sbReturnValue.append("</table>\n");
            }
            catch (Exception e)
            {
                e.printStackTrace();
                throw e;
            }
            
            return sbReturnValue.toString();
        }
        private String repalceNull(String src,String toReplaceNull)
        {
            String re=src;
            if(!StringUtils.hasText(src))
                re=toReplaceNull;
            return re;
                
        }
    }
    
    
}
