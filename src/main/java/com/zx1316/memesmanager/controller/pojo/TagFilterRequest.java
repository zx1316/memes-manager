package com.zx1316.memesmanager.controller.pojo;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TagFilterRequest {
    @Min(1)
    private Integer page;

    @Min(1)
    private Integer size;

    @NotNull
    private Set<@NotBlank String> tags;
}
