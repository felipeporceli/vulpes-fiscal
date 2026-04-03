package com.vulpesfiscal.demo.validator.annotation;

import com.vulpesfiscal.demo.validator.DataMaiorOuIgualValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DataMaiorOuIgualValidator.class)
@Documented
public @interface DataMaiorOuIgual {

    String message() default "Data final não pode ser anterior à data inicial";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String campoInicial();

    String campoFinal();
}