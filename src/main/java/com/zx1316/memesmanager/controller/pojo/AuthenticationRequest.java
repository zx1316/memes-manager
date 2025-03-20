package com.zx1316.memesmanager.controller.pojo;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationRequest {
    @NotNull
    private String username;

    @NotNull
    private String password;
}
