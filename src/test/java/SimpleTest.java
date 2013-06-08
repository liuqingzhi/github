import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;


public class SimpleTest extends TestCase
{
	public void testDoInQuery()
    {
        fail("Not yet implemented");
    }
	private List<String> splitSQL(String sql)
	{
		 List<String> re=new ArrayList<String>();
		 Set<SignPair> pairs=new HashSet<SignPair>();
		 
		 pairs.add(new SignPair("'","'"));
		 pairs.add(new SignPair("/*","*/"));
		 pairs.add(new SignPair("--","\n"));
		 
		 for(SignPair p:pairs)
		 {
			 String start = p.getStart();
			 
		 }
		 
		 
		 return re;
	}
	/**
	 * 表示成对出现的符号,如''，两个单引号就是成对出现的符号，其它
	 * 还有注释也是.
	 * 
	 * @author 刘庆志
	 *
	 */
	private class SignPair
	{
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
	 * @author 刘庆志
	 *
	 */
	private class SignPairRuntime
	{
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
		/**
		 * 内部嵌套的标记
		 */
		private SignPairRuntime signPairRuntime;
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
		public SignPairRuntime getSignPairRuntime() {
			return signPairRuntime;
		}
		public void setSignPairRuntime(SignPairRuntime signPairRuntime) {
			this.signPairRuntime = signPairRuntime;
		}
		
	}
}
