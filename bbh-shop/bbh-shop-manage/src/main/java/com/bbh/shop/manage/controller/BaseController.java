/*



 */
package com.bbh.shop.manage.controller;

import java.math.BigDecimal;
import java.util.Set;

import javax.annotation.Resource;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;


import com.bbh.common.utils.settings.Setting;
import com.bbh.common.utils.settings.SettingUtils;
import com.bbh.common.utils.spring.SpringContextUtils;
import com.bbh.shop.manage.common.Message;
import com.bbh.shop.manage.template.directive.FlashMessageDirective;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller - 基类
 *
 * @author KCSHOP Team
 * @version 3.0
 */
@Component
@DependsOn({"springContextUtils"})
public class BaseController {
    protected Logger logger= LoggerFactory.getLogger(this.getClass());
    /**
     * 错误视图
     */

    protected static final String ERROR_VIEW = "/manage/common/error";

   /* *
     * 错误消息*/

    protected static final Message ERROR_MESSAGE =Message.error("manage.message.error");


    protected static final Message SUCCESS_MESSAGE =Message.success("manage.message.success");
    /**
     * "验证结果"参数名称
     */
    private static final String CONSTRAINT_VIOLATIONS_ATTRIBUTE_NAME = "constraintViolations";

    @Resource(name = "validator")
    private Validator validator;

    /**
     * 数据绑定
     *
     * @param binder WebDataBinder
     */
    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        /*binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
        binder.registerCustomEditor(Date.class, new DateEditor(true));*/
    }

    /**
     * 数据验证
     *
     * @param target 验证对象
     * @param groups 验证组
     * @return 验证结果
     */
    protected boolean isValid(Object target, Class<?>... groups) {
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(target, groups);
        if (constraintViolations.isEmpty()) {
            return true;
        } else {
            RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
            requestAttributes.setAttribute(CONSTRAINT_VIOLATIONS_ATTRIBUTE_NAME, constraintViolations, RequestAttributes.SCOPE_REQUEST);
            return false;
        }
    }

    /**
     * 数据验证
     *
     * @param type     类型
     * @param property 属性
     * @param value    值
     * @param groups   验证组
     * @return 验证结果
     */
    protected boolean isValid(Class<?> type, String property, Object value, Class<?>... groups) {
        Set<?> constraintViolations = validator.validateValue(type, property, value, groups);
        if (constraintViolations.isEmpty()) {
            return true;
        } else {
            RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
            requestAttributes.setAttribute(CONSTRAINT_VIOLATIONS_ATTRIBUTE_NAME, constraintViolations, RequestAttributes.SCOPE_REQUEST);
            return false;
        }
    }

    /**
     * 货币格式化
     *
     * @param amount   金额
     * @param showSign 显示标志
     * @param showUnit 显示单位
     * @return 货币格式化
     */
    protected String currency(BigDecimal amount, boolean showSign, boolean showUnit) {
        Setting setting = SettingUtils.get();
        String price = setting.setScale(amount).toString();
        if (showSign) {
            price = setting.getCurrencySign() + price;
        }
        if (showUnit) {
            price += setting.getCurrencyUnit();
        }
        return price;
    }

    /**
     * 获取国际化消息
     *
     * @param code 代码
     * @param args 参数
     * @return 国际化消息
     */
    protected String message(String code, Object... args) {
        return SpringContextUtils.getMessage(code, args);
    }

    /**
     * 添加瞬时消息
     *
     * @param redirectAttributes RedirectAttributes
     * @param message            消息
     */
    protected void addFlashMessage(RedirectAttributes redirectAttributes, Message message) {
        if (redirectAttributes != null && message != null) {
            redirectAttributes.addFlashAttribute(FlashMessageDirective.FLASH_MESSAGE_ATTRIBUTE_NAME, message);
        }
    }

    /**
     * 添加日志
     *
     * @param content 内容
     */
    protected void addLog(String content) {
        if (content != null) {
            RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
            // requestAttributes.setAttribute(Log.LOG_CONTENT_ATTRIBUTE_NAME, content, RequestAttributes.SCOPE_REQUEST);
        }
    }





}