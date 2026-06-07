package model.equipment;

/**
 * A magic wand - basic magic weapon.
 * Cost: 1 mana
 * Effect: Deals 8 damage to target enemy
 * Shape: 1x2
 */
public class MagicWand extends Equipment {
    private static final int DAMAGE = 8;

    public MagicWand() {
        super(
            "Magic Wand",
            new boolean[][] {
                {true, true}
            },
            0, // energy cost
            1, // mana cost
            0, // gold cost
            Rarity.UNCOMMON,
            25, // buy price
            12  // sell price
        );
    }

    @Override
    public void use(CombatContext context) {
        if (context.getTargetEnemy() != null && context.getHero().useMana(getManaCost())) {
            context.getTargetEnemy().takeDamage(DAMAGE);
        }
    }
}
