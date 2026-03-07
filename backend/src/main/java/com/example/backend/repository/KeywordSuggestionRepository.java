package com.example.backend.repository;

import com.example.backend.entity.KeywordSuggestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KeywordSuggestionRepository extends JpaRepository<KeywordSuggestion, Long> {
    List<KeywordSuggestion> findByValueContainingIgnoreCase(String query);
}