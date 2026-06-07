package model.combat;

import model.Hero;

/**
 * Bee Queen enemy - boss-type enemy.
 * Special: Summons bee minions (regeneration effect) when defending
 * HP: 35
 * Attack: 12 damage
 * Defense: 8 protection + Regeneration (3 turns, 1 stack)
 * Experience: 12 points
 */
public class BeeQueen extends Enemy {
    private static final int HP = 35;
    private static final int ATTACK_DAMAGE = 12;
    private static final int DEFENSE_VALUE = 8;
    private static final int EXPERIENCE = 12;

    public BeeQueen() {
        super("Bee Queen", HP, EXPERIENCE);
    }

    @Override
    protected EnemyAction chooseAction() {
        // Queens prefer defending to summon bees (60% defend, 40% attack)
        return getRandom().nextDouble() < 0.6 ? EnemyAction.DEFEND : EnemyAction.ATTACK;
    }

    @Override
    public void executeAction(Hero hero) {
        if (getNextAction() == EnemyAction.ATTACK) {
            // Normal attack
            super.executeAction(hero);
        } else {
            // Defend and summon bees (represented by regeneration)
            addProtection(DEFENSE_VALUE);
            addStatusEffect(new RegenerationEffect(3, 1));
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
