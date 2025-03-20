package com.zx1316.memesmanager.controller.pojo;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FuzzyFilterRequest {
    @Min(1)
    private Integer page;

    @Min(1)
    private Integer size;

    @NotBlank
    private String keyword;
}
