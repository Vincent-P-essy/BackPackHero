package model.equipment;

/**
 * A can of tuna - consumable energy item.
 * Cost: free
 * Effect: Gives 1 energy, then destroys itself
 * Shape: 1x2
 */
public class TunaCan extends Equipment {
    private static final int ENERGY_PROVIDED = 1;

    public TunaCan() {
        super(
            "Tuna Can",
            new boolean[][] {
                {true, true}
            },
            0, // energy cost
            0, // mana cost
            0, // gold cost
            Rarity.COMMON,
            8, // buy price
            4  // sell price
        );
    }

    @Override
    public void use(CombatContext context) {
        context.getHero().addEnergy(ENERGY_PROVIDED);
        // Note: The item should be removed from backpack after use
        // This will be handled by the combat system
    }

    /**
     * Gets the energy provided.
     * @return energy amount
     */
    public int getEnergyProvided() {
        return ENERGY_PROVIDED;
    }
}
