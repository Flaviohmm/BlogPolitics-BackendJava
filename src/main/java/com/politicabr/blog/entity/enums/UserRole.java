package com.politicabr.blog.entity.enums;

public enum UserRole {
    ADMIN("Administrador", "Acesso completo ao sistema"),
    AUTHOR("Autor", "Pode criar e editar posts"),
    EDITOR("Editor", "Pode editar posts de outros autores"),
    READER("Leitor", "Apenas leitura e comentários");

    private final String displayName;
    private final String description;

    UserRole(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public boolean hasAdminAccess() {
        return this == ADMIN;
    }

    public boolean canCreatePosts() {
        return this == ADMIN || this == AUTHOR || this == EDITOR;
    }

    public boolean canEditOthersPosts() {
        return this == ADMIN || this == EDITOR;
    }

    public boolean canDeletePosts() {
        return this == ADMIN;
    }

    public boolean canManageUsers() {
        return this == ADMIN;
    }

    public boolean canManageCategories() {
        return this == ADMIN || this == EDITOR;
    }

    public boolean canComment() {
        return true; // Todos podem comentar
    }
}
