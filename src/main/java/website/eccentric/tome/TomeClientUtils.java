package website.eccentric.tome;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import website.eccentric.tome.client.TomeScreen;

public class TomeClientUtils {
    public static void openTome(ItemStack stack) {
        if (stack.getItem() instanceof TomeItem tome) {
            Minecraft.getInstance().setScreen(new TomeScreen(tome.getDefaultInstance()));
        }
    }
}
