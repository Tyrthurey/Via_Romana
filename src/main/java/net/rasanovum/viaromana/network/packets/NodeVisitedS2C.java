package net.rasanovum.viaromana.network.packets;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.rasanovum.viaromana.client.data.ClientPathData;
import net.rasanovum.viaromana.network.AbstractPacket;
import net.rasanovum.viaromana.path.Node;
import net.rasanovum.viaromana.path.PathGraph;
import net.rasanovum.viaromana.util.VersionUtils;

/**
 * S2C Packet sent when a player visits a node to update the client-side visitation status.
 */
public record NodeVisitedS2C(BlockPos pos, long timestamp, ResourceKey<Level> dimension) implements AbstractPacket {

    public NodeVisitedS2C(FriendlyByteBuf buf) {
        this(
            buf.readBlockPos(),
            buf.readLong(),
            ResourceKey.create(Registries.DIMENSION, VersionUtils.getLocation(buf.readUtf()))
        );
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(this.pos);
        buf.writeLong(this.timestamp);
        buf.writeUtf(this.dimension.location().toString());
    }

    public void handle(Level level, Player player) {
        if (level.isClientSide) {
            PathGraph graph = ClientPathData.getInstance().getGraph(this.dimension);
            if (graph != null) {
                graph.getNodeAt(this.pos).ifPresent(node -> {
                    node.addVisitedPlayerCustom(player.getUUID(), this.timestamp);
                });
            }
        }
    }
}
