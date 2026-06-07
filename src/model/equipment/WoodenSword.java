package model.equipment;

/**
 * A wooden sword - basic melee weapon.
 * Cost: 1 energy
 * Effect: Deals 6 damage to target enemy
 * Shape: 1x3
 */
public class WoodenSword extends Equipment {
    private static final int DAMAGE = 6;

    public WoodenSword() {
        super(
            "Wooden Sword",
            new boolean[][] {
                {true, true, true}
            },
            1, // energy cost
            0, // mana cost
            0, // gold cost
            Rarity.COMMON,
            10, // buy price
            5   // sell price
        );
    }

    @Override
    public void use(CombatContext context) {
        if (context.getTargetEnemy() != null) {
            context.getTargetEnemy().takeDamage(DAMAGE);
        }
    }
}
