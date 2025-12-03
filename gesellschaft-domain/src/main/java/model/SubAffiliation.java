package model;

import lombok.Getter;

import java.util.Objects;

@Getter
public final class SubAffiliation {
    private final Long id;
    private final String name;
    private final MainAffiliationCategory mainCategory;

    public SubAffiliation(Long id, String name, MainAffiliationCategory mainCategory) {
        this.id = id;
        this.name = Objects.requireNonNull(name, "name must not be null");
        this.mainCategory = Objects.requireNonNull(mainCategory, "mainCategory must not be null");
    }

    public static SubAffiliation create(String name, MainAffiliationCategory mainCategory) {
        return new SubAffiliation(null, name, mainCategory);
    }

    public SubAffiliation withId(Long id) {
        if (this.id != null) {
            throw new IllegalStateException("ID already assigned");
        }
        return new SubAffiliation(id, this.name, this.mainCategory);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SubAffiliation that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "SubAffiliation{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", mainCategory=" + mainCategory +
               '}';
    }
}
