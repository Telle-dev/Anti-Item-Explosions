package de.tellegram.antiexpl;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.WorldMock;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings({"deprecation", "removal"})
class AntiItemExplosionsTest {

    private ServerMock server;
    private AntiItemExplosions plugin;

    @BeforeEach
    void setUp() {
        server = MockBukkit.mock();
        plugin = MockBukkit.load(AntiItemExplosions.class);
    }

    @AfterEach
    void tearDown() {
        MockBukkit.unmock();
    }

    @Test
    void loadsWhitelistCounts() {
        assertTrue(plugin.getProtectedBlocksCount() > 0, "blocks should be loaded");
        assertTrue(plugin.getProtectedItemsCount() > 0, "items should be loaded");
    }

    @Test
    @SuppressWarnings({"deprecation", "removal"})
    void cancelsFireDamageForProtectedItem() {
        WorldMock world = server.addSimpleWorld("world");
        ItemStack stack = new ItemStack(Material.TRIDENT);
        Item item = world.dropItem(new Location(world, 0, 64, 0), stack);

        EntityDamageEvent event = new EntityDamageEvent(item, EntityDamageEvent.DamageCause.FIRE, 1.0);
        plugin.onItemDamage(event);

        assertTrue(event.isCancelled(), "fire damage should be cancelled");
        assertEquals(0, item.getFireTicks(), "item fire ticks should be reset");
    }

    @Test
    @SuppressWarnings({"deprecation", "removal"})
    void cancelsCombustForProtectedItem() {
        WorldMock world = server.addSimpleWorld("world");
        ItemStack stack = new ItemStack(Material.TRIDENT);
        Item item = world.dropItem(new Location(world, 0, 64, 0), stack);

        EntityCombustEvent event = new EntityCombustEvent(item, 10);
        plugin.onItemCombust(event);

        assertTrue(event.isCancelled(), "combust should be cancelled");
        assertEquals(0, item.getFireTicks(), "item fire ticks should be reset");
    }
}