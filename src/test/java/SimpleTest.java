import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;

public class SimpleTest extends TestCase {
	public void testSplitSQL() {
		
		Set<SignPair> pairs = new HashSet<SignPair>();

		pairs.add(new SignPair("'", "'"));
		pairs.add(new SignPair("/\\*", "\\*/"));
		pairs.add(new SignPair("--", "\n"));
		final String splitter=";";
		
		List<String> splitSQLs=null;
		String sql ="";
		
		

		sql="select * From m_order_info";
		splitSQLs = splitSQL(sql,pairs,splitter);
		System.out.println(sql+"\n\n"+splitSQLs);
		
		sql="select * From m_member_info;;;";
		splitSQLs = splitSQL(sql,pairs,splitter);
		System.out.println(sql+"\n\n"+splitSQLs);
		
		sql="insert into m_order_info(a,b,c,d)values('a/*','*/b','c;','d;;');m_values;--这是注释;\nselect/*这也是注释*/a from m_order_info";
		splitSQLs=splitSQL(sql,pairs,splitter);
		System.out.println(sql+"\n\n"+splitSQLs);
		
		sql= "abc/*'aaa*/'bc';;'/*'*/'\n--comment'\n\n'/**'comment'";
		splitSQLs= splitSQL(sql,pairs,splitter);
		System.out.println(sql+"\n\n"+splitSQLs);
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
}
