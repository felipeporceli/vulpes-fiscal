package com.vulpesfiscal.demo.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Vulpes Fiscal API",
                version = "0.0.1",
                contact = @Contact(
                        name = "Felipe Porceli Volpe",
                        email = "felipe.porceliv@gmail.com",
                        url = "vulpesfiscal.com.br"
                )
        )
)
public class OpenApiConfiguration {
}
