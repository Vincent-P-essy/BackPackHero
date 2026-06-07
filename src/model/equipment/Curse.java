package model.equipment;

import model.combat.StatusEffect;

/**
 * Abstract base class for curses.
 * Curses are special equipment that cannot be rotated and have negative effects.
 */
public abstract class Curse extends Equipment {
    private final StatusEffect penaltyOnRemoval;

    /**
     * Creates a curse.
     * @param name the curse name
     * @param shape the curse shape
     * @param penalty penalty applied when removed (can be null)
     */
    protected Curse(String name, boolean[][] shape, StatusEffect penalty) {
        super(name, shape, 0, 0, 0, Rarity.COMMON, 0, 0);
        this.penaltyOnRemoval = penalty;
    }

    @Override
    public boolean canRotate() {
        return false; // Curses cannot be rotated
    }

    @Override
    public void use(CombatContext context) {
        // Curses are not used in combat
    }

    /**
     * Gets the penalty applied when this curse is removed.
     * @return the penalty effect, or null if no penalty
     */
    public StatusEffect getPenaltyOnRemoval() {
        return penaltyOnRemoval;
    }

    /**
     * Gets the passive negative effect of this curse.
     * @return description of the negative effect
     */
    public abstract String getNegativeEffect();
}
