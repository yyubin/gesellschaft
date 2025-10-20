package model.passive;

import lombok.Getter;
import model.skill.SyncLevel;

import java.util.Objects;
import java.util.Optional;

/**
 * 인격 패시브 (Entity)
 * - Persona의 하위 Entity
 * - 일반 패시브 또는 서포트 패시브
 */
@Getter
public final class PersonaPassive {
    private final Long id;                      // nullable
    private final String name;
    private final PassiveKind kind;             // NORMAL / SUPPORT
    private final SyncLevel syncLevel;          // SUPPORT일 때만 (SYNC_3 or SYNC_4)
    private final PassiveCondition condition;   // nullable (발동 조건)
    private final PassiveEffect effect;

    private PersonaPassive(Long id, String name, PassiveKind kind,
                          SyncLevel syncLevel, PassiveCondition condition,
                          PassiveEffect effect) {
        this.id = id;
        this.name = Objects.requireNonNull(name);
        this.kind = Objects.requireNonNull(kind);
        this.syncLevel = syncLevel;
        this.condition = condition;
        this.effect = Objects.requireNonNull(effect);

        // 검증: SUPPORT일 때는 syncLevel 필수
        if (kind == PassiveKind.SUPPORT && syncLevel == null) {
            throw new IllegalArgumentException("SUPPORT passive requires syncLevel");
        }

        // 검증: NORMAL일 때는 syncLevel 없어야 함
        if (kind == PassiveKind.NORMAL && syncLevel != null) {
            throw new IllegalArgumentException("NORMAL passive should not have syncLevel");
        }
    }

    // Factory - NORMAL 패시브
    public static PersonaPassive createNormal(String name,
                                             PassiveCondition condition,
                                             PassiveEffect effect) {
        return new PersonaPassive(null, name, PassiveKind.NORMAL,
                                 null, condition, effect);
    }

    // Factory - SUPPORT 패시브
    public static PersonaPassive createSupport(String name,
                                              SyncLevel syncLevel,
                                              PassiveCondition condition,
                                              PassiveEffect effect) {
        return new PersonaPassive(null, name, PassiveKind.SUPPORT,
                                 syncLevel, condition, effect);
    }

    // ID 할당
    public PersonaPassive withId(Long id) {
        if (this.id != null) {
            throw new IllegalStateException("ID already assigned");
        }
        return new PersonaPassive(id, this.name, this.kind,
                                 this.syncLevel, this.condition, this.effect);
    }

    // Getter with Optional
    public Optional<SyncLevel> getSyncLevel() {
        return Optional.ofNullable(syncLevel);
    }

    public Optional<PassiveCondition> getCondition() {
        return Optional.ofNullable(condition);
    }

    // 패시브가 활성화되는 동기화 레벨 확인
    public boolean isActiveAtSyncLevel(SyncLevel level) {
        if (kind == PassiveKind.NORMAL) {
            return true;  // 일반 패시브는 항상 활성
        }
        return this.syncLevel == level;  // 서포트 패시브는 특정 레벨에만
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PersonaPassive that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "PersonaPassive{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", kind=" + kind +
               ", syncLevel=" + syncLevel +
               '}';
    }

    // Builder
    public static Builder builder(String name, PassiveKind kind) {
        return new Builder(name, kind);
    }

    public static class Builder {
        private final String name;
        private final PassiveKind kind;
        private SyncLevel syncLevel;
        private PassiveCondition condition;
        private PassiveEffect effect;

        private Builder(String name, PassiveKind kind) {
            this.name = name;
            this.kind = kind;
        }

        public Builder syncLevel(SyncLevel syncLevel) {
            this.syncLevel = syncLevel;
            return this;
        }

        public Builder condition(PassiveCondition condition) {
            this.condition = condition;
            return this;
        }

        public Builder effect(PassiveEffect effect) {
            this.effect = effect;
            return this;
        }

        public PersonaPassive build() {
            if (effect == null) {
                throw new IllegalStateException("effect must not be null");
            }

            if (kind == PassiveKind.SUPPORT && syncLevel == null) {
                throw new IllegalStateException("SUPPORT passive requires syncLevel");
            }

            if (kind == PassiveKind.NORMAL && syncLevel != null) {
                throw new IllegalStateException("NORMAL passive should not have syncLevel");
            }

            return new PersonaPassive(null, name, kind, syncLevel, condition, effect);
        }
    }
}
