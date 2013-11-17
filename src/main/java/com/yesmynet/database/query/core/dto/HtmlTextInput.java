package com.yesmynet.database.query.core.dto;

import java.text.MessageFormat;

import org.springframework.util.StringUtils;

import com.yesmynet.database.query.utils.MessageFormatUtils;

public class HtmlTextInput extends HtmlInputType<String> {
	
	public HtmlTextInput(InputTypeEnum inputType) {
		//这里可以检查输入框的类型是否是文本框，以保证生成的html代码是正确的。
		super(inputType);
	}
	@Override
	public String toHtml() {
		return toHtmlInternal();
	}
	private String toHtmlInternal()
	{
		StringBuilder re=new StringBuilder();
		InputTypeEnum inputType2 = this.getInputType();
		String htmlTemplate = inputType2.getHtmlTemplate();
		String value2 = this.getValue();
		String parameterName=this.getParameterName();
		
		if(!StringUtils.hasText(value2) || (getEraseValue()!=null && getEraseValue() ))
		{
			value2="";
		}
		else
		{
			if(!InputTypeEnum.TextArea.equals(inputType2))
			{
				value2=value2.replaceAll("\n", "&#10;");//这个会导致一个换行变成二个换行，还不清楚是什么原因
				value2=value2.replaceAll("\"", "&#034;");
				value2=value2.replaceAll("'", "&#039;");
			}
		}
		
		re.append(MessageFormatUtils.format(htmlTemplate, value2,parameterName,style,styleClass));
		
		return re.toString();
	}
}
