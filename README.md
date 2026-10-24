# TNT Arrow

An arrow that **explodes like a block of TNT** the moment it hits something. Craft it from an arrow and a block of
TNT, shoot it with a bow, a crossbow or a dispenser, and open a wall from a distance, clear a group of mobs or blow
up an enemy base — for the same price as a TNT placed by hand.

For Minecraft **26.3**, on Fabric.

<!-- Screenshot: a TNT Arrow flying towards a dirt wall -->

## Recipe

**1 arrow + 1 TNT → 1 TNT Arrow.** The recipe is shapeless, so it also fits the 2×2 crafting grid of your inventory.
It shows up in the recipe book as soon as you pick up a block of TNT.

<!-- Screenshot: the crafting screen with the arrow, the TNT and the result -->

You find the TNT Arrow in the *Combat* creative tab, right after the spectral arrow.

## Shooting it

- **Bow and crossbow.** The TNT Arrow is ammunition like any other arrow.
- **Dispenser.** It fires the TNT Arrow as it fires a normal arrow.
- **Multishot** works as in vanilla: three arrows leave the crossbow, three explosions, one arrow used up.
- **Piercing** is switched off: the arrow explodes on the first target.
- **Power and Punch** apply as with any arrow.
- **Infinity** does not: it only spares plain arrows, so a TNT Arrow is always used up.

## The explosion

- **On impact**, on a block as on a creature. Nothing waits, there is no fuse.
- **The power of a block of TNT** by default (4.0), set in the [configuration](#configuration).
- **Blocks break**, as with a TNT. There is **no fire**.
- **Underwater**, water protects the blocks around the blast, as it does for a TNT.
- **Kills are credited to whoever shot the arrow.** Death messages and kill counts follow the shooter.
- **A creature hit by the arrow takes the arrow's damage and the blast.** The two do not add up: the arrow's hit
  starts the usual invulnerability delay, so the explosion of the same tick only adds the difference. In practice
  the target takes the stronger of the two.
- **It explodes even if the target blocks the damage** — a creative player, an invulnerable mob, a raised
  shield.
- **The block it hits reacts as to a normal arrow**, just before the blast: a target block emits its signal, a bell
  rings.
- **The arrow is gone after the impact**, nothing to pick up. In flight, it is saved with the world like any other
  arrow: if you quit while it is in the air, it falls and explodes once you come back.
- **The shooter can be caught in the blast** if they shoot at point-blank range, as with a TNT.
- **The `tnt_explodes` game rule applies.** With `/gamerule tnt_explodes false`, the arrow explodes nothing and
  hurts nothing: it just disappears.

<!-- Screenshot: the hole left in a wall -->

## Configuration

`config/tntarrow.json`, written with its defaults the first time the game starts. Restart the game to apply a
change.

```json
{
  "explosion": { "power": 4.0 }
}
```

| Key | Default | Rule |
| --- | --- | --- |
| `explosion.power` | `4.0` | a decimal number from **0.5** to **10**. 4.0 is a block of TNT |

- A missing key takes its default.
- A file with an invalid value is **ignored as a whole**: the defaults apply, the log says why, and the file is
  **never overwritten**. Delete it to get a fresh one.
- **Only the server's copy counts**, since the server makes the arrow explode. In a single-player world, that is
  your own file. The client needs no matching value.

## Data pack tags

| Tag | Type | Content |
| --- | --- | --- |
| `#minecraft:arrows` | item | the TNT Arrow is added: this is what lets bows and crossbows load it |
| `#minecraft:arrows` | entity type | the TNT Arrow is added: Power, Punch and the *Take Aim* advancement read it |

The recipe is plain JSON in `data/tntarrow/recipe/` and can be overridden by a data pack.

## Requirements

- Minecraft **26.3**
- Fabric Loader **0.19.5** or newer
- [Fabric API](https://modrinth.com/mod/fabric-api) — the only dependency
- Java 25

Install it on the **client and on the server**. Removing the mod from a world deletes the TNT Arrows it holds,
arrows in flight included.

## Known limits

- A creature hit by a TNT Arrow keeps a **vanilla** arrow stuck in its body. It is cosmetic: that arrow cannot be
  picked up.
- An arrow that **appears already inside a block** — a dispenser against a wall, a block falling on it — sticks
  there without an impact: it does not explode and can be picked up.
- The enchantments that trigger when an arrow hits a block are skipped.

## Credits and license

Released under the **MIT License** — see `LICENSE`.

The textures are produced by script (`tools/`) from the vanilla arrow and the TNT block, and the icon is a
screenshot taken in game; none comes from generative AI. Not an official Minecraft product; not approved by or
associated with Mojang or Microsoft.
