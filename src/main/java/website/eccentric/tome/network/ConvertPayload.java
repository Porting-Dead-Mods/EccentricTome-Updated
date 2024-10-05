package website.eccentric.tome.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import website.eccentric.tome.EccentricTome;
import website.eccentric.tome.TomeUtils;

public record ConvertPayload(ItemStack bookStack) implements CustomPacketPayload {
    public static final Type<ConvertPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(EccentricTome.ID, "convert_payload"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ConvertPayload> STREAM_CODEC = StreamCodec.composite(
            ItemStack.STREAM_CODEC,
            ConvertPayload::bookStack,
            ConvertPayload::new
    );

    public static void convert(ConvertPayload payload, IPayloadContext context) {
        EccentricTome.LOGGER.debug("Received convert payload.");
        context.enqueueWork(() -> {
            Player player = context.player();
            InteractionHand hand = TomeUtils.inHand(player);

            if (hand != null) {
                ItemStack tome = player.getItemInHand(hand);
                player.setItemInHand(hand, TomeUtils.convert(tome, payload.bookStack));
            }
        });
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}