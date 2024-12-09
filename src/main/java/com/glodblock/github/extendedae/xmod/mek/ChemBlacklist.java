package com.glodblock.github.extendedae.xmod.mek;

import appeng.api.stacks.AEKey;
import me.ramidzkh.mekae2.ae2.MekanismKey;
import mekanism.api.chemical.attribute.ChemicalAttributeValidator;

public class ChemBlacklist {

    public static boolean isValid(AEKey what) {
        if (what instanceof MekanismKey chem) {
            return !ChemicalAttributeValidator.DEFAULT.process(chem.getStack());
        }
        return false;
    }

}
