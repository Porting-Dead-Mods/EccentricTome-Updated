package website.eccentric.tome.client;

import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import org.lwjgl.opengl.GL11;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.phys.BlockHitResult;
import website.eccentric.tome.EccentricDataComponents;
import website.eccentric.tome.EccentricTome;
import website.eccentric.tome.ModName;
import website.eccentric.tome.TomeItem;
import website.eccentric.tome.core.TomeData;
import website.eccentric.tome.core.TomeManager;

@EventBusSubscriber(modid = EccentricTome.ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class RenderGuiOverlayHandler {
	public static final LayeredDraw.Layer LAYER = ((guiGraphics, deltaTracker) -> {

		var minecraft = Minecraft.getInstance();

		var player = minecraft.player;
		if (player == null)
			return;

		var level = minecraft.level;
		if (level == null)
			return;

		var hit = minecraft.hitResult;
		if (!(hit instanceof BlockHitResult))
			return;

		var blockHit = (BlockHitResult) hit;

		var hand = TomeManager.getTomeHand(player);
		if (hand == null)
			return;

		var state = level.getBlockState(blockHit.getBlockPos());
		if (state.isAir())
			return;

		var tome = player.getItemInHand(hand);
		if (!TomeManager.isTome(tome))
			return;

		var mod = ModName.from(state);
		var data = tome.getOrDefault(EccentricDataComponents.TOME_DATA.get(), TomeData.EMPTY);
		
		if (!data.books().containsKey(mod))
			return;

		var books = data.books().get(mod);
		if (books.isEmpty())
			return;

		var book = books.get(books.size() - 1);
		var hoverName = book.getHoverName();
		var convert = I18n.get("eccentrictome.convert");

		RenderSystem.enableBlend();
		RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		var x = guiGraphics.guiWidth() / 2 - 17;
		var y = guiGraphics.guiHeight() / 2 + 2;

		guiGraphics.renderItem(book, x, y);
		guiGraphics.drawString(minecraft.font, hoverName, x + 20, y + 4, 0xFFFFFFFF);
		guiGraphics.drawString(minecraft.font, ChatFormatting.GRAY + convert, x + 25, y + 14, 0xFFFFFFFF);
	});

	@SubscribeEvent
	public static void registerLayers(RegisterGuiLayersEvent event) {
		event.registerAboveAll(ResourceLocation.fromNamespaceAndPath(EccentricTome.ID, "tome_overlay"), LAYER);
	}
}
