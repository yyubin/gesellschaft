package model.persona;

import model.AttackType;
import model.GradeType;
import model.KeywordType;
import model.SinAffinity;
import model.passive.ConditionType;
import model.passive.PassiveCondition;
import model.passive.PassiveEffect;
import model.passive.PersonaPassive;
import model.skill.*;
import model.skill.effect.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Persona 전체 구조 사용 예시
 * - Skill (동기화별 스탯 + 코인 효과)
 * - PersonaPassive (일반 + 서포트)
 */
public class PersonaExample {

    /**
     * 예시 1: 완전한 인격 (스킬 3개 + 패시브 2개)
     *
     * 인격: "잔혹한 검사"
     * - 스킬 1: "참격" (3코인, 출혈)
     * - 스킬 2: "회전 베기" (2코인, 출혈)
     * - 스킬 3: "반격의 자세" (방어, 1코인)
     * - 패시브 1: "전투 준비" (일반)
     * - 패시브 2: "출혈 공명" (서포트 SYNC_3)
     */
    public static Persona example1_완전한인격() {
        // === 스킬 1: 참격 ===
        var skill1Effect = SkillEffect.builder(
                new SkillTrigger(TriggerCode.ON_USE, null)
            )
            .originalText("[사용시] 대상의 출혈 3 당 코인 위력 +1 (최대 3)")
            .branches(EffectBranch.builder()
                .actions(EffectAction.builder(ActionType.COIN_POWER_UP, "COIN_POWER")
                    .formulaAmount("TARGET.BLEED / 3", ActionUnit.FLAT)
                    .cap(3)
                    .scope(ActionScope.COINS_SKILL)
                    .build())
                .build())
            .build();

        var coin1_1 = SkillCoin.builder(0)
            .coinEffects(CoinEffect.builder(
                    new SkillTrigger(TriggerCode.ON_HIT, null)
                )
                .branches(EffectBranch.builder()
                    .actions(EffectAction.builder(ActionType.STATUS_INFLICT, "BLEED")
                        .flatAmount(2, ActionUnit.STACK)
                        .targetSelector(TargetSelector.allEnemies())
                        .build())
                    .build())
                .build())
            .build();

        var coin1_2 = SkillCoin.builder(1)
            .coinEffects(CoinEffect.builder(
                    new SkillTrigger(TriggerCode.ON_HIT, null)
                )
                .branches(EffectBranch.builder()
                    .actions(EffectAction.builder(ActionType.STATUS_INFLICT, "BLEED")
                        .flatAmount(3, ActionUnit.STACK)
                        .targetSelector(TargetSelector.allEnemies())
                        .build())
                    .build())
                .build())
            .build();

        var coin1_3 = SkillCoin.builder(2)
            .coinEffects(CoinEffect.builder(
                    new SkillTrigger(TriggerCode.ON_HEAD_HIT, null)
                )
                .branches(EffectBranch.builder()
                    .actions(EffectAction.builder(ActionType.STATUS_INFLICT, "BLEED")
                        .flatAmount(5, ActionUnit.STACK)
                        .targetSelector(TargetSelector.allEnemies())
                        .build())
                    .build())
                .build())
            .build();

        var skill1Stats = SkillStatsBySync.builder(SyncLevel.SYNC_3)
            .basePower(4)
            .coinPower(6)
            .coinCount(3)
            .weight(1)
            .level(3)
            .skillEffects(List.of(skill1Effect))
            .skillCoins(List.of(coin1_1, coin1_2, coin1_3))
            .build();

        var skill1 = Skill.builder()
            .name("참격")
            .skillNumber(1)
            .skillCategory(SkillCategoryType.ATTACK)
            .sinAffinity(SinAffinity.WRATH)
            .keywordType(KeywordType.BLEED)
            .skillQuantity(3)
            .attackType(AttackType.SLASH)
            .statsBySync(List.of(skill1Stats))
            .build();

        // === 스킬 2: 회전 베기 (간단 버전) ===
        var skill2Coin = SkillCoin.builder(0)
            .coinEffects(CoinEffect.builder(
                    new SkillTrigger(TriggerCode.ON_HIT, null)
                )
                .branches(EffectBranch.builder()
                    .actions(EffectAction.builder(ActionType.STATUS_INFLICT, "BLEED")
                        .flatAmount(4, ActionUnit.STACK)
                        .targetSelector(TargetSelector.allEnemies())
                        .build())
                    .build())
                .build())
            .build();

        var skill2Stats = SkillStatsBySync.builder(SyncLevel.SYNC_3)
            .basePower(5)
            .coinPower(8)
            .coinCount(2)
            .weight(1)
            .level(3)
            .skillCoins(List.of(skill2Coin))
            .build();

        var skill2 = Skill.builder()
            .name("회전 베기")
            .skillNumber(2)
            .skillCategory(SkillCategoryType.ATTACK)
            .sinAffinity(SinAffinity.WRATH)
            .keywordType(KeywordType.BLEED)
            .skillQuantity(2)
            .attackType(AttackType.SLASH)
            .statsBySync(List.of(skill2Stats))
            .build();

        // === 스킬 3: 반격의 자세 (방어) ===
        var skill3Effect = SkillEffect.builder(
                new SkillTrigger(TriggerCode.ON_WIN_CLASH, null)
            )
            .branches(EffectBranch.builder()
                .actions(EffectAction.builder(ActionType.BUFF_DAMAGE_UP, "STR")
                    .flatAmount(1, ActionUnit.STACK)
                    .targetSelector(TargetSelector.self())
                    .build())
                .build())
            .build();

        var skill3Coin = SkillCoin.builder(0)
            .coinEffects(CoinEffect.builder(
                    new SkillTrigger(TriggerCode.ON_WIN_CLASH_HIT, null)
                )
                .branches(EffectBranch.builder()
                    .actions(EffectAction.builder(ActionType.STATUS_INFLICT, "BLEED")
                        .flatAmount(4, ActionUnit.STACK)
                        .targetSelector(TargetSelector.allEnemies())
                        .build())
                    .build())
                .build())
            .build();

        var skill3Stats = SkillStatsBySync.builder(SyncLevel.SYNC_3)
            .basePower(0)
            .coinPower(10)
            .coinCount(1)
            .weight(1)
            .level(3)
            .skillEffects(List.of(skill3Effect))
            .skillCoins(List.of(skill3Coin))
            .build();

        var skill3 = Skill.builder()
            .name("반격의 자세")
            .skillNumber(3)
            .skillCategory(SkillCategoryType.DEFENSE)
            .sinAffinity(SinAffinity.PRIDE)
            .keywordType(KeywordType.BLEED)
            .defenseType(DefenseType.COUNTER)
            .statsBySync(List.of(skill3Stats))
            .build();

        // === 패시브 1: 전투 준비 (일반) ===
        var passive1 = PersonaPassive.builder("전투 준비", model.passive.PassiveKind.NORMAL)
            .effect(PassiveEffect.builder(
                    new SkillTrigger(TriggerCode.ON_BATTLE_START, null)
                )
                .branches(EffectBranch.builder()
                    .actions(EffectAction.builder(ActionType.BUFF_DAMAGE_UP, "STR")
                        .flatAmount(2, ActionUnit.STACK)
                        .targetSelector(TargetSelector.self())
                        .build())
                    .build())
                .build())
            .build();

        // === 패시브 2: 출혈 공명 (서포트 SYNC_3) ===
        var passive2 = PersonaPassive.builder("출혈 공명", model.passive.PassiveKind.SUPPORT)
            .syncLevel(SyncLevel.SYNC_3)
            .condition(PassiveCondition.resonate(SinAffinity.PRIDE, 4))
            .effect(PassiveEffect.builder(
                    new SkillTrigger(TriggerCode.ON_ALLY_HIT, null)
                )
                .branches(EffectBranch.builder()
                    .actions(EffectAction.builder(ActionType.STATUS_INFLICT, "BLEED")
                        .flatAmount(1, ActionUnit.STACK)
                        .targetSelector(TargetSelector.allEnemies())
                        .build())
                    .build())
                .build())
            .build();

        // === Persona 생성 ===
        return Persona.builder()
            .name("잔혹한 검사")
            .nameEn("Cruel Prosecutor")
            .grade(GradeType.THREE)
            .releaseDate(LocalDate.of(2024, 1, 15))
            .maxLevel(50)
            .resistanceInfo(new ResistanceInfo(
                ResistanceType.NORMAL, ResistanceType.WEAK, ResistanceType.RESIST
            ))
            .speedInfo(new SpeedInfo(3, 5))
            .healthInfo(new HealthInfo(100, 1.1, 20, 40, 60))
            .seasonInfo(new SeasonInfo(SeasonType.SEASON_NORMAL, 4))
            .skills(List.of(skill1, skill2, skill3))
            .passives(List.of(passive1, passive2))
            .images(List.of(
                PersonaImage.builder(ImageType.A, "https://example.com/persona1_a.png")
                    .isPrimary(true)
                    .priority(1)
                    .build(),
                PersonaImage.builder(ImageType.AC, "https://example.com/persona1_ac.png")
                    .priority(2)
                    .build()
            ))
            .build();
    }

