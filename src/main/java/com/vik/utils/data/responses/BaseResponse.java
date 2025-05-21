package com.vik.utils.data.responses;

import com.fasterxml.jackson.annotation.*;
import lombok.*;
import lombok.experimental.*;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BaseResponse {
    @JsonProperty("message")
    private String message;
}
