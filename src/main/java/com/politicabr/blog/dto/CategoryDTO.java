package com.politicabr.blog.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class CategoryDTO {

    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100)
    private String name;

    @NotBlank(message = "Slug é obrigatório")
    @Size(max = 100)
    private String slug;

    private String description;
    private String color;
    private String icon;
    private Boolean active;
    private Integer displayOrder;
    private Long postCount;  // Quantidade de posts nesta categoria

    @JsonFormat(pattern = "dd/MM/yyyy'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "dd/MM/yyyy'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    public CategoryDTO() {
    }

    public CategoryDTO(Long id, String name, String slug, String description, String color, String icon, Long postCount) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.description = description;
        this.color = color;
        this.icon = icon;
        this.postCount = postCount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public Long getPostCount() {
        return postCount;
    }

    public void setPostCount(Long postCount) {
        this.postCount = postCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
