package com.zx1316.memesmanager.controller;

import com.zx1316.memesmanager.controller.pojo.FuzzyFilterRequest;
import com.zx1316.memesmanager.controller.pojo.TagFilterRequest;
import com.zx1316.memesmanager.controller.pojo.MemeUpdateRequest;
import com.zx1316.memesmanager.model.Meme;
import com.zx1316.memesmanager.service.MemeService;
import com.zx1316.memesmanager.controller.pojo.MemesFilterResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/memes")
public class MemesAPIController {
    @Autowired
    private MemeService memeService;

    @PostMapping
    public void insertMeme(@RequestParam MultipartFile file, @RequestParam @NotEmpty Set<@NotBlank @Size(max = 48) String> tags) {
        memeService.insertMeme(file, tags);
    }

    @DeleteMapping("/{id}")
    public void deleteMeme(@PathVariable long id) {
        memeService.deleteMeme(id);
    }

    @DeleteMapping
    public void deleteMemes(@RequestBody @NotNull Set<@NotNull Long> ids) {
        ids.forEach(memeService::deleteMeme);
    }

    @PutMapping("/{id}")
    public void updateMeme(@PathVariable long id, @RequestBody @Valid MemeUpdateRequest request) {
        memeService.updateMeme(id, request.getFavorite(), request.getTags());
    }

    @PostMapping("/fuzzy-filter")
    public MemesFilterResponse findMemesByKeyword(@RequestBody @Valid FuzzyFilterRequest request) {
        // >0 已经判断
        Integer page = request.getPage();
        Integer size = request.getSize();
        String keyword = request.getKeyword();
        if (page == null || size == null) {
            return keyword.isEmpty() ? memeService.getAllMemes() : memeService.getMemesByKeyword(keyword);
        }
        return keyword.isEmpty() ? memeService.getAllMemes(page, size) : memeService.getMemesByKeyword(keyword, page, size);
    }

    @PostMapping("/filter")
    public MemesFilterResponse findMemesByTags(@RequestBody @Valid TagFilterRequest request) {
        // >0 已经判断
        Integer page = request.getPage();
        Integer size = request.getSize();
        Set<String> tags = request.getTags();
        if (page == null || size == null) {
            return tags.isEmpty() ? memeService.getAllMemes() : memeService.getMemesByTagsSubset(tags);
        }
        return tags.isEmpty() ? memeService.getAllMemes(page, size) : memeService.getMemesByTagsSubset(tags, page, size);
    }

    @GetMapping("/{id}")
    public Meme getMeme(@PathVariable long id) {
        return memeService.getMeme(id);
    }

    @GetMapping("/{id}/raw")
    public void getMemeRaw(@PathVariable long id, HttpServletRequest req, HttpServletResponse resp) {
        memeService.getMemeRaw(id, req, resp);
    }

    @GetMapping("/{id}/thumbnail")
    public void getMemeThumbnail(@PathVariable long id, HttpServletRequest req, HttpServletResponse resp) {
        memeService.getMemeThumbnail(id, req, resp);
    }
}
