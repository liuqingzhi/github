package com.yesmynet.database.query.service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.StatementCallback;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StopWatch;
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
import com.yesmynet.database.query.dto.SignPair;
import com.yesmynet.database.query.dto.SqlDto;
import com.yesmynet.database.query.dto.SystemParameterName;
import com.yesmynet.database.query.utils.SqlSplitUtils;
/**
 * 查询的默认实现，就是在界面上显示一个输入sql的多行文本框，用来执行给定的SQL
 * @author 刘庆志
 *
 */
public class QueryDefaultImpl  implements Query
{
	Logger logger=LoggerFactory.getLogger(this.getClass());
    /**
     * 关于本查询的设置，包括所有的参数
     */
    private QueryDefinition queryDefinition;
    /**
     * 所有可用的数据库言
     */
    private List<DatabseDialectService> databaseDialectServices;
    /**
     * 查询数据库时，不进行分页的最多记录数，如果查询的记录数大于本参数则进行分页
     * 如果设为负数意味着查询总是分页。
     */
    private Long noPageMaxResult=-1L;
    /**
     * 查询分页时，一页显示的记录数的最大数
     */
    private Long maxPageSize=100L;
    /**
     * 默认每页显示的记录数
     */
    private Long pageSizeDefault=20L;
    /**
     * 在request参数名称，用来得到要执行的sql
     */
    private final String PARAM_SQL="sqlCode";
    /**
     * 在request参数名称，用来得到分页时每页的记录数
     */
    private final String PARAM_PAGE_SIZE="pageSize";
    /**
     * 在request参数名称，用来得到当前页码
     */
    private final String PARAM_CURRENT_PAGE="currentPage";
    /**
     * 是否为ajax请求，只要本参数有值（不管值是什么）就是ajax请求
     */
    private final String PARAM_REQUEST_BY_AJAX="ajaxRequest";
    
    
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
        final String sql=parameterMap.get(PARAM_SQL).getValue();
        final Boolean ajaxRequest=StringUtils.hasText(parameterMap.get(PARAM_REQUEST_BY_AJAX).getValue());
        List<SqlDto> sqlList=SqlSplitUtils.splitSql(sql);
        if(!CollectionUtils.isEmpty(sqlList))
        {
        	boolean tabbedContent=true;//是否使用tab选项卡显示内容
        	int i=1;
        	StringBuilder tabHeaders=new StringBuilder();
        	StringBuilder tabContents=new StringBuilder();
        	
        	tabHeaders.append("<ul>");
        	
        	if(sqlList.size()>1) tabbedContent=true;
        	
        	for(SqlDto sqlDto:sqlList)
        	{
        		String sqlResult="";
        		String tabShowResultDivId=getRandomString();//getShowResultDivId(i);
        		Long pageSize=getParameterValue(PARAM_PAGE_SIZE,parameterMap,pageSizeDefault);
				Long currentPage=getParameterValue(PARAM_CURRENT_PAGE,parameterMap,1L);
        		try
				{
					if(sqlDto.isSelect())
					{
						//sqlResult=executeSelectSql(sqlDto.getSql(),pageSize,currentPage,dataSourceConfig,i,tabShowResultDivId);
						sqlResult=executeSelectSqlPageInStatement(sqlDto.getSql(),pageSize,currentPage,dataSourceConfig,i,tabShowResultDivId);
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
        		String tabHeader = getTabHeader(sqlDto,i,tabShowResultDivId);
        		String tabContent=sqlResult;
        		
        		if(tabbedContent && !ajaxRequest)
        			tabContent=getTabContent(sqlResult,tabShowResultDivId);
        		
        		tabHeaders.append(tabHeader);
        		tabContents.append(tabContent);
        		
        		i++;
        	}
        	tabHeaders.append("</ul>\n");
        	if(tabbedContent && !ajaxRequest)
        	{
        		resultContent.append("<div id='tabs'>\n");
            	resultContent.append(tabHeaders);
            	resultContent.append(tabContents);
            	resultContent.append("</div>\n");

            	resultContent.append("\n").append("<script>\n").append("$(function() {\n").append("$( \"#tabs\" ).tabs();\n").append("});\n").append("</script>\n");
        	}
        	else
        	{
        		resultContent.append(tabContents);
        	}
        	
        	String pageNavigationScriptFunction = getPageNavigationScriptFunction();
        	resultContent.append(pageNavigationScriptFunction);
        }
        
        
        re.setContent(resultContent.toString());
        re.setOnlyShowContent(ajaxRequest);
        return re;
    }
    /**
     * 在statement中实现分页，这可能会有性能问题。
     * 我试了在sql中实现分页，会查出一些奇怪的结果集，如下的sql:SELECT b.* FROM (
		SELECT a.*,ROWNUM num FROM (select * From m_trading_transaction_balance t1 
		join m_trading_goods t2 on t1.seller_trading_id=t2.id
		order by t1.id) a where ROWNUM  <=  20 ) b WHERE num  >=  1
		字段名都是很奇怪的。
     * @param sql
     * @param pageSize
     * @param currentPage
     * @param dataSourceConfig
     * @param sqlIndex
     * @param showResultDivId
     * @return
     */
    
    private String executeSelectSqlPageInStatement(String sql,Long pageSize,Long currentPage,DataSourceConfig dataSourceConfig,int sqlIndex,String showResultDivId)
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
            	//sqlToExecute=databaseDialectService.getPagingSql(databaseDialect, sql, pagingInfo.getRecordBegin(), pagingInfo.getRecordEnd());
            }
    	}
    	final String sqlToExecuteSql=sqlToExecute;
    	final Long recordBegin=pagingInfo.getRecordBegin();
    	final Long recordEnd=pagingInfo.getRecordEnd();
    	final StopWatch stopWatch=new StopWatch();
    	
        re=jdbcTemplate.execute(new ConnectionCallback<String>(){

			public String doInConnection(Connection con) throws SQLException,
					DataAccessException {
				String re="";
				
				try
                {
					stopWatch.start("执行查询");
					Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
					ResultSet rs = stmt.executeQuery(sqlToExecuteSql);
					stopWatch.stop();
					
                    if(rs!=null)
                    {
                    	stopWatch.start("ResultSet中滚动以实现分页");
                    	//rs.first();
                    	//rs.relative(recordBegin.intValue()-1);
                    	int resultSetPageStart=recordBegin.intValue()-1;//国为在输出ResultSet时行调用了ResultSet的next()方法，所以这里少向前滚动了一条数据
                    	if(resultSetPageStart>0)
                    		rs.absolute(resultSetPageStart);
                    	stopWatch.stop();
                    	
                    	stopWatch.start("输出ResultSet中的数据");
                    	re=ShowResultSet(rs,recordBegin,recordEnd);
                    	stopWatch.stop();
                    }
                    
                } catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
                return re;
				
			}});
    	if(paging)
    	{
    		//String sqlIndexDivId="sqlPageDiv"+sqlIndex;
    		//分页了，则显示分页导航
    		
    		String showResultDivIdContainer=getRandomString();
    		//String pageDataDivIdContainer=getRandomString();
    		String page1=getPageNavigation(pagingInfo,showResultDivIdContainer,true);
    		String page2=getPageNavigation(pagingInfo,showResultDivIdContainer,false);
    		String pageDatas=showPagingNavigation(sql,dataSourceConfig,pagingInfo);//,pageDataDivIdContainer);
    		
    		re="<div id='"+ showResultDivIdContainer +"'>"+pageDatas+page1+re;
    		String prettyPrint = stopWatch.prettyPrint();
    		re+="<pre>"+ prettyPrint +"</pre>";
    		re+=page2;
    		re+="</div>";
    	}
    	
    	return re;
    }
    /**
     * 在页面上定义一个javascript函数，以进行分页操作。
     * @return
     */
    private String getPageNavigationScriptFunction()
    {
    	StringBuilder re=new StringBuilder();
    	
    	re.append("<div id=\"ajaxtip\" title=\"正在执行查询\" style=\"display:none;\">\n");
    	re.append("	<img id='ajaxtipImage' src='' style='vertical-align:middle; width:25px; height:25px; margin-right:5px; display:inline;' />正在执行查询，请稍候...\n");  
    	re.append("</div>\n");
    	
    	re.append("<script type=\"text/javascript\">\n");
    	re.append("		function goPage(toReplaceContentDivId,targetPageNum)\n");
    	re.append("		{\n");
    	re.append("			var url=requestContext+\"/query.do\";\n");
    	re.append("			var datasourceId=$(\"#\"+toReplaceContentDivId+\" #SystemDataSourceId\").val();\n");
    	re.append("			var sql=$(\"#\"+toReplaceContentDivId+\" #sqlCode\").val();\n");
    	re.append("			\n");
    	re.append("			$.ajax({\n");
    	re.append("				  type: \"POST\",\n");
    	re.append("				  url: url,\n");
    	re.append("				  dataType:\"html\",\n");
    	re.append("				  data: { \"SystemQueryExecute\":\"\",\"ajaxRequest\":\"1\",\"SystemDataSourceId\": datasourceId, \"sqlCode\": sql,\"currentPage\":targetPageNum },\n");
    	re.append("				  beforeSend:function() {\n");
    	re.append("				  	var ajaxtipImage =$( \"#ajaxtipImage\" );\n");
    	re.append("				  	var ajaxtip=$( \"#ajaxtip\" );\n");
    	re.append("				  	ajaxtipImage.attr('src',requestContext+'/image/loading.gif');\n");
    	re.append("				  	ajaxtip.css('position','absolute');\n");
    	re.append("				  	ajaxtip.css('top', Math.max(0, (($(window).height() - ajaxtip.outerHeight()) / 2) + $(window).scrollTop()) + 'px');\n");
    	re.append("				  	ajaxtip.css('left', Math.max(0, (($(window).width() - ajaxtip.outerWidth()) / 2) + $(window).scrollLeft()) + 'px');\n");
    	re.append("				  	ajaxtip.show();\n");
    	re.append("				  	 },\n");
    	re.append("				  success: function(data, textStatus, jqXHR) {\n");
    	re.append("				  	$( \"#ajaxtip\" ).hide();\n");
    	re.append("					  $(\"#\"+toReplaceContentDivId).html(data);\n");
    	re.append("				  }\n");
    	re.append("				});\n");
    	re.append("		}\n");
    	re.append("	</script>\n");
    	
    	return re.toString();
    }
    /**
     * 得到tab选项卡的头，用来点击这个头可以显示tab页的内容。
     * @param sql 执行的SQL
     * @param tabListIndex 这个tab是所有tab选项卡的第几个。
     * @return
     */
    private String getTabHeader(SqlDto sql,int tabListIndex,String tabContentDivId)
    {
    	String re="<li><a href=\"#"+ tabContentDivId +"\">SQL "+ tabListIndex +"</a></li>\n";
    	return re;
    }
    /**
     * 得到显示查询结果的div的ID
     * @param tabListIndex
     * @return
     */
    private String getShowResultDivId(int tabListIndex)
    {
    	String re="tabs-"+ tabListIndex +"";
    	return re;
    }
    /**
     * 得到tab选项页的内容
     * @param content
     * @return
     */
    private String getTabContent(String content,String tabResultDivId)
    {
    	String re="";
    	re="<div id='"+ tabResultDivId +"'>"+ content +"</div>";
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
    	String re="<pre>";
    	re+=ExceptionUtils.getStackTrace(e);
	    re+="</pre>";
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
    private String executeSelectSql(String sql,Long pageSize,Long currentPage,DataSourceConfig dataSourceConfig,int sqlIndex,String showResultDivId)
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
    	final Long recordBegin=pagingInfo.getRecordBegin();
    	final Long recordEnd=pagingInfo.getRecordEnd();
    	
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
                    re=ShowResultSet(rs,recordBegin,recordEnd);
                } catch (Exception e)
                {
                    throw new RuntimeException(e);
                }
                return re;
            }});
    	if(paging)
    	{
    		//String sqlIndexDivId="sqlPageDiv"+sqlIndex;
    		//分页了，则显示分页导航
    		
    		String showResultDivIdContainer=getRandomString();
    		//String pageDataDivIdContainer=getRandomString();
    		String page1=getPageNavigation(pagingInfo,showResultDivIdContainer,true);
    		String page2=getPageNavigation(pagingInfo,showResultDivIdContainer,false);
    		String pageDatas=showPagingNavigation(sql,dataSourceConfig,pagingInfo);//,pageDataDivIdContainer);
    		
    		re="<div id='"+ showResultDivIdContainer +"'>"+pageDatas+page1+re;
    		re+=page2;
    		re+="</div>";
    		
            
    		
    	}
    	
    	return re;
    }
    /**
     * 得到一个随机字符串作为显示查询结果的div的ID，这个ID不能重复，以准备使用ajax更新结果
     * @return
     */
    private String getRandomString()
    {
    	String re="";
    	re=UUID.randomUUID().toString();
    	return re;
    }
    /**
     * 输出用于分页控制的一些变量 
     * @param sql 要执行的SQL，是用户提交的sql，不带分页
     * @param dataSourceConfig 数据源配置
     * @param pagingInfo 分页的信息
     * @param sqlIndex 当前执行的SQL是所有SQL中的第几个SQL语句，用此序号在html中产生一个ID
     * @return 在html中分页相关的所有数据
     */
    private String showPagingNavigation(String sql,DataSourceConfig dataSourceConfig,PagingDto pagingInfo)//,String sqlIndexDivId)
    {
    	StringBuilder re=new StringBuilder();
    	
    	//re.append("<div id='").append(sqlIndexDivId).append("' style='display:none;'>");
    	re.append("<textarea id='"+ PARAM_SQL +"' style='display:none;'>").append(sql).append("</textarea>\n");//使用textarea以避免sql中的特殊符号导致html出错
    	re.append("<input type='hidden' id='"+ SystemParameterName.DataSourceId.getParamerName() +"' value='"+ dataSourceConfig.getId() +"'>\n");
    	re.append("<input type='hidden' id='"+ PARAM_PAGE_SIZE +"' value='"+ pagingInfo.getPageSize() +"'>\n");
    	re.append("<input type='hidden' id='"+ PARAM_CURRENT_PAGE +"' value='"+ pagingInfo.getCurrentPage() +"'>\n");
    	re.append("<input type='hidden' id='"+ PARAM_REQUEST_BY_AJAX +"' value=''>\n");
    	
    	//re.append("</div>");
    	
    	return re.toString();
    }
    /**
     * 显示分页的导航，可以到达下一页，上一页等
     * @param pagingInfo
     * @return
     */
    private String getPageNavigation(PagingDto pagingInfo,String resultContentDivId,boolean beforeData)
    {
    	String re="";
    	
    	re=String.format("当前第%s页/共%s页，共%s条记录",pagingInfo.getCurrentPage(),pagingInfo.getPageCount(),pagingInfo.getRecordCount());
    	if(pagingInfo.getCurrentPage()>1)
    	{
    		re+=",<a href=\"javascript:goPage('"+ resultContentDivId +"','"+ (pagingInfo.getCurrentPage()-1) +"')\">上一页</a>";
    	}
    	else
    	{
    		re+=",<span>上一页</span>";
    	}
    	String goPageByNumInputName="goPageByNum"+(beforeData?"beforeData":"afterData") ;
    	String goPageNum="$('#"+ resultContentDivId +" #"+ goPageByNumInputName +"').val()";
    	re+=String.format(",到第<input type=\"text\" name=\"%s\" id=\"%s\" value=\"%s\" size=\"3\">页%s",goPageByNumInputName,goPageByNumInputName,pagingInfo.getCurrentPage(),pagingInfo.getPageCount()>0?"<a href=\"javascript:goPage('"+ resultContentDivId +"',"+ goPageNum +")\">确定</a>":"<span>确定</span>" );
    	
    	if(pagingInfo.getCurrentPage()<pagingInfo.getPageCount())
    	{
    		re+=",<a href=\"javascript:goPage('"+ resultContentDivId +"','"+ (pagingInfo.getCurrentPage()+1) +"')\">下一页</a>";
    	}
    	else
    	{
    		re+=",<span>下一页</span>";
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
    	if(currentPageSelf<1)
    		currentPageSelf=1L;
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
     * 显示结果
     * @param rs
     * @return
     * @throws Exception
     */
    private String ShowResultSet(final java.sql.ResultSet rs,final Long recordBegin,Long recordEnd) throws Exception
    {
        StringBuffer sb=new StringBuffer();
        StringBuffer sbReturnValue=new StringBuffer();
        java.sql.ResultSetMetaData rsmd =null; 
        int iColumnCount=0,i=0;
        String strCurrentColumnTypeName="",strCurrentColumnValue="",strColumnLength="";
        Long iResultSetCountBegin=recordBegin;
        Long iResultSetCount=iResultSetCountBegin;
        boolean hasRecord=false;
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
                
                iResultSetCount--;
                //输出数据
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
                    hasRecord=true;
                    
                    if(iResultSetCount>=recordEnd)
                    	break;
                }
                
            }
            
            if(!hasRecord)
            {
            	//这说明ResultSet是空的，没有数据
            	iResultSetCountBegin=0L;
            	iResultSetCount=0L;
            }
            
            
            sbReturnValue.append("<table width=100% border=1 >\n");
            sbReturnValue.append("  <tr>\n");
            sbReturnValue.append("      <th align='left' colspan='"+ iColumnCount +"'>当前显示从第<span style='color:red'>"+ iResultSetCountBegin +"</span>条到第<span style='color:red'>"+ iResultSetCount +"</span>条记录</th>\n");
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
	/**
	 * 初始化本查询的所有参数
	 * @return
	 */
	private QueryDefinition initQueryDefinition()
	{
        /**
         * 初始化本查询的配置,包括所有参数
         */
		QueryDefinition queryDefinition=new QueryDefinition();
        List<Parameter> parameters=new ArrayList<Parameter>(); 
        
        queryDefinition.setParameters(parameters);
        
        
        queryDefinition.setId("-1");
        queryDefinition.setName("默认查询");
        queryDefinition.setDescription("系统实现的默认查询");
        queryDefinition.setAfterParameterHtml("");//显示完所有参数后要显示的html,现在还没有
        queryDefinition.setShowExecuteButton(true);
        
        Parameter p1=new Parameter();
        p1.setTitle("SQL脚本");
        p1.setDescription("");//参数的描述，可以显示在界面的
        p1.setHtmlType(ParameterHtmlType.textArea);
        p1.setCustomName(PARAM_SQL);
        p1.setStyle("width: 1000px; height: 200px;");
        
        Parameter p2=new Parameter();
        p2.setTitle("每页显示的记录数");
        p2.setDescription("");
        p2.setHtmlType(ParameterHtmlType.inputHidden);
        p2.setCustomName(PARAM_PAGE_SIZE);
        p2.setStyle("");
        
        Parameter p3=new Parameter();
        p3.setTitle("当前页码");
        p3.setDescription("");
        p3.setHtmlType(ParameterHtmlType.inputHidden);
        p3.setCustomName(PARAM_CURRENT_PAGE);
        p3.setStyle("");
        
        Parameter p4=new Parameter();
        p4.setTitle("是否为ajax请求");
        p4.setDescription("");
        p4.setHtmlType(ParameterHtmlType.inputHidden);
        p4.setCustomName(PARAM_REQUEST_BY_AJAX);
        p4.setStyle("");
        
        
        parameters.add(p1);
        parameters.add(p2);
        parameters.add(p3);
        parameters.add(p4);
        
        return queryDefinition;
	    
	}
    public QueryDefinition getQueryDefinition()
    {
    	if(this.queryDefinition==null)
    	{
    		this.queryDefinition=initQueryDefinition();
    	}
        return this.queryDefinition;
    }
    public void setQueryDefinition(QueryDefinition queryDefinition)
    {
        this.queryDefinition = queryDefinition;
    }
	public List<DatabseDialectService> getDatabaseDialectServices() {
		return databaseDialectServices;
	}
	public void setDatabaseDialectServices(
			List<DatabseDialectService> databaseDialectServices) {
		this.databaseDialectServices = databaseDialectServices;
	}
    
}
