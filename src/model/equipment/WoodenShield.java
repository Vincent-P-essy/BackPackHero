package model.equipment;

/**
 * A wooden shield - basic protection.
 * Cost: 1 energy
 * Effect: Adds 7 protection for next turn
 * Shape: 2x2
 */
public class WoodenShield extends Equipment {
    private static final int PROTECTION = 7;

    public WoodenShield() {
        super(
            "Wooden Shield",
            new boolean[][] {
                {true, true},
                {true, true}
            },
            1, // energy cost
            0, // mana cost
            0, // gold cost
            Rarity.COMMON,
            15, // buy price
            7   // sell price
        );
    }

    @Override
    public void use(CombatContext context) {
        context.getHero().addProtection(PROTECTION);
    }
}
