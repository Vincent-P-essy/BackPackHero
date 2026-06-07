package model.equipment;

import model.combat.WeaknessEffect;

/**
 * Rust curse - reduces damage dealt.
 * Shape: 2x2 L-shape
 */
public class RustCurse extends Curse {

    public RustCurse() {
        super(
            "Rust Curse",
            new boolean[][] {
                {true, true},
                {true, false}
            },
            new WeaknessEffect(3, 2) // Penalty: Weakness for 3 turns
        );
    }

    @Override
    public String getNegativeEffect() {
        return "Reduces all damage dealt by 3";
    }
}
