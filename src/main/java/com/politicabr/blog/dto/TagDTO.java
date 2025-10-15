package com.politicabr.blog.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class TagDTO {

    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 50)
    private String name;

    @NotBlank(message = "Slug é obrigatório")
    @Size(max = 50)
    private String slug;

    private Long postCount;

    @JsonFormat(pattern = "dd/MM/yyyy'T'HH:mm:ss")
    private LocalDateTime createdAt;

    public TagDTO() {
    }

    public TagDTO(Long id, String name, String slug, Long postCount) {
        this.id = id;
        this.name = name;
        this.slug = slug;
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
}
