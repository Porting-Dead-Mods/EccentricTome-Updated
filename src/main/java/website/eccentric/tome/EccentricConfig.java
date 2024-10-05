package website.eccentric.tome;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;

public class EccentricConfig {
    public static final ModConfigSpec.BooleanValue DISABLE_OVERLAY;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> ITEMS;
    public static final ModConfigSpec.ConfigValue<List<? extends String>> ALIASES;

    public static final HashMap<String, String> ALIAS_MAP = new HashMap<>();

    public static final ModConfigSpec SPEC;

    static {
        var BUILDER = new ModConfigSpec.Builder()
                .comment("Common configuration settings")
                .push("common");

        DISABLE_OVERLAY = BUILDER
                .comment("Disable overlay previewing tome conversion")
                .define("disable_overlay", false);

        ITEMS = BUILDER
                .comment("Whitelisted items").defineListAllowEmpty(
                        List.of("items"),
                        () -> List.of(
                                "paganbless:pagan_guide",
                                "nautec:nautec_guide",
                                "immersiveengineering:manual",
                                "tconstruct:materials_and_you",
                                "tconstruct:puny_smelting",
                                "tconstruct:mighty_smelting",
                                "tconstruct:fantastic_foundry",
                                "tconstruct:tinkers_gadgetry",
                                "integrateddynamics:on_the_dynamics_of_integration",
                                "evilcraft:origins_of_darkness",
                                "cookingforblockheads:no_filter_edition",
                                "alexsmobs:animal_dictionary",
                                "occultism:dictionary_of_spirits",
                                "theoneprobe:probenote",
                                "compactmachines:personal_shrinking_device",
                                "draconicevolution:info_tablet",
                                "iceandfire:bestiary",
                                "rootsclassic:runic_tablet",
                                "enigmaticlegacy:the_acknowledgment",
                                "ad_astra:astrodux"),
                        () -> "",
                        Validator::isStringResource
                );

        ALIASES = BUILDER
                .comment("Mod aliases")
                .defineListAllowEmpty(
                        List.of("aliases"),
                        () -> List.of(
                                "mythicbotany=botania",
                                "integratedtunnels=integrateddynamics",
                                "integratedterminals=integrateddynamics",
                                "integratedcrafting=integrateddynamics",
                                "rftoolsbuilder=rftoolsbase",
                                "rftoolscontrol=rftoolsbase",
                                "rftoolsdim=rftoolsbase",
                                "rftoolspower=rftoolsbase",
                                "rftoolsstorage=rftoolsbase",
                                "rftoolsutility=rftoolsbase",
                                "rftoolspower=rftoolsbase",
                                "deepresonance=rftoolsbase",
                                "xnet=rftoolsbase",
                                "mysticalaggraditions=mysticalagriculture"),
                        () -> "",
                        Validator::isStringAlias);

        BUILDER.pop();

        SPEC = BUILDER.build();
    }

    public static class Validator {
        public static boolean isString(Object object) {
            return object instanceof String;
        }

        public static boolean isStringResource(Object object) {
            String pattern = "^.+:.+$";
            return isString(object) && ((String) object).matches(pattern);
        }

        public static boolean isStringAlias(Object object) {
            String pattern = "^.+=.+$";
            return isString(object) && ((String) object).matches(pattern);
        }
    }
}
