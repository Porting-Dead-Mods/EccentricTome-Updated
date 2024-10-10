package website.eccentric.tome.network;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import website.eccentric.tome.EccentricTome;

@EventBusSubscriber(modid = EccentricTome.ID, bus = EventBusSubscriber.Bus.MOD)
public class TomeNetworking {
    @SubscribeEvent
    public static void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(EccentricTome.ID);
        registrar.playToServer(RevertPayload.TYPE, RevertPayload.STREAM_CODEC, RevertPayload::revert);
        registrar.playToServer(ConvertPayload.TYPE, ConvertPayload.STREAM_CODEC, ConvertPayload::convert);
    }
}
