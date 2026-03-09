package com.example.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/towns")
public class TownController {

    private static final List<String> TOWNS = List.of(
            "Warszawa", "Kraków", "Wrocław", "Poznań",
            "Gdańsk", "Szczecin", "Łódź", "Katowice",
            "Lublin", "Białystok"
    );

    @GetMapping
    public ResponseEntity<List<String>> getTowns() {
        return ResponseEntity.ok(TOWNS);
    }
}
