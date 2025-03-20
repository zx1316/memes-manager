package com.zx1316.memesmanager.controller.pojo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemeUpdateRequest {
    @NotNull
    private Boolean favorite;

    @NotEmpty
    private Set<@NotBlank @Size(max = 48) String> tags;
}
