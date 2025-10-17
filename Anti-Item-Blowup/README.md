# AntiItemExplosions by Tellegram v1.0.1

Protects whitelisted blocks from explosions and whitelisted dropped items from fire, lava, magma, and explosions. API-only. No NMS.

---

## Features
- Removes protected blocks from `blockList()` on TNT and creeper explosions.
- Cancels damage/combust for protected **dropped items** (`FIRE`, `FIRE_TICK`, `LAVA`, `HOT_FLOOR`, `ENTITY_EXPLOSION`, `BLOCK_EXPLOSION`).
- Optional per-world filter via `enabled-worlds`.
- Simple toggles: `protect.explosions`, `protect.fire_lava`.
- Pattern support: `WOODEN_*` matches all materials starting with `WOODEN_`.

## Compatibility
- Servers: Spigot, Paper, Purpur, Folia (no async world access).
- Versions: 1.20.x–1.21.x (`plugin.yml` `api-version: "1.21"`).
- Java: 17.
- No NMS. Only Bukkit/Paper events.

## Installation
1. Download or build the jar.
2. Put it in `plugins/`.
3. Start the server once to generate `config.yml`.
4. Edit `config.yml`.
5. Run `/antiexpl reload` to apply changes.

## Commands & Permissions
- Command: `/antiexpl reload`
- Permission: `antiexpl.reload` (default: op)

## Configuration
**File:** `config.yml`  
**Section:** `ItemDestruction`  
**Lists:** `BLOCKS`, `ITEMS` (use Spigot material names; patterns like `NETHERITE_*` allowed)  
**Worlds:** `enabled-worlds` (empty = all worlds)  
**Toggles:** `protect.explosions`, `protect.fire_lava`

### Example
```yml
ItemDestruction:
  BLOCKS:
    - CHEST
    - ENDER_CHEST
    - GLASS
    - STONE
  ITEMS:
    - DIAMOND
    - NETHERITE_SWORD
enabled-worlds: [world, world_nether]
protect:
  explosions: true
  fire_lava: true
Limits
Protects only dropped items in the world. Does not protect inventories, chests, item frames, armor stands.

No protection against cactus, void, ClearLag removals, hoppers, minecarts.

Listeners run at HIGHEST with ignoreCancelled=false so the whitelist wins last.

Test Plan
Startup: Spigot/Paper/Purpur 1.20.6–1.21.4, Java 17. Zero errors or warnings.

Explosions: TNT/creeper near whitelisted BLOCKS → they remain. Non-whitelisted blocks break.

Items vs fire/lava: Drop whitelisted ITEMS onto fire/lava/magma → no damage or combustion. Non-whitelisted burns.

Items vs explosions: Drop whitelisted ITEMS near TNT/creeper → damage cancelled.

World filter: Set enabled-worlds; [world] → protection active only in world.

Toggles: Set protect.explosions=false and protect.fire_lava=false → respective protections disabled.

Reload: Edit config → `/antiexpl reload` → new lists and toggles take effect. Success message shows counts.

Support
Community: https://discord.gg/DCUQJuDbhB


Author
Tellegram — https://github.com/Tellegram

Repo: https://github.com/Tellegram/AntiItemExplosions