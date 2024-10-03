package website.eccentric.tome.client;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import website.eccentric.tome.EccentricTome;
import website.eccentric.tome.TomeItem;

@EventBusSubscriber(modid = EccentricTome.ID, bus = EventBusSubscriber.Bus.GAME,value = Dist.CLIENT)
public class TomeHandler {
    @SubscribeEvent
    public static void onOpenTome(PlayerInteractEvent.RightClickItem event) {
        if (event.getItemStack().getItem() instanceof TomeItem tome) {
            Minecraft.getInstance().setScreen(new TomeScreen(tome.getDefaultInstance()));
        }
    }
}
