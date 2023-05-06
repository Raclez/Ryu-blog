package com.example.demo.base.validator.constraint;

import com.example.demo.utils.StringUtils;
import com.example.demo.base.validator.annotion.NotBlank;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 判断是否为空字符串【校验器】
 *
 * @author 
 * @date
 */
public class StringValidator implements ConstraintValidator<NotBlank, String> {
    @Override
    public void initialize(NotBlank constraintAnnotation) {

    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || StringUtils.isBlank(value) || StringUtils.isEmpty(value.trim())) {
            return false;
        }
        return true;
    }
}
