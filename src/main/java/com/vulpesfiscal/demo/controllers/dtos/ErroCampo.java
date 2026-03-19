package com.vulpesfiscal.demo.controllers.dtos;

import lombok.Builder;

@Builder
public record ErroCampo(String campo, String erro) {
}
