import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.TestCase;

public class SimpleTest extends TestCase {
	public void testDoInQuery() {
		String sql = "abc/*'aaa*/'bc';;'/*'*/'\n--comment'\n\n'/**'comment'";
		splitSQL(sql);
	}

	private List<String> splitSQL(String sql) {
		

		System.out.println("src=" + sql);
		List<String> re = new ArrayList<String>();
		Set<SignPair> pairs = new HashSet<SignPair>();

		pairs.add(new SignPair("'", "'"));
		pairs.add(new SignPair("/\\*", "\\*/"));
		pairs.add(new SignPair("--", "\\n"));

		
		String allSignPairStart = getALLSignPair(pairs,0);
		getSignPairMatchList(pairs,sql,1);
		

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
		List<SignPairRuntime> beginMatchList = new ArrayList<SignPairRuntime>();
		
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
		System.out.println(allBeginSign);
		return allBeginSign;
/*
		Pattern pattern = Pattern.compile(allBeginSign);
		Matcher matcher = pattern.matcher(sql);
		while (matcher.find()) {
			String s =matcher.group(0);
				
			System.out.printf("group=%s,start=%s,end=%s\n", matcher.group(),matcher.start(), matcher.end());
			
			SignPairRuntime b=new SignPairRuntime();
			SignPair spair=new SignPair();
			spair.setStart(matcher.group());
			
			b.setSignPair(spair);
			b.setStartIndex(matcher.start());
			b.setEndIndex(matcher.end());
			beginMatchList.add(b);
		}
		
		return beginMatchList;*/
	}

	/**
	 * 表示成对出现的符号,如''，两个单引号就是成对出现的符号，其它 还有注释也是.
	 * 
	 * @author 刘庆志
	 * 
	 */
	private class SignPair {
		/**
		 * 开始标记
		 */
		private String start;
		/**
		 * 结束标记
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
	 * 成对出现的标记运行后的数据
	 * 
	 * @author 刘庆志
	 * 
	 */
	private class SignPairRuntime {
		/**
		 * 成对出现的标记
		 */
		private SignPair signPair;
		/**
		 * 开始标记的位置
		 */
		private Integer startIndex;
		/**
		 * 结束标记的位置
		 */
		private Integer endIndex;
		public SignPair getSignPair() {
			return signPair;
		}
		public void setSignPair(SignPair signPair) {
			this.signPair = signPair;
		}
		public Integer getStartIndex() {
			return startIndex;
		}
		public void setStartIndex(Integer startIndex) {
			this.startIndex = startIndex;
		}
		public Integer getEndIndex() {
			return endIndex;
		}
		public void setEndIndex(Integer endIndex) {
			this.endIndex = endIndex;
		}
	}
}
