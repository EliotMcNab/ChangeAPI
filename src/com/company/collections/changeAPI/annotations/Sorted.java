package com.company.collections.changeAPI.annotations;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.LOCAL_VARIABLE})
public @interface Sorted {
    Class<? extends Exception> exception() default Exception.class;
}
