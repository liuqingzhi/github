package com.yesmynet.database.query.core.dto;

import java.text.MessageFormat;
import java.util.List;

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.yesmynet.database.query.core.dto.HtmlInputType.InputTypeEnum;
import com.yesmynet.database.query.utils.MessageFormatUtils;

public class HtmlSelectInput extends HtmlInputType<List<SelectOption>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3005498297082242227L;

	public HtmlSelectInput(InputTypeEnum inputType) {
		//检查输入框类型是否是select checkbox radio等有选项的，以免返回的值和生成的html不对。
		super(inputType);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toHtml() {
		String re="";
		InputTypeEnum inputType2 = this.getInputType();
		if(inputType2.equals(InputTypeEnum.Select))
			re=toHtmlSelect();
		else
			re=toHtmlCheckBoxRadio();
		return re;
	}
	private String toHtmlSelect()
	{
		StringBuilder re=new StringBuilder();
		InputTypeEnum inputType2 = this.getInputType();
		String htmlTemplate = inputType2.getHtmlTemplate();
		String value2 = getOptions();
		String parameterName=this.getParameterName();
		
		if(!StringUtils.hasText(value2) || (getEraseValue()!=null && getEraseValue() ))
		{
			value2="";
		}
		
		re.append(MessageFormatUtils.format(htmlTemplate, value2,parameterName,style,styleClass));
		
		return re.toString();
	}
	private String toHtmlCheckBoxRadio()
	{
		StringBuilder re=new StringBuilder();
		InputTypeEnum inputType2 = this.getInputType();
		String htmlTemplate = inputType2.getHtmlTemplate();
		List<SelectOption> value2 = this.getOptionValues();
		String parameterName=this.getParameterName();
		
		if(!CollectionUtils.isEmpty(value2))
		{
			for(SelectOption option:value2)
			{
				String selected=isOptionSelected(option,this.getValue())?"checked":"";
				re.append(MessageFormatUtils.format(htmlTemplate, option.getValue(),parameterName,style,styleClass,option.getText(),selected));
			}
		}
		return re.toString();
	}
	private String getOptions()
	{
		StringBuilder re=new StringBuilder();
		List<SelectOption> value2 = this.getOptionValues();
		if(!CollectionUtils.isEmpty(value2))
		{
			String optionTempalte="<option value='%1$s' %2$s>%3$s</option>\n";
			for(SelectOption option:value2)
			{
				String selected=isOptionSelected(option,this.getValue())?"selected":"";
				String format = String.format(optionTempalte, option.getValue(),selected,option.getText());
				re.append(format);
			}
		}
		return re.toString();
	}
	/**
	 * 判断选项是否选中
	 * @param curOption
	 * @param allSelected
	 * @return
	 */
	private boolean isOptionSelected(SelectOption curOption,List<SelectOption> allSelected)
	{
		boolean re=false;
		if(!CollectionUtils.isEmpty(allSelected))
		{
			for(SelectOption o:allSelected)
			{
				String curValue = curOption.getValue();
				String value2 = o.getValue();
				if(StringUtils.hasText(curValue) && StringUtils.hasText(value2) && curValue.equals(value2))
					re=true;
			}
		}
		return re;
	}
}
