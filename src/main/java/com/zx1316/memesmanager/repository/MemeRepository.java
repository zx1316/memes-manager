package com.zx1316.memesmanager.repository;

import com.zx1316.memesmanager.model.Meme;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface MemeRepository extends JpaRepository<Meme, Long> {
    // 查询拥有的标签是给定标签集合子集的图片
    @Query("SELECT m.id FROM Meme m JOIN m.memeTags t WHERE t.tagName IN :tags GROUP BY m.id HAVING COUNT(t.tagName) = :tagCount ORDER BY m.uploadTime DESC")
    Page<Long> findIdsByTagsSubset(Set<String> tags, int tagCount, Pageable pageable);

    @Query("SELECT m.id FROM Meme m JOIN m.memeTags t WHERE t.tagName IN :tags GROUP BY m.id HAVING COUNT(t.tagName) = :tagCount ORDER BY m.uploadTime DESC")
    List<Long> findIdsByTagsSubset(Set<String> tags, int tagCount);

    // 模糊查询图片
    @Query("SELECT DISTINCT m.id FROM Meme m JOIN m.memeTags t WHERE t.tagName LIKE %:keyword% ORDER BY m.uploadTime DESC")
    Page<Long> findIdsByKeyword(String keyword, Pageable pageable);

    @Query("SELECT DISTINCT m.id FROM Meme m JOIN m.memeTags t WHERE t.tagName LIKE %:keyword% ORDER BY m.uploadTime DESC")
    List<Long> findIdsByKeyword(String keyword);

    // 查询所有图片
    @Query("SELECT m.id FROM Meme m ORDER BY m.uploadTime DESC")
    Page<Long> findAllIdsPageable(Pageable pageable);

    @Query("SELECT m.id FROM Meme m ORDER BY m.uploadTime DESC")
    List<Long> findAllIdsPageable();

    Optional<Meme> findBySha(String sha);
}
