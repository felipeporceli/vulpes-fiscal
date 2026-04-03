package com.vulpesfiscal.demo.validator;

import com.vulpesfiscal.demo.validator.annotation.DataMaiorOuIgual;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.lang.reflect.Field;
import java.time.LocalDate;

public class DataMaiorOuIgualValidator implements ConstraintValidator<DataMaiorOuIgual, Object> {

    private String campoInicial;
    private String campoFinal;

    @Override
    public void initialize(DataMaiorOuIgual annotation) {
        this.campoInicial = annotation.campoInicial();
        this.campoFinal = annotation.campoFinal();
    }

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context) {
        if (obj == null) {
            return true;
        }

        try {
            Field fieldInicial = obj.getClass().getDeclaredField(campoInicial);
            Field fieldFinal = obj.getClass().getDeclaredField(campoFinal);

            fieldInicial.setAccessible(true);
            fieldFinal.setAccessible(true);

            Object valorInicial = fieldInicial.get(obj);
            Object valorFinal = fieldFinal.get(obj);

            if (valorInicial == null || valorFinal == null) {
                return true;
            }

            if (!(valorInicial instanceof LocalDate dataInicial) || !(valorFinal instanceof LocalDate dataFinal)) {
                return false;
            }

            boolean valido = !dataFinal.isBefore(dataInicial);

            if (!valido) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(context.getDefaultConstraintMessageTemplate())
                        .addPropertyNode(campoFinal)
                        .addConstraintViolation();
            }

            return valido;

        } catch (NoSuchFieldException | IllegalAccessException e) {
            return false;
        }
    }
}
