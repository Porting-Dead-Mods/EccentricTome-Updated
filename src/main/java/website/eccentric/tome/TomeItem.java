package website.eccentric.tome;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import website.eccentric.tome.network.ConvertPayload;

public class TomeItem extends Item {
    public TomeItem() {
        super(new Properties().stacksTo(1)
                .component(EccentricDataComponents.MOD_LIST, ModListComponent.EMPTY)
                .component(EccentricDataComponents.IS_TOME, false)
                .component(EccentricDataComponents.VERSION, 0));
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        var player = context.getPlayer();
        if (player == null)
            return InteractionResult.PASS;

        var hand = context.getHand();
        var position = context.getClickedPos();
        var tome = context.getItemInHand();
        var mod = ModName.from(context.getLevel().getBlockState(position));
        var modsBooks = TomeUtils.getModsBooks(tome).modList();

        if (!player.isShiftKeyDown() || !modsBooks.containsKey(mod))
            return InteractionResult.PASS;

        List<ItemStack> books = modsBooks.get(mod);
        if (!books.isEmpty() && context.getLevel().isClientSide) {
            var book = books.getLast();

            PacketDistributor.sendToServer(new ConvertPayload(book));
            return InteractionResult.FAIL;
        }

        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        var tome = player.getItemInHand(hand);

        if (level.isClientSide) {
            TomeClientUtils.openTome(tome);
        }

        return InteractionResultHolder.sidedSuccess(tome, level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack tome, @Nullable TooltipContext context, List<Component> tooltip, TooltipFlag advanced) {
        var modsBooks = TomeUtils.getModsBooks(tome).modList();

        for (var mod : modsBooks.keySet()) {
            tooltip.add(Component.literal(ModName.name(mod)));
            var books = modsBooks.get(mod);

            for (var book : books) {
                if (book.is(Items.AIR))
                    continue;
                var name = book.getHoverName().getString();
                tooltip.add(Component.literal("  " + ChatFormatting.GRAY + name));
            }
        }
    }
}
