package com.yesmynet.database.query.service;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.StatementCallback;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;



import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.yesmynet.database.query.core.dto.Parameter;
import com.yesmynet.database.query.core.dto.ParameterHtmlType;
import com.yesmynet.database.query.core.dto.Query;
import com.yesmynet.database.query.core.dto.QueryDefinition;
import com.yesmynet.database.query.core.dto.QueryReult;
import com.yesmynet.database.query.dto.DataSourceConfig;
import com.yesmynet.database.query.dto.DatabaseDialect;
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
    /**
     * 所有可用的数据库言
     */
    private List<DatabseDialectService> databaseDialectServices;
    /**
     * 查询数据库时，不进行分页的最多记录数，如果查询的记录数大于本参数则进行分页
     */
    private Long noPageMaxResult=300L;
    /**
     * 查询分页时，一页显示的记录数的最大数
     */
    private Long maxPageSize=200L;
    /**
     * 默认每页显示的记录数
     */
    private Long pageSizeDefault=50L;
    /**
     * 在request参数名称，用来得到要执行的sql
     */
    private static String PARAM_SQL="sqlCode";
    /**
     * 在request参数名称，用来得到分页时每页的记录数
     */
    private static String PARAM_PAGE_SIZE="pageSize";
    /**
     * 在request参数名称，用来得到当前页码
     */
    private static String PARAM_CURRENT_PAGE="currentPage";
    
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
        p1.setCustomName(PARAM_SQL);
        
        parameters.add(p1);
        
    }
    public QueryReult doInQuery(DataSourceConfig dataSourceConfig,
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
        StringBuilder resultContent=new StringBuilder();
        final String sql=parameterMap.get("sqlCode").getValue();
        List<SqlDto> sqlList=splitSql(sql);
        if(!CollectionUtils.isEmpty(sqlList))
        {
        	for(SqlDto sqlDto:sqlList)
        	{
        		String sqlResult="";
        		
        		try
				{
					if(sqlDto.isSelect())
					{
						Long pageSize=getParameterValue(PARAM_PAGE_SIZE,parameterMap,pageSizeDefault);
						Long currentPage=getParameterValue(PARAM_CURRENT_PAGE,parameterMap,1L);
						sqlResult=executeSelectSql(sqlDto.getSql(),pageSize,currentPage,dataSourceConfig);
						
					}
					else
					{
						sqlResult=executeUpdataSql(sqlDto.getSql(),dataSourceConfig);
					}
				}
				catch (Exception e)
				{
					sqlResult=doWithException(e);
				}
        		resultContent.append(sqlResult);
        	}
        }
        

        re.setContent(resultContent.toString());
        return re;
    }
    private String executeUpdataSql(String sql,DataSourceConfig dataSourceConfig)
    {
    	String re="";
    	DataSource datasource2 = dataSourceConfig.getDatasource();
        JdbcTemplate jdbcTemplate=new JdbcTemplate(datasource2);
        
        int update = jdbcTemplate.update(sql);
        re=String.format("更新了%s条记录",update);
    	return re;
    }
    /**
     * 把Exception显示为字符串
     * @param e
     * @return
     */
    private String doWithException(Exception e)
    {
    	String re="";
    	re=ExceptionUtils.getStackTrace(e);
	    
    	return re;
    }
    /**
     * 根据参数名称得到参数值
     * @param parameterName
     * @param parameterMap
     * @return
     */
    private Long getParameterValue(String parameterName,Map<String,Parameter> parameterMap,Long defaultValue)
    {
    	Long re=null;
    	Parameter parameter = parameterMap.get(parameterName);
		if(parameter!=null)
		{
			String pageSizeStr = parameter.getValue();
			try
			{
				re=Long.parseLong(pageSizeStr);
			}
			catch (NumberFormatException e)
			{
			}
		}
		if(re==null) re=defaultValue;
		return re;
    }
    /**
     * 执行select查询，并得到要显示的结果
     * @param sql
     * @param pageSize
     * @param currentPage
     * @param dataSourceConfig
     * @return
     */
    private String executeSelectSql(String sql,Long pageSize,Long currentPage,DataSourceConfig dataSourceConfig)
    {
    	String re="";
    	DatabaseDialect databaseDialect = dataSourceConfig.getDatabaseDialect();
    	DatabseDialectService databaseDialectService = getDatabaseDialectService(databaseDialect);
    	DataSource datasource2 = dataSourceConfig.getDatasource();
        JdbcTemplate jdbcTemplate=new JdbcTemplate(datasource2);
        
        boolean paging=false;
        PagingDto pagingInfo=null; 
        String sqlToExecute=sql;
        
    	if(databaseDialectService!=null)
    	{
    		String pagingCountSql = getPagingCountSql(sql);
    		
            long resultCount = jdbcTemplate.queryForLong(pagingCountSql);
            if(resultCount>noPageMaxResult)
            {
            	//进行分页
            	pagingInfo = getPagingInfo(resultCount,pageSize,currentPage);
            	paging=true;
            	sqlToExecute=databaseDialectService.getPagingSql(databaseDialect, sql, pagingInfo.getRecordBegin(), pagingInfo.getRecordEnd());
            }
    	}
    	final String sqlToExecuteSql=sqlToExecute;
    	
        re=jdbcTemplate.execute(new StatementCallback<String>(){

            public String doInStatement(Statement stmt)
                    throws SQLException, DataAccessException
            {
                String re="";
                java.sql.ResultSet rs=null;
                
                stmt.execute(sqlToExecuteSql);
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
    	if(paging)
    	{
    		//分页了，则显示分页导航
    		String page1=String.format("当前第%s页,共%s页，共%s条记录",pagingInfo.getCurrentPage(),pagingInfo.getPageCount(),pagingInfo.getRecordCount());
    		re+=page1;
    	}
    	
    	return re;
    }
    /**
     * 得到分页的数据
     * @param recordCountInDB 数据库中查询出的记录总数
     * @param pageSize 每页显示的记录数
     * @param currentPage 当前页码
     * @return 得到的整数数组，第1个元素是
     */
    private PagingDto getPagingInfo(Long recordCountInDB,Long pageSize,Long currentPage)
    {
    	PagingDto re=new PagingDto();
    	Long pageSizeSelf=pageSize>maxPageSize?maxPageSize:pageSize;
    	Long pageCount=recordCountInDB/pageSizeSelf;
    	pageCount+=recordCountInDB % pageSizeSelf>0?1:0;
    	Long currentPageSelf=currentPage>pageCount?pageCount:currentPage;
    	Long recordBegin=(currentPageSelf-1)*pageSizeSelf+1;
    	Long recordEnd= currentPageSelf*pageSizeSelf;
    	
    	re.setCurrentPage(currentPageSelf);
    	re.setPageSize(pageSizeSelf);
    	re.setRecordBegin(recordBegin);
    	re.setRecordEnd(recordEnd);
    	re.setPageCount(pageCount);
    	re.setRecordCount(recordCountInDB);
    	
    	return re;
    }
    /**
     * 得到总记录数的sql
     * @param sql
     * @return
     */
    private String getPagingCountSql(String sql)
    {
    	String re="";
    	re+="SELECT count(*) as cnt from ( "+sql+" )a";
    	return re;
    }
    /**
     * 得到要进行分页查询的sql的言处理的service
     * @param databaseDialect 数据源的方言
     * @return
     */
    private DatabseDialectService getDatabaseDialectService(DatabaseDialect databaseDialect)
    {
    	DatabseDialectService re=null;
    	if(!CollectionUtils.isEmpty(databaseDialectServices))
    	{
    		for(DatabseDialectService dialectService:databaseDialectServices)
    		{
    			if(dialectService.isSupport(databaseDialect))
    			{
    				re=dialectService;
    				break;
    			}
    		}
    	}
    	return re;
    }
    /**
     * 分隔sql，并判断sql是不就select语句
     * */
    private List<SqlDto> splitSql(String sql)
    {
    	List<SqlDto> re=new ArrayList<SqlDto>(); 
		Set<SignPair> pairs = new HashSet<SignPair>();

		pairs.add(new SignPair("'", "'"));
		pairs.add(new SignPair("/\\*", "\\*/"));
		pairs.add(new SignPair("--", "\n"));
		final String splitter=";";
		
		List<String> splitSQL = splitSQL(sql,pairs,splitter);
		if(!CollectionUtils.isEmpty(splitSQL))
		{
			for(String sqlStr:splitSQL)
			{
				sqlStr=sqlStr.trim();
				if(sqlStr.endsWith(splitter))
				{
					sqlStr=sqlStr.substring(0,sqlStr.length()-1);
				}
				if(StringUtils.hasText(sqlStr))
				{
					boolean select=false;
					if(sqlStr.length()>=6)
					{
						String substring = sqlStr.substring(0, 6);
						if("select".equalsIgnoreCase(substring))
							select=true;
					}
					
					SqlDto s=new SqlDto();
					s.setSql(sqlStr);
					s.setSelect(select);
					
					re.add(s);
				}
			}
		}
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
    /**
	 * 分隔SQL，使用指定的分隔符分开多条sql
	 * @param sql 多条sql
	 * @param pairs 在sql中有成对出现的标记符，如多行注释，单行注释，两个单引号等，在这些成对出现的标记中
	 * 出现的分隔符不应该分隔SQL
	 * @param splitter 分隔符，如，使用分号分隔sql，则本变量即为";"
	 * @return 分隔后的多条sql
	 */
	private List<String> splitSQL(final String sql,final Set<SignPair> pairs,final String splitter) {
		List<String> re=new ArrayList<String>();
		String sqlSelf=sql;
		String patternStr=getALLSignPair(pairs,0);
		patternStr+="|"+splitter;//要在sql中查找的符号是所有的标记的开关和分隔符
		Pattern pattern=Pattern.compile(patternStr);
		Matcher matcher = pattern.matcher(sqlSelf);
		boolean finished=false;
		boolean findComma=false;
		String currentSplittedSql="";
		int i=0;
		while(!finished)
		{
			if(findComma)
			{
				re.add(currentSplittedSql);
				currentSplittedSql="";
			}
			matcher = pattern.matcher(sqlSelf);
			if(matcher.find())
			{
				String group = matcher.group();
				int end = matcher.end();
				String tmp=sqlSelf.substring(0, end);
				currentSplittedSql+=tmp;
				sqlSelf=sqlSelf.substring(end);
				System.out.println("\t\ti="+i+",group="+group+",end="+end+",currentSplittedSql="+currentSplittedSql+",sqlSefl="+sqlSelf);
				
				if(group.equals(splitter))
				{
					findComma=true;
				}
				else
				{
					findComma=false;
					String endStr =this.getSignPairEnd(group, pairs);
					
					int indexOf =sqlSelf.length();
					
					Pattern endPattern = Pattern.compile(endStr);
					Matcher matcher2 = endPattern.matcher(sqlSelf);
					if(matcher2.find())
					{
						indexOf=matcher2.end();
					}
					
					currentSplittedSql+=sqlSelf.substring(0,indexOf);
					sqlSelf=sqlSelf.substring(indexOf);
					
					System.out.println("\t\ti="+i+",endStr="+ endStr +",indexOf="+indexOf+",currentSplittedSql="+currentSplittedSql+"sqlSefl="+sqlSelf);
				}
				
			}
			else
			{
				finished=true;
			}
			i++;
		}
		currentSplittedSql+=sqlSelf;
		
		if(currentSplittedSql.length()>0)
			re.add(currentSplittedSql);
		
		
		return re;
	}
	/**
	 * 得到成对出现的标记的匹配情况。
	 * @param pairs 所有的标记
	 * @param sql 源字符串
	 * @param flag 0表示要得到开始标记的匹配情况，1表示结束标记的匹配情况
	 * @return
	 */
	private String getALLSignPair(Set<SignPair> pairs,int flag)
	{
		String allBeginSign = "";
		
		int i = 0;
		for (SignPair p : pairs) {
			String start ="";
			if(flag==0)
				start=p.getStart();
			else if(flag==1)
				start=p.getEnd();
			else
				throw new RuntimeException("不支持的操作");
			
			allBeginSign += start;
			if (i < pairs.size() - 1)
			{
				allBeginSign += "|";
			}

			i++;
		}
		return allBeginSign;

	}
	/**
	 * 根据运行时得到的开始标记得到对应的结束标记
	 * @param start 在sql中得到的开始标记
	 * @param pairs 所有标记对
	 * @return 该 start对应的结束标记
	 */
	private String getSignPairEnd(String start,Set<SignPair> pairs)
	{
		String re="";
		for(SignPair p:pairs)
		{
			if(Pattern.matches(p.getStart(), start))
			{
				re=p.getEnd();
				break;
			}
		}
		return re;
	}
	/**
	 * 表示成对出现的符号,如''，两个单引号就是成对出现的符号，其它 还有注释也是.
	 * 
	 * @author 刘庆志
	 * 
	 */
	private class SignPair {
		/**
		 * 开始标记，以正则表达式表示
		 */
		private String start;
		/**
		 * 结束标记，以正则表达式表示
		 */
		private String end;
		/**
		 * 构造函数
		 */
		public SignPair() {
			super();
		}
		/**
		 * 构造函数
		 * 
		 * @param start
		 * @param end
		 */
		public SignPair(String start, String end) {
			super();
			this.start = start;
			this.end = end;
		}

		public String getStart() {
			return start;
		}

		public void setStart(String start) {
			this.start = start;
		}

		public String getEnd() {
			return end;
		}

		public void setEnd(String end) {
			this.end = end;
		}

	}
	/**
	 * 表示一条sql语句的对象
	 * @author 刘庆志
	 *
	 */
	private class SqlDto
	{
		/**
		 * sql语句
		 */
		private String sql;
		/**
		 * 是否是一个select语句。
		 */
		private boolean select;
		public String getSql()
		{
			return sql;
		}
		public void setSql(String sql)
		{
			this.sql = sql;
		}
		public boolean isSelect()
		{
			return select;
		}
		public void setSelect(boolean select)
		{
			this.select = select;
		}
	}
	/**
	 * 表示分页查询的参数
	 * @author 刘庆志
	 *
	 */
	private class PagingDto
	{
		/**
		 * 每页显示的记录数
		 */
		private Long pageSize;
		/**
		 * 当前页码
		 */
		private Long currentPage;
		/**
		 * 总页数
		 */
		private Long pageCount;
		/**
		 * 总记录数
		 */
		private Long recordCount;
		/**
		 * 显示的记录是从第条记录开始的
		 */
		private Long recordBegin;
		/**
		 * 显示的记录是到第几条记录结束的
		 */
		private Long recordEnd;
		public Long getPageSize()
		{
			return pageSize;
		}
		public void setPageSize(Long pageSize)
		{
			this.pageSize = pageSize;
		}
		public Long getCurrentPage()
		{
			return currentPage;
		}
		public void setCurrentPage(Long currentPage)
		{
			this.currentPage = currentPage;
		}
		public Long getPageCount()
		{
			return pageCount;
		}
		public void setPageCount(Long pageCount)
		{
			this.pageCount = pageCount;
		}
		public Long getRecordCount()
		{
			return recordCount;
		}
		public void setRecordCount(Long recordCount)
		{
			this.recordCount = recordCount;
		}
		public Long getRecordBegin()
		{
			return recordBegin;
		}
		public void setRecordBegin(Long recordBegin)
		{
			this.recordBegin = recordBegin;
		}
		public Long getRecordEnd()
		{
			return recordEnd;
		}
		public void setRecordEnd(Long recordEnd)
		{
			this.recordEnd = recordEnd;
		}
		
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
