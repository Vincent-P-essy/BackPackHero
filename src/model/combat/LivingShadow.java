package model.combat;

import model.Hero;

/**
 * Living Shadow enemy.
 * Special: Applies Weakness effect when attacking
 * HP: 18
 * Attack: 8 damage + Weakness (2 turns, 1 stack)
 * Defense: 4 protection
 * Experience: 7 points
 */
public class LivingShadow extends Enemy {
    private static final int HP = 18;
    private static final int ATTACK_DAMAGE = 8;
    private static final int DEFENSE_VALUE = 4;
    private static final int EXPERIENCE = 7;

    public LivingShadow() {
        super("Living Shadow", HP, EXPERIENCE);
    }

    @Override
    protected EnemyAction chooseAction() {
        // Shadows prefer attacking (70% attack, 30% defend)
        return getRandom().nextDouble() < 0.7 ? EnemyAction.ATTACK : EnemyAction.DEFEND;
    }

    @Override
    public void executeAction(Hero hero) {
        if (getNextAction() == EnemyAction.ATTACK) {
            // Attack and apply weakness
            hero.takeDamage(ATTACK_DAMAGE);
            hero.addStatusEffect(new WeaknessEffect(2, 1));
            hero.resetProtection();
        } else {
            // Normal defend
            super.executeAction(hero);
        }
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
