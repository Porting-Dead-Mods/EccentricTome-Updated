package website.eccentric.tome;

import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public record ModListComponent(Map<String, List<ItemStack>> modList) {
    public static final ModListComponent EMPTY = new ModListComponent(new HashMap<>());

    public static final Codec<ModListComponent> CODEC = Codec.unboundedMap(Codec.STRING, ItemStack.CODEC.listOf())
            .xmap(ModListComponent::new, ModListComponent::modList);

    public static final StreamCodec<RegistryFriendlyByteBuf, ModListComponent> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(
                    HashMap::new,
                    ByteBufCodecs.STRING_UTF8,
                    ItemStack.STREAM_CODEC.apply(ByteBufCodecs.list()),
                    256
            ),
            ModListComponent::modList,
            ModListComponent::new
    );

    public ModListComponent copy() {
        Map<String, List<ItemStack>> map = new HashMap<>();
        for (Map.Entry<String, List<ItemStack>> entry : this.modList.entrySet()) {
            map.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        return new ModListComponent(map);
    }
}
