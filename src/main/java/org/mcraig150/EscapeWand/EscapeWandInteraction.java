package org.mcraig150.EscapeWand;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.client.SimpleBlockInteraction;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class EscapeWandInteraction extends SimpleBlockInteraction {

        public static final BuilderCodec<EscapeWandInteraction> CODEC = BuilderCodec
                        .builder(EscapeWandInteraction.class, EscapeWandInteraction::new).build();

        private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        @Override
        protected void interactWithBlock(@NonNullDecl World world,
                        @NonNullDecl CommandBuffer<EntityStore> commandBuffer,
                        @NonNullDecl InteractionType interactionType,
                        @NonNullDecl InteractionContext interactionContext, @NullableDecl ItemStack itemStack,
                        @NonNullDecl Vector3i vector3i, @NonNullDecl CooldownHandler cooldownHandler) {

                var ref = interactionContext.getEntity();
                var store = ref.getStore();
                var player = store.getComponent(ref, Player.getComponentType());

                if (player != null) {
                        com.hypixel.hytale.logger.HytaleLogger.forEnclosingClass().atInfo()
                                        .log(player.getDisplayName() + " activated Escape Wand!");

                        // Start the countdown
                        player.sendMessage(Message.raw("Your Escape Wand activated! Teleporting in..."));

                        // Schedule countdown messages
                        for (int i = 5; i >= 1; i--) {
                                final int countdown = i;
                                scheduler.schedule(() -> {
                                        player.sendMessage(Message.raw("Escaping in " + countdown + "..."));
                                }, 5 - i, TimeUnit.SECONDS);
                        }

                        // Schedule the teleport after 5 seconds
                        scheduler.schedule(() -> {
                                try {
                                        com.hypixel.hytale.logger.HytaleLogger.forEnclosingClass().atInfo()
                                                        .log("Attempting teleport for " + player.getDisplayName());
                                        world.execute(() -> {
                                                player.sendMessage(Message.raw(
                                                                "You are being teleported away to the surface!"));
                                                TeleportUtils.escapeToSurface(store, player, world);
                                        });
                                } catch (Exception e) {
                                        com.hypixel.hytale.logger.HytaleLogger.forEnclosingClass().atSevere()
                                                        .withCause(e).log("Error during teleport!");
                                }
                        }, 5, TimeUnit.SECONDS);
                }
        }

        @Override
        protected void simulateInteractWithBlock(@NonNullDecl InteractionType interactionType,
                        @NonNullDecl InteractionContext interactionContext, @NullableDecl ItemStack itemStack,
                        @NonNullDecl World world, @NonNullDecl Vector3i vector3i) {

        }
}