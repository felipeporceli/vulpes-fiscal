package com.vulpesfiscal.demo.validator;

import com.vulpesfiscal.demo.controllers.dtos.CadastroConsumidorDTO;
import com.vulpesfiscal.demo.validator.annotation.CpfOuCnpjExclusivo;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import com.vulpesfiscal.demo.controllers.dtos.CadastroConsumidorDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CpfOuCnpjExclusivoValidator implements ConstraintValidator<CpfOuCnpjExclusivo, CadastroConsumidorDTO> {

    @Override
    public boolean isValid(CadastroConsumidorDTO dto, ConstraintValidatorContext context) {
        if (dto == null) {
            return true;
        }

        boolean cpfPreenchido = dto.cpf() != null && !dto.cpf().isBlank();
        boolean cnpjPreenchido = dto.cnpj() != null && !dto.cnpj().isBlank();

        if (cpfPreenchido && cnpjPreenchido) {
            context.disableDefaultConstraintViolation();

            context.buildConstraintViolationWithTemplate("Informe apenas CPF ou CNPJ, nunca os dois juntos")
                    .addPropertyNode("cpf")
                    .addConstraintViolation();

            context.buildConstraintViolationWithTemplate("Informe apenas CPF ou CNPJ, nunca os dois juntos")
                    .addPropertyNode("cnpj")
                    .addConstraintViolation();

            return false;
        }

        if (!cpfPreenchido && !cnpjPreenchido) {
            context.disableDefaultConstraintViolation();

            context.buildConstraintViolationWithTemplate("Informe CPF ou CNPJ")
                    .addPropertyNode("cpf")
                    .addConstraintViolation();

            context.buildConstraintViolationWithTemplate("Informe CPF ou CNPJ")
                    .addPropertyNode("cnpj")
                    .addConstraintViolation();

            return false;
        }

        return true;
    }
}
