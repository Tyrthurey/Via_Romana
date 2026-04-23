package net.rasanovum.viaromana.network.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.rasanovum.viaromana.client.ClientConfigCache;
import net.rasanovum.viaromana.ViaRomana;
import net.rasanovum.viaromana.network.AbstractPacket;

/**
 * Server-to-client packet to synchronize server config values to the client.
 */
public record ConfigSyncS2C(float pathQualityThreshold, int nodeDistanceMin, int nodeDistanceMax, int nodeUtilityDistance, int infrastructureCheckRadius, boolean requireWalkedPath, int visitedNodeDecayTime, int pathingLimitRadius) implements AbstractPacket {

    public ConfigSyncS2C(FriendlyByteBuf buf) {
        this(
            buf.readFloat(),
            buf.readInt(),
            buf.readInt(),
            buf.readInt(),
            buf.readInt(),
            buf.readBoolean(),
            buf.readInt(),
            buf.readInt()
        );
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeFloat(this.pathQualityThreshold);
        buf.writeInt(this.nodeDistanceMin);
        buf.writeInt(this.nodeDistanceMax);
        buf.writeInt(this.nodeUtilityDistance);
        buf.writeInt(this.infrastructureCheckRadius);
        buf.writeBoolean(this.requireWalkedPath);
        buf.writeInt(this.visitedNodeDecayTime);
        buf.writeInt(this.pathingLimitRadius);
    }

    public void handle(Level level, Player player) {
        if (level.isClientSide) {
            ClientConfigCache.updateFromServer(
                this.pathQualityThreshold, this.nodeDistanceMin, this.nodeDistanceMax, this.nodeUtilityDistance, this.infrastructureCheckRadius, this.requireWalkedPath, this.visitedNodeDecayTime, this.pathingLimitRadius
            );
            
            ViaRomana.LOGGER.debug("Received config sync from server: pathQuality={}, nodeMin={}, nodeMax={}, utility={}, infraRadius={}, requireWalked={}, decayTime={}, limitRadius={}",
                this.pathQualityThreshold, this.nodeDistanceMin, this.nodeDistanceMax, this.nodeUtilityDistance, this.infrastructureCheckRadius, this.requireWalkedPath, this.visitedNodeDecayTime, this.pathingLimitRadius);
        }
    }
}
