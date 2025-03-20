package com.zx1316.memesmanager.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemeTagCompositeKey {
    private String tagName;
    private Meme meme;
}
