package dev.vainmodding.sbtbg.mixin;

import dev.vainmodding.sbtbg.Sbtbg;
import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.common.ClientboundResourcePackPushPacket;
import net.minecraft.network.protocol.common.ServerboundResourcePackPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(ClientCommonPacketListenerImpl.class)
public abstract class ClientCommonPacketListenerImplMixin {

    @Shadow
    @Final
    protected Connection connection;

    @Inject(method = "handleResourcePackPush", at = @At("HEAD"), cancellable = true)
    private void sbtbg$blockServerPack(ClientboundResourcePackPushPacket packet, CallbackInfo ci) {
        if (!Sbtbg.shouldBlock()) return;

        UUID id = packet.id();
        this.connection.send(new ServerboundResourcePackPacket(id, ServerboundResourcePackPacket.Action.ACCEPTED));
        this.connection.send(new ServerboundResourcePackPacket(id, ServerboundResourcePackPacket.Action.DOWNLOADED));
        this.connection.send(new ServerboundResourcePackPacket(id, ServerboundResourcePackPacket.Action.SUCCESSFULLY_LOADED));
        ci.cancel();

        Sbtbg.onBlocked(packet);
    }
}
