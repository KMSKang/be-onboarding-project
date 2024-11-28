package com.survey.www.surveys.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SurveyDslRepositoryImpl implements SurveyDslRepository {
    private final JPAQueryFactory factory;

    @Override
    public void searchBySurveyId() {
        factory.select().from();
    }
}
