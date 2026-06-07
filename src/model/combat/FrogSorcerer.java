package model.combat;

import model.Hero;

/**
 * Frog Sorcerer enemy.
 * Special: Can cast poison spells (applies poison effect to hero)
 * HP: 25
 * Attack: 10 damage
 * Defense: 6 protection
 * Experience: 8 points
 * Special: 30% chance to cast poison (2 turns, 1 stack) instead of normal action
 */
public class FrogSorcerer extends Enemy {
    private static final int HP = 25;
    private static final int ATTACK_DAMAGE = 10;
    private static final int DEFENSE_VALUE = 6;
    private static final int EXPERIENCE = 8;
    private static final double POISON_CHANCE = 0.3;

    private boolean willCastPoison;

    public FrogSorcerer() {
        super("Frog Sorcerer", HP, EXPERIENCE);
        this.willCastPoison = false;
    }

    @Override
    protected EnemyAction chooseAction() {
        // 30% chance to prepare poison spell
        if (getRandom().nextDouble() < POISON_CHANCE) {
            willCastPoison = true;
            return EnemyAction.ATTACK; // Use ATTACK as placeholder for special
        }

        willCastPoison = false;

        // Otherwise 50/50 attack or defend
        return getRandom().nextBoolean() ? EnemyAction.ATTACK : EnemyAction.DEFEND;
    }

    @Override
    public void executeAction(Hero hero) {
        if (willCastPoison) {
            // Cast poison spell
            hero.addStatusEffect(new PoisonEffect(2, 1));
            willCastPoison = false;
        } else {
            // Normal action
            super.executeAction(hero);
        }
    }

    @Override
    public EnemyAction getNextAction() {
        if (willCastPoison) {
            return EnemyAction.ATTACK; // Display as attack but actually poison
        }
        return super.getNextAction();
    }

    @Override
    protected int getAttackDamage() {
        return ATTACK_DAMAGE;
    }

    @Override
    protected int getDefenseValue() {
        return DEFENSE_VALUE;
    }

    /**
     * Checks if this enemy will cast poison next turn.
     * @return true if casting poison
     */
    public boolean isWillCastPoison() {
        return willCastPoison;
    }
}
