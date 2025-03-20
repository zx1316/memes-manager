package com.zx1316.memesmanager.controller.pojo;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemesFilterResponse {
    private long total;
    private List<Long> ids;
}
