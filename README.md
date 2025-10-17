# AntiItemExplosions v1.0.1  
by [Tellegram](https://github.com/Tellegram)

Prevents protected blocks from breaking in explosions and protects whitelisted dropped items from fire, lava, magma, and explosions.  
Pure API plugin — no NMS or reflection.

---

## Features
- Removes protected blocks from `blockList()` on TNT and creeper explosions  
- Cancels damage and combustion for protected dropped items  
  (`FIRE`, `FIRE_TICK`, `LAVA`, `HOT_FLOOR`, `ENTITY_EXPLOSION`, `BLOCK_EXPLOSION`)  
- Optional per-world filtering via `enabled-worlds`  
- Configurable toggles for explosions and fire/lava  
- Pattern matching for materials (`WOODEN_*`, `NETHERITE_*`, etc.)

---

## Compatibility
| Component | Supported |
|------------|------------|
| Servers | Spigot, Paper, Purpur, Folia |
| Versions | 1.20.x – 1.21.x |
| Java | 17 |
| API | Bukkit/Paper only (`plugin.yml: api-version: "1.21"`) |
| NMS | Not used |

---

## Installation
1. Download or build the JAR file.  
2. Place it in your server’s `plugins/` folder.  
3. Start the server once to generate the configuration file.  
4. Edit `plugins/AntiItemExplosions/config.yml` as needed.  
5. Apply changes with `/antiexpl reload`.

---

## Commands & Permissions
| Command | Description | Permission |
|----------|--------------|-------------|
| `/antiexpl reload` | Reloads configuration | `antiexpl.reload` *(default: op)* |

---

## Configuration
**File:** `config.yml`  
**Main Section:** `ItemDestruction`  
**Lists:**  
- `BLOCKS` → protected block materials  
- `ITEMS` → protected dropped items  
Supports pattern matching (e.g., `WOODEN_*`).  
**Toggles:**  
- `protect.explosions`  
- `protect.fire_lava`  
**Worlds:**  
- `enabled-worlds` (empty = all worlds)

### Example
```yaml
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
```
Protects only dropped items in the world

Does not protect inventories, containers, item frames, or armor stands

No protection from cactus, void, hopper transport, minecarts, or ClearLag removals

Listeners registered at HIGHEST priority with ignoreCancelled=false for maximum compatibility

Testing Summary

Startup:

Tested on Paper/Purpur 1.20.6–1.21.4 with Java 17

No console warnings or errors

Explosion tests:

TNT or creeper near whitelisted blocks → remain intact

Non-whitelisted blocks → break normally

Fire/lava tests:

Whitelisted items dropped on fire/lava/magma → remain

Non-whitelisted → destroyed

Explosion damage tests:

Whitelisted items near TNT/creeper → survive

Non-whitelisted → destroyed

World filters:

enabled-worlds: [world] → active only in listed worlds

Toggles:

protect.explosions=false disables block protection

protect.fire_lava=false disables item fire protection

Reload:

/antiexpl reload updates configuration without restart

Support:

or join the community: discord.gg/DCUQJuDbhB

