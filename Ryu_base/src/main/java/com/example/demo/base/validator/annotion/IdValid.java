package com.example.demo.base.validator.annotion;

import com.example.demo.base.validator.constraint.IdValidator;
import com.example.demo.base.validator.Messages;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * ID校验【注解】
 *
 * @author Ryu
 * @date:
 */
@Target({TYPE, ANNOTATION_TYPE, FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {IdValidator.class})
public @interface IdValid {

    boolean required() default true;

    String message() default Messages.ID_LENGTH_THIRTY_TWO;

    String value() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
