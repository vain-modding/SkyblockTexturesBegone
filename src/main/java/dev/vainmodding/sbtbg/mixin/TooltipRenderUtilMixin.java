package dev.vainmodding.sbtbg.mixin;

import dev.vainmodding.sbtbg.Sbtbg;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.function.UnaryOperator;

@Mixin(TooltipRenderUtil.class)
public abstract class TooltipRenderUtilMixin {

    @ModifyVariable(method = "renderTooltipBackground", at = @At("HEAD"), argsOnly = true)
    private static Identifier sbtbg$vanillaTooltipWhenMissing(Identifier style) {
        if (style == null || !Sbtbg.fixMissingModels()) return style;

        Minecraft mc = Minecraft.getInstance();
        if (mc == null) return style;

        TextureAtlas atlas = mc.getAtlasManager().getAtlasOrThrow(AtlasIds.GUI);
        Identifier background = style.withPath((UnaryOperator<String>) s -> "tooltip/" + s + "_background");
        if (atlas.getSprite(background) == atlas.missingSprite()) {
            return null;
        }
        return style;
    }
}
