package de.tellegram.antiexpl;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

public final class AntiItemExplosions extends JavaPlugin implements Listener {

    private final Set<Material> protectedBlocks = EnumSet.noneOf(Material.class);
    private final Set<Material> protectedItems = EnumSet.noneOf(Material.class);
    private final Set<String> enabledWorlds = new HashSet<>();
    private boolean protectExplosions = true;
    private boolean protectFireLava = true;

    @Override
    public void onEnable() {
        logStartupBanner();
        saveDefaultConfig();
        reloadProtection();
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void reloadConfig() {
        super.reloadConfig();
        reloadProtection();
    }

    private void reloadProtection() {
        protectedBlocks.clear();
        protectedItems.clear();
        enabledWorlds.clear();

        enabledWorlds.addAll(getConfig().getStringList("enabled-worlds"));
        ConfigurationSection protect = getConfig().getConfigurationSection("protect");
        protectExplosions = protect == null || protect.getBoolean("explosions", true);
        protectFireLava = protect == null || protect.getBoolean("fire_lava", true);

        ConfigurationSection root = getConfig().getConfigurationSection("ItemDestruction");
        if (root == null) {
            getLogger().warning("Missing ItemDestruction section in config.yml");
            return;
        }

        addMaterials(root.getStringList("BLOCKS"), protectedBlocks, "BLOCKS");
        addMaterials(root.getStringList("ITEMS"), protectedItems, "ITEMS");

        getLogger().info("blocks=" + protectedBlocks.size() + ", items=" + protectedItems.size());
        getLogger().info("worlds=" + (enabledWorlds.isEmpty() ? "all" : enabledWorlds) + ", toggles={explosions=" + protectExplosions + ", fire_lava=" + protectFireLava + "}");
    }

    private void addMaterials(List<String> names, Set<Material> target, String sectionName) {
        for (String s : names) {
            if (s == null || s.isEmpty()) continue;
            if (s.endsWith("_*")) {
                String prefix = s.substring(0, s.length() - 2);
                int added = 0;
                for (Material m : Material.values()) {
                    if (m.name().startsWith(prefix)) {
                        target.add(m);
                        added++;
                    }
                }
                if (added == 0) {
                    getLogger().log(Level.WARNING, "Prefix '{0}' in {1} matched nothing", new Object[]{prefix, sectionName});
                }
            } else {
                Material m = Material.matchMaterial(s);
                if (m != null) target.add(m);
                else getLogger().log(Level.WARNING, "Invalid material '{0}' in {1}", new Object[]{s, sectionName});
            }
        }
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.HIGHEST)
    public void onEntityExplode(EntityExplodeEvent e) {
        if (!shouldProtectExplosions(e.getLocation().getWorld())) return;
        filterExplodedBlocks(e.blockList());
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.HIGHEST)
    public void onBlockExplode(BlockExplodeEvent e) {
        if (!shouldProtectExplosions(e.getBlock().getWorld())) return;
        filterExplodedBlocks(e.blockList());
    }

    private void filterExplodedBlocks(List<Block> list) {
        if (protectedBlocks.isEmpty()) return;
        Iterator<Block> it = list.iterator();
        while (it.hasNext()) {
            Block b = it.next();
            if (protectedBlocks.contains(b.getType())) it.remove();
        }
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.HIGHEST)
    public void onItemDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Item item)) return;
        if (!isWorldEnabled(item.getWorld())) return;
        Material mat = item.getItemStack().getType();
        if (!protectedItems.contains(mat)) return;

        switch (e.getCause()) {
            case ENTITY_EXPLOSION, BLOCK_EXPLOSION -> {
                if (!protectExplosions) return;
                e.setCancelled(true);
                item.setFireTicks(0);
            }
            case FIRE, FIRE_TICK, LAVA, HOT_FLOOR -> {
                if (!protectFireLava) return;
                e.setCancelled(true);
                item.setFireTicks(0);
            }
            default -> {
            }
        }
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.HIGHEST)
    public void onItemCombust(EntityCombustEvent e) {
        if (!(e.getEntity() instanceof Item item)) return;
        if (!isWorldEnabled(item.getWorld())) return;
        if (protectedItems.contains(item.getItemStack().getType())) {
            if (!protectFireLava) return;
            e.setCancelled(true);
            item.setFireTicks(0);
        }
    }

    private boolean isWorldEnabled(World world) {
        return enabledWorlds.isEmpty() || enabledWorlds.contains(world.getName());
    }

    private boolean shouldProtectExplosions(World world) {
        return protectExplosions && isWorldEnabled(world);
    }

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
        if (!cmd.getName().equalsIgnoreCase("antiexpl")) return false;
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (!s.hasPermission("antiexpl.reload")) { s.sendMessage("§cNo permission."); return true; }
            reloadConfig();
            s.sendMessage("§aReloaded. blocks=" + protectedBlocks.size() + ", items=" + protectedItems.size());
            return true;
        }
        s.sendMessage("§7Usage: §f/antiexpl reload");
        return true;
    }

    public int getProtectedBlocksCount() { return protectedBlocks.size(); }
    public int getProtectedItemsCount() { return protectedItems.size(); }

    private void logStartupBanner() {
        String banner = """
             _         _ _ _               _                      _                 
            / \\  _   _(_) | | ___ _ __ ___(_) _ __   ___ _ __ __ _| |_ ___  _ __ ___ 
           / _ \\| | | | | | |/ _ \\ '__/ __| | '_ \\ / _ \\ '__/ _` | __/ _ \\| '__/ __| 
          / ___ \\ |_| | | | |  __/ |  \\__ \\ | | | |  __/ | | (_| | || (_) | |  \\__ \\ 
         /_/   \\_\\__,_|_|_|_|\\___|_|  |___/_|_| |_|\\___|_|  \\__,_|\\__\\___/|_|  |___/
        """;
        getLogger().info("\n" + banner + "\nAnti-Item-Explosions v" + getDescription().getVersion() + " loaded.");
    }
}