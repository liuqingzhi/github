package com.yesmynet.database.query.core.dto;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

public class HtmlInputTypeTest extends TestCase {
	public void testInput()
	{
		List<HtmlInputType> a=getHtmlInput();
		for(HtmlInputType input:a)
		{
			System.out.println(input.toHtml()+"\n");
		}
		
			
	}
	private List<HtmlInputType> getHtmlInput()
	{
		List<HtmlInputType> a=new ArrayList<HtmlInputType>();
		HtmlTextInput text=new HtmlTextInput(HtmlInputType.InputTypeEnum.InputText);
		
		text.setCustomName("inputNameaaa");
		text.setId("1");
		text.setOptionValues("默认值1");
		
		HtmlSelectInput select=new HtmlSelectInput(HtmlInputType.InputTypeEnum.Select);
		List<SelectOption> options=new ArrayList<SelectOption>();
		List<SelectOption> selectedOptions=new ArrayList<SelectOption>();
		
		SelectOption o1=new SelectOption();
		o1.setValue("o1");
		o1.setText("选项1");
		
		SelectOption o2=new SelectOption();
		o2.setValue("o2");
		o2.setText("选项2");
		
		SelectOption o3=new SelectOption();
		o3.setValue("o3");
		o3.setText("选项3");
		
		SelectOption o4=new SelectOption();
		o4.setValue("o4");
		o4.setText("选项4");
		
		options.add(o1);
		options.add(o2);
		options.add(o3);
		options.add(o4);
		
		SelectOption s1=new SelectOption();
		s1.setValue("o2");
		SelectOption s2=new SelectOption();
		s2.setValue("s2");
		
		
		selectedOptions.add(s1);
		selectedOptions.add(s2);
		
		select.setId("2");
		select.setOptionValues(options);
		select.setValue(selectedOptions);;
		
		
		HtmlSelectInput checkbox=new HtmlSelectInput(HtmlInputType.InputTypeEnum.Checkbox);
		checkbox.setId("2");
		checkbox.setOptionValues(options);
		checkbox.setValue(selectedOptions);;
		
		HtmlSelectInput radio=new HtmlSelectInput(HtmlInputType.InputTypeEnum.Radio);
		radio.setId("2");
		radio.setOptionValues(options);
		radio.setValue(selectedOptions);;
		
		
		a.add(text);
		a.add(select);
		a.add(checkbox);
		a.add(radio);
		
		return a;
	}
}
