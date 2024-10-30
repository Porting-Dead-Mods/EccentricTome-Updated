package website.eccentric.tome.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import website.eccentric.tome.EccentricTome;
import website.eccentric.tome.TomeUtils;

public record RevertPayload() implements CustomPacketPayload {
    public static final RevertPayload INSTANCE = new RevertPayload();

    public static final Type<RevertPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(EccentricTome.ID, "revert_payload"));
    public static final StreamCodec<RegistryFriendlyByteBuf, RevertPayload> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void revert(RevertPayload payload, IPayloadContext context) {
        //EccentricTome.LOGGER.debug("Received revert message.");
        context.enqueueWork(() -> {
            Player player = context.player();
            PacketFlow direction = context.flow();
            InteractionHand hand = TomeUtils.inHand(player);

            if (hand != null) {
                var stack = player.getItemInHand(hand);
                var tome = TomeUtils.revert(stack);
                player.setItemInHand(hand, tome);

                if (direction.getReceptionSide().isClient()) {
                    Minecraft.getInstance().gameRenderer.itemInHandRenderer.itemUsed(hand);
                }
            }
        });
    }
}