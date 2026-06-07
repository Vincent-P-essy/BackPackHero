package model.equipment;

/**
 * Heavy curse - takes up space and provides no benefit.
 * Shape: 3x2 rectangle
 */
public class HeavyCurse extends Curse {

    public HeavyCurse() {
        super(
            "Heavy Curse",
            new boolean[][] {
                {true, true, true},
                {true, true, true}
            },
            null // No penalty on removal, just wastes space
        );
    }

    @Override
    public String getNegativeEffect() {
        return "Takes up valuable backpack space";
    }
}
