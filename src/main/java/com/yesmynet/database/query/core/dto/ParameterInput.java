package com.yesmynet.database.query.core.dto;

import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.yesmynet.database.query.utils.MessageFormatUtils;

/**
 * 表示参数的输入控件，如：input、select、radio等
 * @author 刘庆志
 *
 */
public class ParameterInput extends BaseDto
{
    /**
     * 标题
     */
    private String title;
    /**
     * 描述
     */
    private String description;
    /**
     * 参数显示时使用的html类型，如：一个单行文本框或一个多行文本框。
     */
    private ParameterHtmlType htmlType;
    /**
     * 默认参数名前面的前缀。
     * 查询参数在运行时为了收集参数值，要在HttpRequest中取一个参数，如：request.getParameter("");
     * 其中的参数名如果没有定义则使用默认参数，即本属性+id,形成如：param123这样的参数名称，如果自定义了参数名
     * 则直接使用定义的参数名
     */
    private String defaltNamePrifix="param";
    /**
     * 自定义参数名，如果本参数为空，则系统会使用默认规则生成参数名称。
     * 表示本查询参数在运行时通过HttpRequest收集参数时，在HttpRequest中的参数名。
     * 自定义参数名可以更方便的通过其它方式传参数，如：直接通过URL传参数。
     * 本属性可以为null,表示使用默认的参数名
     */
    private String customName;
    /**
     * 在页面上显示参数时的样式，如：可以使用css定义输入框的大小.
     * 本参数只要style="..."，双引号里的内容，不要带上style=""，如下是好的样式：width:150px;font-size:24px;
     */
    private String style;
    /**
     * 在页面上显示参数时的样式的class，
     * 本参数只要class="..."，双引号里的内容，不要带上class=""。
     */
    protected String styleClass;
    /**
     * 参数运行时的值
     */
    private String[] value;
    /**
     * 参数的可选值，对于文本框就是默认值，对于下拉框则可以有多个可选择值
     */
    private List<SelectOption> OptionValues;
    /**
     * 是否擦除提交的值，如果为true 表示擦除，即不回显
     */
    private Boolean eraseValue;
    
    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public ParameterHtmlType getHtmlType()
    {
        return htmlType;
    }

    public void setHtmlType(ParameterHtmlType htmlType)
    {
        this.htmlType = htmlType;
    }

    public String getDefaltNamePrifix()
    {
        return defaltNamePrifix;
    }

    public void setDefaltNamePrifix(String defaltNamePrifix)
    {
        this.defaltNamePrifix = defaltNamePrifix;
    }

    public String getCustomName()
    {
        return customName;
    }

    public void setCustomName(String customName)
    {
        this.customName = customName;
    }

    public String getStyle()
    {
        return style;
    }

    public void setStyle(String style)
    {
        this.style = style;
    }

    public String getStyleClass()
    {
        return styleClass;
    }

    public void setStyleClass(String styleClass)
    {
        this.styleClass = styleClass;
    }

    public String[] getValue()
    {
        return value;
    }

    public void setValue(String[] value)
    {
        this.value = value;
    }

    public List<SelectOption> getOptionValues()
    {
        return OptionValues;
    }

    public void setOptionValues(List<SelectOption> optionValues)
    {
        OptionValues = optionValues;
    }

    public Boolean getEraseValue()
    {
        return eraseValue;
    }

    public void setEraseValue(Boolean eraseValue)
    {
        this.eraseValue = eraseValue;
    }

    /**
     * 得到参数的html页面上显示的名称
     * @param parameterDefine
     * @return
     */
    public String getParameterName() 
    {
        String re=getCustomName();
        if(!StringUtils.hasText(re))
        {
            re=getDefaltNamePrifix();
            re+=getId();
        }
        return re;
    }

    /**
     * 转成html代码
     * @return
     */
    public String toHtml()
    {
        String re=null;
        ParameterHtmlType htmlType2 = this.getHtmlType();
        String parameterName = this.getParameterName();
        switch(htmlType2)
        {
        case InputText:
        case TextArea:
        case InputHidden:
            re=MessageFormatUtils.format(htmlType2.getHtmlTemplate(), getTextInputValue(),parameterName,style,styleClass);
            break;
        case Select:
            re=MessageFormatUtils.format(htmlType2.getHtmlTemplate(), getOptions(),parameterName,style,styleClass);
            break;
        case Radio:
        case Checkbox:
            re=toHtmlCheckBoxRadio();
            break;
        }
        
        
        return re;
    }
    private String toHtmlCheckBoxRadio()
    {
        StringBuilder re=new StringBuilder();
        ParameterHtmlType htmlType2 = this.getHtmlType();
        String htmlTemplate = htmlType2.getHtmlTemplate();
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
    /**
     * 对于只有一个值的输入框（如：单选文本框，多行文本框等），得到输入的值
     * @return
     */
    private String getTextInputValue()
    {
        String value2=null;
        if(!ArrayUtils.isEmpty(this.getValue()))
            value2=this.getValue()[0];
        
        if(!StringUtils.hasText(value2) || (getEraseValue()!=null && getEraseValue() ))
        {
            value2="";
        }
        else
        {
            if(!ParameterHtmlType.TextArea.equals(getHtmlType()))
            {
                value2=value2.replaceAll("\n", "&#10;");//这个会导致一个换行变成二个换行，还不清楚是什么原因
                value2=value2.replaceAll("\"", "&#034;");
                value2=value2.replaceAll("'", "&#039;");
            }
        }
        return value2;
    }
    /**
     * 得到下拉框的所有选项的html字符串
     * @return
     */
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
    private boolean isOptionSelected(SelectOption curOption,String[] allSelected )
    {
        boolean re=false;
        if(!ArrayUtils.isEmpty(allSelected))
        {
            for(String o:allSelected)
            {
                String curValue = curOption.getValue();
                if(StringUtils.hasText(curValue) && StringUtils.hasText(o) && curValue.equals(o))
                    re=true;
            }
        }
        return re;
    }   
}
