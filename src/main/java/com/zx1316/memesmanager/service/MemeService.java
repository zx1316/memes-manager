package com.zx1316.memesmanager.service;

import com.luciad.imageio.webp.WebPReadParam;
import com.zx1316.memesmanager.exception.ResourceAlreadyExistsException;
import com.zx1316.memesmanager.exception.ResourceNotFoundException;
import com.zx1316.memesmanager.model.Meme;
import com.zx1316.memesmanager.model.MemeTag;
import com.zx1316.memesmanager.repository.MemeRepository;
import com.zx1316.memesmanager.controller.pojo.MemesFilterResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.detect.Detector;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ServerErrorException;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class MemeService {

    @Autowired
    private MemeRepository memeRepository;

    private final Detector detector;
    private final List<MediaType> acceptedTypes;

    public MemeService() {
        // 初始化 Tika 配置和检测器
        TikaConfig tikaConfig = TikaConfig.getDefaultConfig();
        detector = new DefaultDetector(tikaConfig.getMimeRepository());
        acceptedTypes = List.of(
                new MediaType("image", "png"),
                new MediaType("image", "jpeg"),
                new MediaType("image", "gif"),
                new MediaType("image", "webp")
        );
    }

    // 插入图片
    @Transactional
    public void insertMeme(MultipartFile file, Set<String> tags) {
        try {
            byte[] data = file.getBytes();
            MediaType mimeType;
            try (TikaInputStream stream = TikaInputStream.get(data)) {
                Metadata metadata = new Metadata(); // 创建元数据对象
                mimeType = MediaType.parseMediaType(detector.detect(stream, metadata).toString());    // 使用 Tika 检测文件类型
            }

            // 检查文件类型是否为图片
            if (!acceptedTypes.contains(mimeType)) {
                throw new UnsupportedMediaTypeStatusException(mimeType, acceptedTypes);
            }

            MessageDigest md = MessageDigest.getInstance("SHA");// 生成一个SHA-1加密计算摘要
            byte[] messageDigest = md.digest(data);
            StringBuilder sb = new StringBuilder();
            for (byte b : messageDigest) {
                sb.append(String.format("%02x", b));
            }
            String shaStr = sb.toString();

            memeRepository.findBySha(shaStr).ifPresent(meme -> {
                throw new ResourceAlreadyExistsException("Meme already exists");   // 一旦md5重复，抛出异常，就不会存储图片
            });

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            if (mimeType.toString().equals("image/webp")) {
                ImageInputStream iis = ImageIO.createImageInputStream(bais);
                ImageReader reader = ImageIO.getImageReaders(iis).next();   // 获取 WebP 图像的 ImageReader
                reader.setInput(iis);
                BufferedImage image = reader.read(0, new WebPReadParam());  // 读取图像
                Thumbnails.of(image)
                        .size(192, 192) // 设置缩略图大小，按等比缩放
                        .outputFormat("webp") // 输出格式为 WebP
                        .toOutputStream(baos);
            } else {
                Thumbnails.of(bais)
                        .size(192, 192) // 设置缩略图大小，按等比缩放
                        .toOutputStream(baos);
            }

            Meme meme = new Meme(null, shaStr, mimeType.getSubtype(), System.currentTimeMillis(), false, data, baos.toByteArray(), null);
            HashSet<MemeTag> set = new HashSet<>();
            tags.forEach(tag -> set.add(new MemeTag(tag, meme)));
            meme.setMemeTags(set);
            memeRepository.save(meme);
        } catch (IOException | NoSuchAlgorithmException e) {
            throw new ServerErrorException(e.getMessage(), e);
        }
    }

    // 删除图片
    @Transactional
    public void deleteMeme(long id) {
        // 自动级联删除 image_tag 记录
        memeRepository.deleteById(id);
    }

    // 修改图片
    @Transactional
    public void updateMeme(long id, boolean favorite, Set<String> tags) {
        Meme meme = memeRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
        meme.setFavorite(favorite);
        Set<MemeTag> set = meme.getMemeTags();
        set.clear();
        tags.forEach(tag -> set.add(new MemeTag(tag, meme)));
        memeRepository.save(meme);
    }

    // 查询拥有的标签是给定标签集合子集的图片
    public MemesFilterResponse getMemesByTagsSubset(Set<String> tags, int page, int size) {
        Page<Long> result = memeRepository.findIdsByTagsSubset(tags, tags.size(), PageRequest.of(page - 1, size));
        return new MemesFilterResponse(result.getTotalElements(), result.toList());
    }

    public MemesFilterResponse getMemesByTagsSubset(Set<String> tags) {
        List<Long> result = memeRepository.findIdsByTagsSubset(tags, tags.size());
        return new MemesFilterResponse(result.size(), result);
    }

    // 模糊查询图片
    public MemesFilterResponse getMemesByKeyword(String keyword, int page, int size) {
        Page<Long> result = memeRepository.findIdsByKeyword(keyword, PageRequest.of(page - 1, size));
        return new MemesFilterResponse(result.getTotalElements(), result.toList());
    }

    public MemesFilterResponse getMemesByKeyword(String keyword) {
        List<Long> result = memeRepository.findIdsByKeyword(keyword);
        return new MemesFilterResponse(result.size(), result);
    }

    // 所有图片
    public MemesFilterResponse getAllMemes(int page, int size) {
        Page<Long> result = memeRepository.findAllIdsPageable(PageRequest.of(page - 1, size));
        return new MemesFilterResponse(result.getTotalElements(), result.toList());
    }

    public MemesFilterResponse getAllMemes() {
        List<Long> result = memeRepository.findAllIdsPageable();
        return new MemesFilterResponse(result.size(), result);
    }

    // 获取图片信息
    @Transactional
    public Meme getMeme(long id) {
        return memeRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
    }

    // 获取图片原图
    @Transactional
    public void getMemeRaw(long id, HttpServletRequest req, HttpServletResponse resp) {
        Meme meme = memeRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
        String etag = req.getHeader("If-None-Match");
        if (etag != null && etag.equals(meme.getSha())) {
            resp.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            return;
        }
        resp.setContentType("image/" + meme.getFormat());
        resp.setHeader("Cache-Control", "public, max-age=0");
        resp.setHeader("ETag", meme.getSha());
        try {
            resp.getOutputStream().write(meme.getImage());
        } catch (IOException e) {
            throw new ServerErrorException(e.getMessage(), e);
        }
    }

    // 获取图片缩略图
    @Transactional
    public void getMemeThumbnail(long id, HttpServletRequest req, HttpServletResponse resp) {
        Meme meme = memeRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
        String etag = req.getHeader("If-None-Match");
        if (etag != null && etag.equals(meme.getSha())) {
            resp.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            return;
        }
        resp.setContentType("image/" + meme.getFormat());
        resp.setHeader("Cache-Control", "public, max-age=0");
        resp.setHeader("ETag", meme.getSha());
        try {
            resp.getOutputStream().write(meme.getThumbnail());
        } catch (IOException e) {
            throw new ServerErrorException(e.getMessage(), e);
        }
    }
}
