package com.zx1316.memesmanager.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.zx1316.memesmanager.model.serializer.MemeTagSetSerializer;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.LazyGroup;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
@ToString(exclude = {"image", "thumbnail"})
@Entity
@Table(name = "meme")
public class Meme {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "CHAR(40)", nullable = false, unique = true)
    private String sha;

    @Column(columnDefinition = "CHAR(4)", nullable = false)
    private String format;

    @Column(name = "upload_time", nullable = false)
    private long uploadTime;    // not null

    @Column(nullable = false)
    private boolean favorite;   // not null

    @Lob
    @Column(nullable = false)
    @Basic(fetch = FetchType.LAZY)
    @LazyGroup("image")
    @JsonIgnore
    private byte[] image;

    @Lob
    @Column(nullable = false)
    @Basic(fetch = FetchType.LAZY)
    @LazyGroup("thumbnail")
    @JsonIgnore
    private byte[] thumbnail;

    @OneToMany(mappedBy = "meme", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonSerialize(using = MemeTagSetSerializer.class)
    private Set<MemeTag> memeTags;
}
