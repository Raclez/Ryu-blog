package com.example.demo.base.validator.constraint;

import com.example.demo.base.validator.annotion.Range;
import com.example.demo.utils.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 字符串范围约束，限制长度【校验器】
 *
 * @author
 * @date
 */
public class RangValidator implements ConstraintValidator<Range, String> {
    private long min;
    private long max;

    @Override
    public void initialize(Range constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        if (null == value || StringUtils.isBlank(value)) {
            return min == 0;
        }
        return value.length() >= min && value.length() <= max;
    }
}
