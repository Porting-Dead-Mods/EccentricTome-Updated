package website.eccentric.tome;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import website.eccentric.tome.core.TomeManager;

public class AttachmentRecipe extends CustomRecipe {
    public AttachmentRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput crafting, Level level) {
        var foundTome = false;
        var foundTarget = false;

        for (var i = 0; i < crafting.size(); i++) {
            var stack = crafting.getItem(i);
            if (stack.isEmpty())
                continue;

            var item = stack.getItem();
            if (item instanceof BlockItem) {
                return false;
            }
            if (item instanceof TomeItem) {
                if (foundTome)
                    return false;
                foundTome = true;
            } else if (isTarget(stack)) {
                if (foundTarget)
                    return false;
                foundTarget = true;
            } else
                return false;
        }

        return foundTome && foundTarget;
    }

    @Override
    public ItemStack assemble(CraftingInput crafting, HolderLookup.Provider access) {
        var tome = ItemStack.EMPTY;
        var target = ItemStack.EMPTY;

        for (var i = 0; i < crafting.size(); i++) {
            var stack = crafting.getItem(i);
            if (stack.isEmpty())
                continue;

            if (stack.getItem() instanceof TomeItem)
                tome = stack;
            else
                target = stack;
        }

        return TomeManager.addBookToTome(tome.copy(), target);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    public boolean isTarget(ItemStack stack) {
        if (stack.isEmpty())
            return false;

        var location = BuiltInRegistries.ITEM.getKey(stack.getItem());
        var locationString = location.toString();
        var locationDamage = locationString + ":" + stack.getDamageValue();

        var items = EccentricConfig.getWhitelistedItems();
        return location.getNamespace().equals(ModName.PATCHOULI) || location.getNamespace().equals("modonomicon") ||  items.contains(locationString) || items.contains(locationDamage);
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return ItemStack.EMPTY;
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput crafting) {
        return NonNullList.withSize(crafting.size(), ItemStack.EMPTY);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return EccentricTome.ATTACHMENT.get();
    }
}