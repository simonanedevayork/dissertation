package com.york.doghealthtracker.config;

import com.york.doghealthtracker.model.HormoneCategory;
import com.york.doghealthtracker.model.HormoneQuestion;
import com.york.doghealthtracker.model.QuizAnswer;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "hormone.quiz")
public class HormoneQuizConfig {

    private List<Question> questions;

    @Getter
    @Setter
    public static class Question {
        private String id;
        private HormoneCategory category;
        private String text;
        private List<QuizAnswer> options;
    }
}