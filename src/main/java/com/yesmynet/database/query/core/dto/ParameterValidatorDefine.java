package com.yesmynet.database.query.core.dto;

import java.util.List;
import java.util.Map;

/**
 * 参数验证器定义器。
 * 在把一个{@link #ParameterValidator}验证器用到一个参数时，可能要定义一些
 * 验证时的数据，如：对于正则表达式验证器，要定义验证使用的正则表达式，对于数字
 * 范围的验证器，要定义验证时的最小值、最大值。 
 * @author 刘庆志
 *
 */
public abstract class ParameterValidatorDefine
{
    /**
     * 得到验证器的名称。如：非空验证器、邮件地址验证器。
     * @return
     * @author 刘庆志
     */
    protected abstract String getName();
    /**
     * 得到设置验证器的所有输入框，所有输入框的名称作为{@link #showHtml(Map)}和{@link #save(Map)}中的map中的key.
     * @return
     * @author 刘庆志
     */
    protected abstract List<ParameterInput> getInputs();
    
    /**
     * 以html展示这个验证器的设置界面。
     * @param validateRuleDatas 所有的参数
     * @return
     * @author 刘庆志
     */
    public String showHtml(Map<String,String> validateRuleDatas)
    {
        List<ParameterInput> inputs = getInputs();
        
    }
    /**
     * 保存验证器的设置。
     * @param validateRuleDatas 用户提交的所有的参数
     * @return
     * @author 刘庆志
     */
    public boolean save(Map<String,String> validateRuleDatas)
    {
        
    }
    
}
