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
			System.out.println(input.getParameterInput().toHtml()+"\n");
		}
		
			
	}
	private List<Parameter> getHtmlInput()
	{
		List<Parameter> a=new ArrayList<Parameter>();
		Parameter text=new Parameter();
		ParameterInput textInput=new ParameterInput();
		
		text.setParameterInput(textInput);
		text.getParameterInput().setHtmlType(ParameterHtmlType.InputText);
		text.getParameterInput().setCustomName("inputNameaaa");
		text.getParameterInput().setId("1");
		text.getParameterInput().setValue(new String[]{"输入值"});
		
		Parameter select=new Parameter();
		ParameterInput selectInput=new ParameterInput();
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


        select.setParameterInput(selectInput);
		select.setId("2");
		select.getParameterInput().setHtmlType(ParameterHtmlType.Select);
		select.getParameterInput().setOptionValues(options);
		select.getParameterInput().setValue(new String[]{"o2","s2"});
		
		
		Parameter checkbox=new Parameter();
		ParameterInput checkboxInput=new ParameterInput();
		
		checkbox.setParameterInput(checkboxInput);
		checkbox.getParameterInput().setId("2");
		checkbox.getParameterInput().setHtmlType(ParameterHtmlType.Checkbox);
		checkbox.getParameterInput().setOptionValues(options);
		checkbox.getParameterInput().setValue(new String[]{"o2","s2"});
		
		Parameter radio=new Parameter();
		ParameterInput radioInput=new ParameterInput();
        
		radio.setParameterInput(radioInput);
		radio.setId("2");
		radio.getParameterInput().setHtmlType(ParameterHtmlType.Radio);
		radio.getParameterInput().setOptionValues(options);
		radio.getParameterInput().setValue(new String[]{"o2","s2"});;
		
		
		a.add(text);
		a.add(select);
		a.add(checkbox);
		a.add(radio);
		
		return a;
	}
}
