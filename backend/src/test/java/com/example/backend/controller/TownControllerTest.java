package com.example.backend.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TownController.class)
class TownControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnListOfTowns() throws Exception {
        mockMvc.perform(get("/api/towns"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(10));
    }

    @Test
    void shouldContainWarszawa() throws Exception {
        mockMvc.perform(get("/api/towns"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("Warszawa"));
    }

    @Test
    void shouldContainKrakow() throws Exception {
        mockMvc.perform(get("/api/towns"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[1]").value("Kraków"));
    }
}