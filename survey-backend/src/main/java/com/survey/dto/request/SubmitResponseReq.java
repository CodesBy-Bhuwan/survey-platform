package com.survey.dto.request;

import tools.jackson.databind.JsonNode;
import jakarta.validation.constraints.NotNull;

public record SubmitResponseReq(

        @NotNull
        JsonNode answers

) {
}
