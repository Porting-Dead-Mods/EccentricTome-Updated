package website.eccentric.tome;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.fml.ModList;

public class TomeUtils {
    public static ItemStack convert(ItemStack tome, ItemStack book) {
        var modsBooks = getModsBooks(tome);
        var mod = ModName.from(book);
        var books = modsBooks.copy().modList().get(mod);
        var registry = BuiltInRegistries.ITEM.getKey(book.getItem());
        books.removeIf(b -> BuiltInRegistries.ITEM.getKey(b.getItem()).equals(registry));

        EccentricTome.LOGGER.debug("MODSBOOKS: {}", modsBooks);
        setModsBooks(book, modsBooks);
        //Migration.setVersion(book);

        book.set(EccentricDataComponents.IS_TOME, true);
        setHoverName(book);

        return book;
    }

    public static ItemStack revert(ItemStack book) {
        //Migration.apply(book);

        var tome = new ItemStack(EccentricTome.TOME.get());
        copyMods(book, tome);
        //Migration.setVersion(tome);
        clear(book);

        return tome;
    }

    public static ItemStack attach(ItemStack tome, ItemStack book) {
        EccentricTome.LOGGER.debug("Attaching");
        var mod = ModName.from(book);
        var modsBooks = getModsBooks(tome);

        Map<String, List<ItemStack>> modListCopy = modsBooks.copy().modList();

        var books = modListCopy.getOrDefault(mod, new ArrayList<>());

        ItemStack itemStack = book.copyWithCount(1);
        if (books.contains(itemStack)) return ItemStack.EMPTY;

        for (ItemStack book1 : books) {
            if (ItemStack.isSameItemSameComponents(book1, book)) {
                return ItemStack.EMPTY;
            }
        }

        books.add(itemStack);

        modListCopy.put(mod, books);

        setModsBooks(tome, new ModListComponent(modListCopy));
        return tome;
    }

    public static ModListComponent remove(ItemStack tome, ItemStack bookToRemove) {
        ModListComponent modListComponent = getModsBooks(tome).copy();
        Map<String, List<ItemStack>> modList = modListComponent.modList();
        String modId = bookToRemove.getItem().getCreatorModId(bookToRemove);
        List<ItemStack> itemStacks = modList.get(modId);
        itemStacks.remove(bookToRemove);
        if (itemStacks.isEmpty()) {
            modList.remove(modId);
        }
        return modListComponent;
    }

    public static ModListComponent getModsBooks(ItemStack stack) {
        return stack.get(EccentricDataComponents.MOD_LIST);
    }

    public static void setModsBooks(ItemStack stack, ModListComponent modsBooks) {
        stack.set(EccentricDataComponents.MOD_LIST, modsBooks);
    }

    public static boolean isTome(ItemStack stack) {
        if (stack.isEmpty())
            return false;
        else if (stack.getItem() instanceof TomeItem)
            return true;
        else {
            return Boolean.TRUE.equals(stack.get(EccentricDataComponents.IS_TOME));
        }
    }

    @Nullable
    public static InteractionHand inHand(Player player) {
        var hand = InteractionHand.MAIN_HAND;
        var stack = player.getItemInHand(hand);
        if (isTome(stack))
            return hand;

        hand = InteractionHand.OFF_HAND;
        stack = player.getItemInHand(hand);
        if (isTome(stack))
            return hand;

        return null;
    }

    private static void copyMods(ItemStack source, ItemStack target) {
        target.set(EccentricDataComponents.MOD_LIST, source.get(EccentricDataComponents.MOD_LIST).copy());
    }

    private static void clear(ItemStack stack) {
        stack.remove(EccentricDataComponents.MOD_LIST);
        stack.remove(EccentricDataComponents.IS_TOME);
        stack.remove(EccentricDataComponents.VERSION);

        stack.set(DataComponents.CUSTOM_NAME, stack.get(DataComponents.ITEM_NAME));
    }

    private static void setHoverName(ItemStack book) {
        var name = book.getHoverName().copy().withStyle(ChatFormatting.GREEN);
        book.set(DataComponents.CUSTOM_NAME, Component.translatable("eccentrictome.name", name));
    }
}
