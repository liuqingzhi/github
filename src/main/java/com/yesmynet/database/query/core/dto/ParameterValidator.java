package com.yesmynet.database.query.core.dto;
/**
 * 参数的值的验证器
 * @author 刘庆志
 *
 */
public interface ParameterValidator
{
    /**
     * 验证参数的值
     * @param parameter
     * @return
     * @author 刘庆志
     */
    public ValidateResult valid(ParameterInput parameterInput); 
}
