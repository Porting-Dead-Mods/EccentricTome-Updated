package website.eccentric.tome;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EccentricDataComponents {
    public static final DeferredRegister.DataComponents COMPONENTS = DeferredRegister.createDataComponents(EccentricTome.ID);

    public static final Supplier<DataComponentType<ModListComponent>> MOD_LIST =
            registerDataComponentType("mod_list", () -> builder -> builder
                    .persistent(ModListComponent.CODEC)
                    .networkSynchronized(ModListComponent.STREAM_CODEC)
            );
    public static final Supplier<DataComponentType<Integer>> VERSION = registerDataComponentType("version", () -> builder -> builder
            .persistent(Codec.INT)
            .networkSynchronized(ByteBufCodecs.INT)
    );
    public static final Supplier<DataComponentType<Boolean>> IS_TOME = registerDataComponentType("is_tome", () -> builder -> builder
            .persistent(Codec.BOOL)
            .networkSynchronized(ByteBufCodecs.BOOL)
    );

    public static <T> Supplier<DataComponentType<T>> registerDataComponentType(
            String name, Supplier<UnaryOperator<DataComponentType.Builder<T>>> builderOperator) {
        return COMPONENTS.register(name, () -> builderOperator.get().apply(DataComponentType.builder()).build());
    }

}
