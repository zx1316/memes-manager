package com.zx1316.memesmanager.model;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "meme_tag")
@IdClass(MemeTagCompositeKey.class)
public class MemeTag {
    @Id
    @Column(name = "tag_name", nullable = false, length = 48)
    private String tagName;

    @Id
    @ManyToOne
    @JoinColumn(name = "meme_id")
    private Meme meme;

    @Override
    public String toString() {
        return "MemeTag(tagName=" + tagName +
                ", memeId=" + meme.getId() + ')';
    }
}
