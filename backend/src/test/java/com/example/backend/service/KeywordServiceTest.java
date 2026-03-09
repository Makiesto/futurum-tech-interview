package com.example.backend.service;

import com.example.backend.entity.KeywordSuggestion;
import com.example.backend.repository.KeywordSuggestionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class KeywordServiceTest {

    @Mock
    private KeywordSuggestionRepository keywordRepository;

    @InjectMocks
    private KeywordService keywordService;

    @Test
    void shouldReturnKeywordsMatchingQuery() {
        KeywordSuggestion keyword1 = new KeywordSuggestion();
        keyword1.setValue("sport");

        KeywordSuggestion keyword2 = new KeywordSuggestion();
        keyword2.setValue("sports shoes");

        when(keywordRepository.findByValueContainingIgnoreCase("sport"))
                .thenReturn(List.of(keyword1, keyword2));

        List<String> result = keywordService.findByQuery("sport");

        assertThat(result).hasSize(2);
        assertThat(result).containsExactly("sport", "sports shoes");
    }

    @Test
    void shouldReturnEmptyListWhenNoKeywordsMatch() {
        when(keywordRepository.findByValueContainingIgnoreCase("xyz"))
                .thenReturn(List.of());

        List<String> result = keywordService.findByQuery("xyz");

        assertThat(result).isEmpty();
    }

    @Test
    void shouldBeCaseInsensitive() {
        KeywordSuggestion keyword = new KeywordSuggestion();
        keyword.setValue("Sport");

        when(keywordRepository.findByValueContainingIgnoreCase("SPORT"))
                .thenReturn(List.of(keyword));

        List<String> result = keywordService.findByQuery("SPORT");

        assertThat(result).containsExactly("Sport");
    }
}