package dev.vainmodding.sbtbg.mixin;

import dev.vainmodding.sbtbg.Sbtbg;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;

@Mixin(ItemModelResolver.class)
public abstract class ItemModelResolverMixin {

    @Redirect(
            method = "appendItemLayers",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;get(Lnet/minecraft/core/component/DataComponentType;)Ljava/lang/Object;"
            )
    )
    private Object sbtbg$fallbackMissingModel(ItemStack stack, DataComponentType<?> type) {
        Object value = stack.get(type);

        if (!Sbtbg.fixMissingModels()) return value;
        if (type != DataComponents.ITEM_MODEL || !(value instanceof Identifier requested)) return value;

        Minecraft mc = Minecraft.getInstance();
        if (mc == null) return value;
        ModelManager modelManager = mc.getModelManager();
        if (modelManager == null) return value;

        Map<Identifier, ?> models = ((ModelManagerAccessor) modelManager).sbtbg$getBakedItemStackModels();
        if (models == null || models.containsKey(requested)) return value;

        if (stack.has(DataComponents.PROFILE)) {
            Identifier headModel = Items.PLAYER_HEAD.components().get(DataComponents.ITEM_MODEL);
            if (headModel != null && models.containsKey(headModel)) {
                return headModel;
            }
        }

        Identifier fallback = stack.getItem().components().get(DataComponents.ITEM_MODEL);
        if (fallback != null && !fallback.equals(requested) && models.containsKey(fallback)) {
            return fallback;
        }
        return value;
    }
}
