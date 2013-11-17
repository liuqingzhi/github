package com.yesmynet.database.query.core.dto;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

public class HtmlInputTypeTest extends TestCase {
	public void testInput()
	{
		List<Parameter> a=getHtmlInput();
		for(Parameter input:a)
		{
			System.out.println(input.toHtml()+"\n");
		}
		
			
	}
	private List<Parameter> getHtmlInput()
	{
		List<Parameter> a=new ArrayList<Parameter>();
		Parameter text=new Parameter();
		
		text.setHtmlType(ParameterHtmlType.InputText);
		text.setCustomName("inputNameaaa");
		text.setId("1");
		text.setValue(new String[]{"输入值"});
		
		Parameter select=new Parameter();
		List<SelectOption> options=new ArrayList<SelectOption>();
		
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
		
		select.setId("2");
		select.setHtmlType(ParameterHtmlType.Select);
		select.setOptionValues(options);
		select.setValue(new String[]{"o2","s2"});
		
		
		Parameter checkbox=new Parameter();
		checkbox.setId("2");
		checkbox.setHtmlType(ParameterHtmlType.Checkbox);
		checkbox.setOptionValues(options);
		checkbox.setValue(new String[]{"o2","s2"});
		
		Parameter radio=new Parameter();
		radio.setId("2");
		radio.setHtmlType(ParameterHtmlType.Radio);
		radio.setOptionValues(options);
		radio.setValue(new String[]{"o2","s2"});;
		
		
		a.add(text);
		a.add(select);
		a.add(checkbox);
		a.add(radio);
		
		return a;
	}
}
