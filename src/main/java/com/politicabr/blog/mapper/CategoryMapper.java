package com.politicabr.blog.mapper;

import com.politicabr.blog.dto.CategoryDTO;
import com.politicabr.blog.entity.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

    public CategoryDTO toDTO(Category category) {
        if (category == null) {
            return null;
        }

        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setSlug(category.getSlug());
        dto.setDescription(category.getDescription());
        dto.setColor(category.getColor());
        dto.setIcon(category.getIcon());
        dto.setActive(category.getActive());
        dto.setDisplayOrder(category.getDisplayOrder());
        dto.setCreatedAt(category.getCreatedAt());
        dto.setUpdatedAt(category.getUpdatedAt());

        // Contar posts publicados
        if (category.getPosts() != null) {
            long count = category.getPosts().stream()
                    .filter(post -> Boolean.TRUE.equals(post.getPublished()))
                    .count();
            dto.setPostCount(count);
        } else {
            dto.setPostCount(0L);
        }

        return dto;
    }

    public Category toEntity(CategoryDTO dto) {
        if (dto == null) {
            return null;
        }

        Category category = new Category();
        category.setId(dto.getId());
        category.setName(dto.getName());
        category.setSlug(dto.getSlug());
        category.setDescription(dto.getDescription());
        category.setColor(dto.getColor());
        category.setIcon(dto.getIcon());
        category.setActive(dto.getActive());
        category.setDisplayOrder(dto.getDisplayOrder());

        return category;
    }
}
