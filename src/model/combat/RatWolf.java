package model.combat;

/**
 * A rat-wolf enemy.
 * HP: 20
 * Attack: 12 damage
 * Defense: 7 protection
 * Experience: 6 points
 */
public class RatWolf extends Enemy {
    private static final int HP = 20;
    private static final int ATTACK_DAMAGE = 12;
    private static final int DEFENSE_VALUE = 7;
    private static final int EXPERIENCE = 6;

    public RatWolf() {
        super("Rat-Wolf", HP, EXPERIENCE);
    }

    @Override
    protected EnemyAction chooseAction() {
        // Randomly choose between attack and defend
        return getRandom().nextBoolean() ? EnemyAction.ATTACK : EnemyAction.DEFEND;
    }

    @Override
    protected int getAttackDamage() {
        return ATTACK_DAMAGE;
    }

    @Override
    protected int getDefenseValue() {
        return DEFENSE_VALUE;
    }
}
