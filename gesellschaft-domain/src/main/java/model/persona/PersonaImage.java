package model.persona;

import lombok.Getter;

import java.util.Objects;

/**
 * 인격 이미지 (Value Object)
 * - 불변 객체
 * - Persona에 종속
 */
@Getter
public final class PersonaImage {
    private final Long id;              // nullable
    private final ImageType type;
    private final String url;
    private final int priority;         // 우선순위 (정렬용)
    private final boolean isPrimary;    // 대표 이미지 여부

    private PersonaImage(Long id, ImageType type, String url, int priority, boolean isPrimary) {
        this.id = id;
        this.type = Objects.requireNonNull(type, "type must not be null");
        this.url = Objects.requireNonNull(url, "url must not be null");
        this.priority = priority;
        this.isPrimary = isPrimary;
    }

    // Factory
    public static PersonaImage create(ImageType type, String url, int priority, boolean isPrimary) {
        return new PersonaImage(null, type, url, priority, isPrimary);
    }

    public static PersonaImage create(ImageType type, String url) {
        return new PersonaImage(null, type, url, 0, false);
    }

    // ID 할당
    public PersonaImage withId(Long id) {
        if (this.id != null) {
            throw new IllegalStateException("ID already assigned");
        }
        return new PersonaImage(id, this.type, this.url, this.priority, this.isPrimary);
    }

    // 대표 이미지로 설정
    public PersonaImage markAsPrimary() {
        return new PersonaImage(this.id, this.type, this.url, this.priority, true);
    }

    // 우선순위 변경
    public PersonaImage withPriority(int priority) {
        return new PersonaImage(this.id, this.type, this.url, priority, this.isPrimary);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PersonaImage that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "PersonaImage{" +
               "type=" + type +
               ", url='" + url + '\'' +
               ", isPrimary=" + isPrimary +
               '}';
    }

    // Builder
    public static Builder builder(ImageType type, String url) {
        return new Builder(type, url);
    }

    public static class Builder {
        private final ImageType type;
        private final String url;
        private int priority = 0;
        private boolean isPrimary = false;

        private Builder(ImageType type, String url) {
            this.type = type;
            this.url = url;
        }

        public Builder priority(int priority) {
            this.priority = priority;
            return this;
        }

        public Builder isPrimary(boolean isPrimary) {
            this.isPrimary = isPrimary;
            return this;
        }

        public PersonaImage build() {
            return new PersonaImage(null, type, url, priority, isPrimary);
        }
    }
}
