package model.equipment;

/**
 * A mana stone - provides mana.
 * Passive effect: Adds 1 mana at start of combat
 * Shape: 1x1
 */
public class ManaStone extends Equipment {
    private static final int MANA_PROVIDED = 1;

    public ManaStone() {
        super(
            "Mana Stone",
            new boolean[][] {
                {true}
            },
            0, // energy cost
            0, // mana cost
            0, // gold cost
            Rarity.COMMON,
            15, // buy price
            7   // sell price
        );
    }

    @Override
    public void use(CombatContext context) {
        // Mana stones provide mana passively at combat start
        // This method is not used during combat
    }

    /**
     * Gets the mana provided by this stone.
     * @return mana amount
     */
    public int getManaProvided() {
        return MANA_PROVIDED;
    }
}
