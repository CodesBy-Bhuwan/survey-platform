
package com.survey.controller;

import com.survey.dto.response.AnalyticsDto;
import com.survey.service.AnalyticsService;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/surveys/{surveyId}/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping
    public AnalyticsDto getAnalytics(
            @PathVariable String surveyId
    ) {

        return analyticsService.getAnalytics(surveyId);
    }
}
