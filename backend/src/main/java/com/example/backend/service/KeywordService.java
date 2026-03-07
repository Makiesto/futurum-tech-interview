package com.example.backend.service;

import com.example.backend.entity.KeywordSuggestion;
import com.example.backend.repository.KeywordSuggestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KeywordService {

    private final KeywordSuggestionRepository keywordRepository;

    public List<String> findByQuery(String query) {
        return keywordRepository.findByValueContainingIgnoreCase(query)
                .stream()
                .map(KeywordSuggestion::getValue)
                .toList();
    }
}