package model.equipment;

/**
 * Interface for equipment behavior in combat.
 * Defines how an equipment is used.
 */
@FunctionalInterface
public interface EquipmentBehavior {
    /**
     * Uses the equipment.
     * @param context the combat context
     */
    void use(CombatContext context);
}
