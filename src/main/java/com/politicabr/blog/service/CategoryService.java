package com.politicabr.blog.service;

import com.politicabr.blog.dto.CategoryDTO;
import com.politicabr.blog.entity.Category;
import com.politicabr.blog.exception.ResourceNotFoundException;
import com.politicabr.blog.mapper.CategoryMapper;
import com.politicabr.blog.repository.CategoryRepository;
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
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private SlugUtil slugUtil;

    @Transactional(readOnly = true)
    @Cacheable(value = "categories")
    public List<CategoryDTO> getAllActiveCategories() {
        return categoryRepository.findByActiveTrueOrderByDisplayOrderAsc()
                .stream()
                .map(categoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "categoriesWithCount")
    public List<CategoryDTO> getCategoriesWithPostCount() {
        List<Object[]> results = categoryRepository.findActiveCategoriesWithPostCount();
        return results.stream()
                .map(result -> {
                    Category category = (Category) result[0];
                    Long count = result.length > 1 ? (Long) result[1] : 0L;

                    CategoryDTO dto = categoryMapper.toDTO(category);
                    dto.setPostCount(count);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoryDTO getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));
        return categoryMapper.toDTO(category);
    }

    @Transactional(readOnly = true)
    public CategoryDTO getCategoryBySlug(String slug) {
        Category category = categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada: " + slug));
        return categoryMapper.toDTO(category);
    }

    @CacheEvict(value = {"categories", "categoriesWithCount"}, allEntries = true)
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Category category = categoryMapper.toEntity(categoryDTO);

        // Gerar slug se não fornecido
        if (category.getSlug() == null || category.getSlug().isEmpty()) {
            String baseSlug = slugUtil.generateSlug(category.getName());
            category.setSlug(generateUniqueSlug(baseSlug));
        }

        Category savedCategory = categoryRepository.save(category);
        return categoryMapper.toDTO(savedCategory);
    }

    @CacheEvict(value = {"categories", "categoriesWithCount"}, allEntries = true)
    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO) {
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));

        existingCategory.setName(categoryDTO.getName());
        existingCategory.setDescription(categoryDTO.getDescription());
        existingCategory.setColor(categoryDTO.getColor());
        existingCategory.setIcon(categoryDTO.getIcon());
        existingCategory.setActive(categoryDTO.getActive());
        existingCategory.setDisplayOrder(categoryDTO.getDisplayOrder());

        // Atualizar slug se nome mudou
        if (!categoryDTO.getName().equals(existingCategory.getName())) {
            String newSlug = slugUtil.generateSlug(categoryDTO.getName());
            if (!newSlug.equals(existingCategory.getSlug())) {
                existingCategory.setSlug(generateUniqueSlug(newSlug));
            }
        }

        Category savedCategory = categoryRepository.save(existingCategory);
        return categoryMapper.toDTO(savedCategory);
    }

    @CacheEvict(value = {"categories", "categoriesWithCount"}, allEntries = true)
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Categoria não encontrada");
        }
        categoryRepository.deleteById(id);
    }

    private String generateUniqueSlug(String baseSlug) {
        String slug = baseSlug;
        int count = 1;

        while (categoryRepository.existsBySlug(slug)) {
            slug = baseSlug + "-" + count++;
        }

        return slug;
    }
}
