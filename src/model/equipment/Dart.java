package model.equipment;

/**
 * A dart - basic ranged weapon.
 * Cost: 1 energy
 * Effect: Deals 4 damage to target enemy
 * Shape: 1x1
 */
public class Dart extends Equipment {
    private static final int DAMAGE = 4;

    public Dart() {
        super(
            "Dart",
            new boolean[][] {
                {true}
            },
            1, // energy cost
            0, // mana cost
            0, // gold cost
            Rarity.COMMON,
            5, // buy price
            2  // sell price
        );
    }

    @Override
    public void use(CombatContext context) {
        if (context.getTargetEnemy() != null) {
            context.getTargetEnemy().takeDamage(DAMAGE);
        }
    }
}
