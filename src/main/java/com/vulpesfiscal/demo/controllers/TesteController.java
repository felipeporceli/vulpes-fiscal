package com.vulpesfiscal.demo.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/teste")
public class TesteController {

    @GetMapping
    public Map<String, String> teste() {
        return Map.of("mensagem", "Backend funcionando");
    }
}
