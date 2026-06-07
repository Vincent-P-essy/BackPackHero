package model.equipment;

/**
 * Leather armor - basic armor.
 * Cost: 1 energy
 * Effect: Adds 5 protection
 * Shape: 2x2
 */
public class LeatherArmor extends Equipment {
    private static final int PROTECTION = 5;

    public LeatherArmor() {
        super(
            "Leather Armor",
            new boolean[][] {
                {true, true},
                {true, true}
            },
            1, // energy cost
            0, // mana cost
            0, // gold cost
            Rarity.COMMON,
            20, // buy price
            10  // sell price
        );
    }

    @Override
    public void use(CombatContext context) {
        context.getHero().addProtection(PROTECTION);
    }
}
