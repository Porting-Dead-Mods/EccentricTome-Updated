package website.eccentric.tome;

import javax.annotation.Nullable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import website.eccentric.tome.core.TomeManager;

public class TomeUtils {
    
    @Deprecated
    public static boolean isTome(ItemStack stack) {
        return TomeManager.isTome(stack) || TomeManager.isActiveBook(stack);
    }

    @Nullable
    public static InteractionHand inHand(Player player) {
        InteractionHand hand = TomeManager.getTomeHand(player);
        if (hand != null) return hand;
        
        return TomeManager.getBookHand(player);
    }
}
