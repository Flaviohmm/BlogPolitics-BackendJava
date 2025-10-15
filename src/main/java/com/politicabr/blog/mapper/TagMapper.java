package com.politicabr.blog.mapper;

import com.politicabr.blog.dto.TagDTO;
import com.politicabr.blog.entity.Tag;
import org.springframework.stereotype.Component;

@Component
public class TagMapper {

    public TagDTO toDTO(Tag tag) {
        if (tag == null) {
            return null;
        }

        TagDTO dto = new TagDTO();
        dto.setId(tag.getId());
        dto.setName(tag.getName());
        dto.setSlug(tag.getSlug());
        dto.setCreatedAt(tag.getCreatedAt());

        // Contar posts
        if (tag.getPosts() != null) {
            long count = tag.getPosts().stream()
                    .filter(post -> Boolean.TRUE.equals(post.getPublished()))
                    .count();
            dto.setPostCount(count);
        } else {
            dto.setPostCount(0L);
        }

        return dto;
    }

    public Tag toEntity(TagDTO dto) {
        if (dto == null) {
            return null;
        }

        Tag tag = new Tag();
        tag.setId(dto.getId());
        tag.setName(dto.getName());
        tag.setSlug(dto.getSlug());

        return tag;
    }
}
