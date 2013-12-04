package com.yesmynet.database.query.core.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.yesmynet.database.query.core.dto.ParameterHtmlType;
import com.yesmynet.database.query.core.dto.ParameterInput;
import com.yesmynet.database.query.core.service.ParameterValidatorDefine;
/**
 * 使用java正则表达式验证参数的定义
 */
public class ParameterRegularExpressionValidatorDefine extends ParameterValidatorDefine {
	private String type="RegularExpression";
	private String name="java正则表达式验证器";
	/**
	 * 表示输入正则表达式的输入框的name
	 */
	private final String PARAM_NAME_REGULAR_EXPRESSION="RegularExpression";
	@Override
	public String getValidatorType() {
		return type;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public List<ParameterInput> getInputs() {
		List<ParameterInput> re=new ArrayList<ParameterInput>();
		ParameterInput p1=new ParameterInput();
        p1.setTitle("java正则表达式");
        p1.setDescription("用于验证的参数值的正则表达式，是java中的正则表达式");//参数的描述，可以显示在界面的
        p1.setHtmlType(ParameterHtmlType.InputText);
        p1.setCustomName(PARAM_NAME_REGULAR_EXPRESSION);
        p1.setStyle("width: 100px; ");
		return re;
	}

	@Override
	protected boolean validParameterBeforeSave(Map<String, String> validateRuleDatas) {
		String regExp = validateRuleDatas.get(PARAM_NAME_REGULAR_EXPRESSION);
		if(!StringUtils.hasText(regExp))
			return false;
		else
			return true;
	}

}
