package com.example.bankcards.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;

public class NotEqualsValidator implements ConstraintValidator<NotEquals, Object> {

    private String field1;
    private String field2;

    @Override
    public void initialize(NotEquals constraintAnnotation) {
        this.field1 = constraintAnnotation.field1();
        this.field2 = constraintAnnotation.field2();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) return true;

        BeanWrapper beanWrapper = PropertyAccessorFactory.forBeanPropertyAccess(value);

        Object val1 = beanWrapper.getPropertyValue(field1);
        Object val2 = beanWrapper.getPropertyValue(field2);

        if (val1 == null || val2 == null) {
            return true;
        }

        return !val1.equals(val2);
    }
}