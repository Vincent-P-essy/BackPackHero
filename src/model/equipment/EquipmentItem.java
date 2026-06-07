package model.equipment;

/**
 * Wrapper class that makes EquipmentData compatible with the existing Equipment interface.
 * This allows for a smooth transition from the class-based hierarchy to the data-based approach.
 */
public class EquipmentItem extends Equipment {
    private final EquipmentData data;

    /**
     * Creates a new equipment item from equipment data.
     * @param data the equipment data
     */
    public EquipmentItem(EquipmentData data) {
        super(
            data.name(),
            data.shape(),
            data.energyCost(),
            data.manaCost(),
            data.goldCost(),
            data.rarity(),
            data.buyPrice(),
            data.sellPrice()
        );
        this.data = data;
    }

    @Override
    public void use(CombatContext context) {
        data.use(context);
    }

    /**
     * Gets the underlying equipment data.
     * @return the equipment data
     */
    public EquipmentData getData() {
        return data;
    }
}
