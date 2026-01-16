package org.mcraig150.EscapeWand;

import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.server.core.modules.entity.EntityModule;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.math.vector.Vector3d;
import java.util.Objects;

public class TeleportUtils {
    public static void escapeToSurface(Store<EntityStore> store,
                                       com.hypixel.hytale.server.core.entity.entities.Player player, World world) {
        Transform transform = Objects.requireNonNull(store.getComponent(Objects.requireNonNull(player.getReference()),
                EntityModule.get().getTransformComponentType())).getTransform();

        double x = transform.getPosition().x;
        double z = transform.getPosition().z;
        int blockX = (int) Math.floor(x);
        int blockZ = (int) Math.floor(z);

        int surfaceY = 0;
        for (int y = 256; y >= 0; y--) {
            int blockId = world.getBlock(blockX, y, blockZ);
            if (blockId != 0) {
                surfaceY = y + 1;
                break;
            }
        }

        Transform newTransform = (Transform) transform.clone();
        newTransform.setPosition(new Vector3d(x, surfaceY, z));

        world.execute(() -> {
            store.addComponent(Objects.requireNonNull(player.getReference()), Teleport.getComponentType(),
                    new Teleport(newTransform));
        });
    }
}
