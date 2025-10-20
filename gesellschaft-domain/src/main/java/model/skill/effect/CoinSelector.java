package model.skill.effect;

import lombok.Getter;

/**
 * 코인 선택 조건 (Value Object)
 */
@Getter
public final class CoinSelector {
    private final CoinSelectorType type;
    private final Integer index;  // INDEX 타입일 때만 사용

    private CoinSelector(CoinSelectorType type, Integer index) {
        this.type = type;
        this.index = index;

        if (type == CoinSelectorType.INDEX && index == null) {
            throw new IllegalArgumentException("INDEX type requires index value");
        }
    }

    public static CoinSelector all() {
        return new CoinSelector(CoinSelectorType.ALL, null);
    }

    public static CoinSelector last() {
        return new CoinSelector(CoinSelectorType.LAST, null);
    }

    public static CoinSelector first() {
        return new CoinSelector(CoinSelectorType.FIRST, null);
    }

    public static CoinSelector index(int idx) {
        return new CoinSelector(CoinSelectorType.INDEX, idx);
    }

    public static CoinSelector current() {
        return new CoinSelector(CoinSelectorType.CURRENT, null);
    }
}
