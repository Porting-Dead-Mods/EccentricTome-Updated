package website.eccentric.tome;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;
import net.neoforged.neoforge.event.entity.item.ItemTossEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import website.eccentric.tome.network.RevertPayload;

import java.util.function.Supplier;

@Mod(EccentricTome.ID)
public class EccentricTome {
    public static final String ID = "eccentrictome";
    public static final Logger LOGGER = LogManager.getLogger(ID);

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(EccentricTome.ID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPES = DeferredRegister
            .create(BuiltInRegistries.RECIPE_SERIALIZER, ID);

    public static final Supplier<RecipeSerializer<?>> ATTACHMENT = RECIPES.register("attachment",
            () -> new SimpleCraftingRecipeSerializer<>(AttachmentRecipe::new));
    public static final DeferredItem<Item> TOME = ITEMS.register("tome", TomeItem::new);

    public EccentricTome(IEventBus modEvent, ModContainer container) {
        ITEMS.register(modEvent);
        RECIPES.register(modEvent);
        EccentricDataComponents.COMPONENTS.register(modEvent);

        modEvent.addListener(this::onClientSetup);
        modEvent.addListener(this::onModConfig);
        modEvent.addListener(this::onBuildCreativeModeTabContents);
        modEvent.addListener(this::modifyDefaultComponents);
        container.registerConfig(ModConfig.Type.COMMON, EccentricConfig.SPEC);

        var minecraftEvent = NeoForge.EVENT_BUS;
        minecraftEvent.addListener(EventPriority.LOW, this::onItemDropped);
        minecraftEvent.addListener(this::onRightClickTomeItem);
        minecraftEvent.addListener(this::onRightClickTomeOnBlock);
    }

    private void onClientSetup(final FMLClientSetupEvent event) {
        var minecraftEvent = NeoForge.EVENT_BUS;
        minecraftEvent.addListener(this::onLeftClickEmpty);
    }

    private void onModConfig(ModConfigEvent event) {
        EccentricConfig.ALIAS_MAP.clear();
        for (var alias : EccentricConfig.getAliases()) {
            var tokens = alias.split("=");
            EccentricConfig.ALIAS_MAP.put(tokens[0], tokens[1]);
        }
    }

    private void onLeftClickEmpty(PlayerInteractEvent.LeftClickEmpty event) {
        var stack = event.getItemStack();
        if (TomeUtils.isTome(stack) && !(stack.getItem() instanceof TomeItem)) {
            PacketDistributor.sendToServer(new RevertPayload());
        }
    }

    private void onRightClickTomeItem(PlayerInteractEvent.RightClickItem event) {
        event.setCanceled(onRightClickTome(event));
    }

    private void onRightClickTomeOnBlock(PlayerInteractEvent.RightClickBlock event) {
        event.setCanceled(onRightClickTome(event));
    }

    private static boolean onRightClickTome(PlayerInteractEvent event) {
        var stack = event.getItemStack();
        if (!(stack.getItem() instanceof TomeItem) && stack.getOrDefault(EccentricDataComponents.IS_TOME, false)) {
            ItemStack prevStack = stack.copy();
            Player player = event.getEntity();
            InteractionHand hand = event.getHand();
            stack.use(event.getLevel(), player, hand);
            if (player.getMainHandItem().isEmpty()) {
                ModListComponent component = TomeUtils.remove(prevStack, stack);
                ItemStack newStack = EccentricTome.TOME.toStack();
                newStack.set(EccentricDataComponents.MOD_LIST, component);
                player.setItemInHand(hand, newStack);
            }
            return true;
        }
        return false;
    }

    private void onItemDropped(ItemTossEvent event) {
        if (!event.getPlayer().isShiftKeyDown())
            return;

        var entity = event.getEntity();
        var stack = entity.getItem();

        if (TomeUtils.isTome(stack) && !(stack.getItem() instanceof TomeItem)) {
            var detatchment = TomeUtils.revert(stack);
            var level = entity.getCommandSenderWorld();

            if (!level.isClientSide) {
                level.addFreshEntity(new ItemEntity(level, entity.getX(), entity.getY(), entity.getZ(), detatchment));
            }

            entity.setItem(stack);
        }
    }

    private void onBuildCreativeModeTabContents(final BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(CreativeModeTabs.TOOLS_AND_UTILITIES)) {
            event.accept(TOME);
        }
    }

    private void modifyDefaultComponents(ModifyDefaultComponentsEvent event) {
        event.modifyMatching(item -> {
            ResourceLocation itemKey = BuiltInRegistries.ITEM.getKey(item);
            String namespace = itemKey.getNamespace();
            return namespace.equals(ModName.PATCHOULI) || namespace.equals("modonomicon") || EccentricConfig.getWhitelistedItems().contains(item.toString());
        }, builder -> builder
                .set(EccentricDataComponents.MOD_LIST.get(), ModListComponent.EMPTY)
                .set(EccentricDataComponents.IS_TOME.get(), false)
                .set(EccentricDataComponents.VERSION.get(), 0));
    }
}