    /**
     * 예시 2: 동기화 레벨별 스킬 차이
     *
     * 같은 스킬이 SYNC_3와 SYNC_4에서 다른 수치
     */
    public static Persona example2_동기화레벨차이() {
        var coin = SkillCoin.builder(0)
            .coinEffects(CoinEffect.builder(
                    new SkillTrigger(TriggerCode.ON_HIT, null)
                )
                .branches(EffectBranch.builder()
                    .actions(EffectAction.builder(ActionType.STATUS_INFLICT, "BURN")
                        .flatAmount(2, ActionUnit.STACK)
                        .targetSelector(TargetSelector.allEnemies())
                        .build())
                    .build())
                .build())
            .build();

        // SYNC_3: 위력 낮음
        var sync3 = SkillStatsBySync.builder(SyncLevel.SYNC_3)
            .basePower(3)
            .coinPower(5)
            .coinCount(1)
            .weight(1)
            .level(2)
            .skillCoins(List.of(coin))
            .build();

        // SYNC_4: 위력 높음
        var sync4 = SkillStatsBySync.builder(SyncLevel.SYNC_4)
            .basePower(4)
            .coinPower(7)
            .coinCount(1)
            .weight(1)
            .level(3)
            .skillCoins(List.of(coin))
            .build();

        var skill = Skill.builder()
            .name("화염 방출")
            .skillNumber(1)
            .skillCategory(SkillCategoryType.ATTACK)
            .sinAffinity(SinAffinity.WRATH)
            .keywordType(KeywordType.BURN)
            .skillQuantity(1)
            .attackType(AttackType.PIERCE)
            .statsBySync(List.of(sync3, sync4))  // 두 레벨 모두
            .build();

        return Persona.builder()
            .name("화염술사")
            .grade(GradeType.TWO)
            .skills(List.of(skill))
            .build();
    }

