package model.combat;

/**
 * A small rat-wolf enemy.
 * HP: 15
 * Attack: 8 damage
 * Defense: 5 protection
 * Experience: 4 points
 */
public class SmallRatWolf extends Enemy {
    private static final int HP = 15;
    private static final int ATTACK_DAMAGE = 8;
    private static final int DEFENSE_VALUE = 5;
    private static final int EXPERIENCE = 4;

    public SmallRatWolf() {
        super("Small Rat-Wolf", HP, EXPERIENCE);
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
