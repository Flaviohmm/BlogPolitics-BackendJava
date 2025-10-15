package com.politicabr.blog.service;

import com.politicabr.blog.dto.TagDTO;
import com.politicabr.blog.entity.Tag;
import com.politicabr.blog.exception.ResourceNotFoundException;
import com.politicabr.blog.mapper.TagMapper;
import com.politicabr.blog.repository.TagRepository;
import com.politicabr.blog.util.SlugUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class TagService {

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private SlugUtil slugUtil;

    @Transactional(readOnly = true)
    @Cacheable(value = "tags")
    public List<TagDTO> getAllTags() {
        List<Object[]> results = tagRepository.findAllWithPostCount();
        return results.stream()
                .map(result -> {
                    Tag tag = (Tag) result[0];
                    Long count = result.length > 1 ? (Long) result[1] : 0L;

                    TagDTO dto = tagMapper.toDTO(tag);
                    dto.setPostCount(count);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "popularTags")
    public List<TagDTO> getPopularTags(int limit) {
        List<Object[]> results = tagRepository.findPopularTags();
        return results.stream()
                .limit(limit)
                .map(result -> {
                    Tag tag = (Tag) result[0];
                    Long count = result.length > 1 ? (Long) result[1] : 0L;

                    TagDTO dto = tagMapper.toDTO(tag);
                    dto.setPostCount(count);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public TagDTO getTagById(Long id) {
        Tag tag = tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tag não encontrada"));
        return tagMapper.toDTO(tag);
    }

    @Transactional(readOnly = true)
    public TagDTO getTagBySlug(String slug) {
        Tag tag = tagRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Tag não encontrada: " + slug));
        return tagMapper.toDTO(tag);
    }

    @Transactional(readOnly = true)
    public List<TagDTO> searchTags(String query) {
        return tagRepository.findByNameContainingIgnoreCase(query)
                .stream()
                .map(tagMapper::toDTO)
                .collect(Collectors.toList());
    }

    @CacheEvict(value = {"tags", "popularTags"}, allEntries = true)
    public TagDTO createTag(TagDTO tagDTO) {
        Tag tag = tagMapper.toEntity(tagDTO);

        // Gerar slug se não fornecido
        if (tag.getSlug() == null || tag.getSlug().isEmpty()) {
            String baseSlug = slugUtil.generateSlug(tag.getName());
            tag.setSlug(generateUniqueSlug(baseSlug));
        }

        Tag savedTag = tagRepository.save(tag);
        return tagMapper.toDTO(savedTag);
    }

    @CacheEvict(value = {"tags", "popularTags"}, allEntries = true)
    public TagDTO updateTag(Long id, TagDTO tagDTO) {
        Tag existingTag = tagRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tag não encontrada"));

        existingTag.setName(tagDTO.getName());

        // Atualizar slug se nome mudou
        if (!tagDTO.getName().equals(existingTag.getName())) {
            String newSlug = slugUtil.generateSlug(tagDTO.getName());
            if (!newSlug.equals(existingTag.getSlug())) {
                existingTag.setSlug(generateUniqueSlug(newSlug));
            }
        }

        Tag savedTag = tagRepository.save(existingTag);
        return tagMapper.toDTO(savedTag);
    }

    @CacheEvict(value = {"tags", "popularTags"}, allEntries = true)
    public void deleteTag(Long id) {
        if (!tagRepository.existsById(id)) {
            throw new ResourceNotFoundException("Tag não encontrada");
        }
        tagRepository.deleteById(id);
    }

    private String generateUniqueSlug(String baseSlug) {
        String slug = baseSlug;
        int counter = 1;

        while (tagRepository.existsBySlug(slug)) {
            slug = baseSlug + "-" + counter++;
        }

        return slug;
    }
}