    /**
     * 예시 3: 이미지 활용
     */
    public static void example3_이미지활용() {
        var persona = example1_완전한인격();

        // 1. 대표 이미지 조회
        persona.getPrimaryImage()
            .ifPresent(img -> {
                System.out.println("대표 이미지: " + img.getType() + " - " + img.getUrl());
            });

        // 2. 특정 타입 이미지 조회
        var coreImages = persona.getImagesByType(ImageType.AC);
        System.out.println("코어파트 이미지 개수: " + coreImages.size());

        // 3. 이미지 추가
        var newImage = PersonaImage.create(ImageType.SD, "https://example.com/sd.png");
        var updated = persona.addImage(newImage);

        System.out.println("원본 이미지 개수: " + persona.getImages().size());  // 2
        System.out.println("새 객체 이미지 개수: " + updated.getImages().size());  // 3
    }

    /**
     * 예시 4: Persona 메서드 활용
     */
    public static void example4_메서드활용() {
        var persona = example1_완전한인격();

        // 1. 특정 스킬 조회
        persona.getSkillByNumber(1)
            .ifPresent(skill -> {
                System.out.println("스킬 1: " + skill.getName());

                // 동기화 3 스탯 조회
                skill.getStatsBySyncLevel(SyncLevel.SYNC_3)
                    .ifPresent(stats -> {
                        System.out.println("  기본 위력: " + stats.getBasePower());
                        System.out.println("  코인 개수: " + stats.getCoinCount());

                        // 코인별 효과 확인
                        stats.getSkillCoins().forEach(coin -> {
                            System.out.println("  코인 " + coin.getOrderIndex() + ": " +
                                             coin.getCoinEffects().size() + "개 효과");
                        });
                    });
            });

        // 2. 동기화 3에서 활성 패시브 조회
        var activePassives = persona.getActivePassivesAtSyncLevel(SyncLevel.SYNC_3);
        System.out.println("\n동기화 3 활성 패시브: " + activePassives.size() + "개");
        activePassives.forEach(p -> {
            System.out.println("  - " + p.getName() + " (" + p.getKind() + ")");
            p.getCondition().ifPresent(cond -> {
                System.out.println("    조건: " + cond);
            });
        });

        // 3. 일반 패시브만 조회
        var normalPassives = persona.getNormalPassives();
        System.out.println("\n일반 패시브: " + normalPassives.size() + "개");

        // 4. 서포트 패시브만 조회
        var supportPassives = persona.getSupportPassives();
        System.out.println("서포트 패시브: " + supportPassives.size() + "개");
    }

    /**
     * 예시 5: 불변성 테스트
     */
    public static void example5_불변성테스트() {
        var original = example1_완전한인격();

        // 스킬 추가 → 새 객체 반환
        var newSkill = Skill.builder()
            .name("추가 스킬")
            .skillNumber(4)
            .skillCategory(SkillCategoryType.ATTACK)
            .sinAffinity(SinAffinity.ENVY)
            .keywordType(KeywordType.NONE)
            .build();

        var updated = original.addSkill(newSkill);

        System.out.println("원본 스킬 개수: " + original.getSkills().size());  // 3
        System.out.println("새 객체 스킬 개수: " + updated.getSkills().size());  // 4
        System.out.println("원본 불변 확인: " + (original.getSkills().size() == 3));

        // 패시브 추가
        var newPassive = PersonaPassive.builder("새 패시브", model.passive.PassiveKind.NORMAL)
            .effect(PassiveEffect.builder(
                    new SkillTrigger(TriggerCode.ALWAYS, null)
                )
                .branches(EffectBranch.builder()
                    .actions(EffectAction.builder(ActionType.BUFF_DAMAGE_UP, "STR")
                        .flatAmount(1, ActionUnit.STACK)
                        .targetSelector(TargetSelector.self())
                        .build())
                    .build())
                .build())
            .build();

        var updated2 = original.addPassive(newPassive);
        System.out.println("원본 패시브 개수: " + original.getPassives().size());  // 2
        System.out.println("새 객체 패시브 개수: " + updated2.getPassives().size());  // 3
    }
}
