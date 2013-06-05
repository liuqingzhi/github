package com.yesmynet.database.query.core.service.impl;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.StatementCallback;
import org.springframework.util.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yesmynet.database.query.core.dto.Parameter;
import com.yesmynet.database.query.core.dto.ParameterHtmlType;
import com.yesmynet.database.query.core.dto.Query;
import com.yesmynet.database.query.core.dto.QueryDefinition;
import com.yesmynet.database.query.core.dto.QueryReult;
/**
 * 查询的默认实现，就是在界面上显示一个输入sql的多行文本框，用来执行给定的SQL
 * @author 刘庆志
 *
 */
public class QueryDefaultImpl  implements Query
{
    /**
     * 关于本查询的设置，包括所有的参数
     */
    private static QueryDefinition queryDefinition;
    
    static {
        /**
         * 初始化本查询的配置,包括所有参数
         */
        queryDefinition=new QueryDefinition();
        List<Parameter> parameters=new ArrayList<Parameter>(); 
        
        queryDefinition.setParameters(parameters);
        
        
        queryDefinition.setId("-1");
        queryDefinition.setName("默认查询");
        queryDefinition.setDescription("系统实现的默认查询");
        queryDefinition.setAfterParameterHtml("显示完所有参数后要显示的html,现在还没有");
        queryDefinition.setShowExecuteButton(true);
        
        Parameter p1=new Parameter();
        p1.setTitle("SQL脚本");
        p1.setDescription("在这里输入SQL");
        p1.setHtmlType(ParameterHtmlType.textArea);
        p1.setCustomName("sqlCode");
        
        parameters.add(p1);
        
    }
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
    public static QueryDefinition getQueryDefinition()
    {
        return queryDefinition;
    }
    public static void setQueryDefinition(QueryDefinition queryDefinition)
    {
        QueryDefaultImpl.queryDefinition = queryDefinition;
    }
    
}
