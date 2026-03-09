package com.example.backend.controller;

import com.example.backend.service.KeywordService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(KeywordController.class)
class KeywordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private KeywordService keywordService;

    @Test
    void shouldReturnKeywordsMatchingQuery() throws Exception {
        when(keywordService.findByQuery("sport")).thenReturn(List.of("sport", "sports shoes"));

        mockMvc.perform(get("/api/keywords").param("query", "sport"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("sport"))
                .andExpect(jsonPath("$[1]").value("sports shoes"));
    }

    @Test
    void shouldReturnEmptyListWhenNoKeywordsMatch() throws Exception {
        when(keywordService.findByQuery("xyz")).thenReturn(List.of());

        mockMvc.perform(get("/api/keywords").param("query", "xyz"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void shouldReturn400WhenQueryParamMissing() throws Exception {
        mockMvc.perform(get("/api/keywords"))
                .andExpect(status().isBadRequest());
    }
}