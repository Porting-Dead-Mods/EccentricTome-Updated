package website.eccentric.tome.network;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import website.eccentric.tome.EccentricTome;

@EventBusSubscriber(modid = EccentricTome.ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class TomeNetworking {
    public static final ResourceLocation name = ResourceLocation.fromNamespaceAndPath(EccentricTome.ID, "general");
    public static final String version = ResourceLocation.fromNamespaceAndPath(EccentricTome.ID, "1").toString();

    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(EccentricTome.ID);
        registrar.playBidirectional(RevertPayload.TYPE, RevertPayload.STREAM_CODEC, RevertPayload::revert);
        registrar.playBidirectional(ConvertPayload.TYPE, ConvertPayload.STREAM_CODEC, ConvertPayload::convert);
    }
}
