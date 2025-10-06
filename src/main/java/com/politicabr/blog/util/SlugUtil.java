package com.politicabr.blog.util;

import org.springframework.stereotype.Component;

import java.text.Normalizer;
import java.util.Locale;
import java.util.function.Function;
import java.util.regex.Pattern;

@Component
public class SlugUtil {

    private static final Pattern NON_LATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");
    private static final Pattern EDGES_DASHES = Pattern.compile("(^-|-$)");
    private static final Pattern MULTIPLE_DASHES = Pattern.compile("-+");

    /**
     * Gera um slug a partir de uma string
     * Exemplo: "Política Nacional" -> "politica-nacional"
     */
    public String generateSlug(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        // Converter para minúsculas
        String slug = input.toLowerCase(Locale.ROOT);

        // Remover acentos e caracteres especiais
        slug = Normalizer.normalize(slug, Normalizer.Form.NFD);
        slug = slug.replaceAll("\\p{M}", "");

        // Substituir espaços por hífens
        slug = WHITESPACE.matcher(slug).replaceAll("-");

        // Remover caracteres não alfanuméricos (exceto hífens)
        slug = NON_LATIN.matcher(slug).replaceAll("");

        // Remover múltiplos hífens consecutivos
        slug = MULTIPLE_DASHES.matcher(slug).replaceAll("-");

        // Remover hífens no início e fim
        slug = EDGES_DASHES.matcher(slug).replaceAll("");

        return slug;
    }

    /**
     * Gera um slug único verificando se já existe
     */
    public String generateUniqueSlug(String baseSlug, Function<String, Boolean> existsChecker) {
        String slug = baseSlug;
        int counter = 1;

        while (existsChecker.apply(slug)) {
            slug = baseSlug + "-" + counter++;
        }

        return slug;
    }

    /**
     * Valida se um slug está no formato correto
     */
    public boolean isValidSlug(String slug) {
        if (slug == null || slug.isEmpty()) {
            return false;
        }

        // Slug deve conter apenas letras minúsculas, números e hífens
        // Não pode começar ou terminar com hífen
        return slug.matches("^[a-z0-9]+(?:-[a-z0-9]+)*$");
    }
}
