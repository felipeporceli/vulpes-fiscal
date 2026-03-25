package com.vulpesfiscal.demo.validator.annotation;
import com.vulpesfiscal.demo.validator.CpfOuCnpjExclusivoValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CpfOuCnpjExclusivoValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface CpfOuCnpjExclusivo {

    String message() default "CPF e CNPJ não podem ser informados juntos";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
