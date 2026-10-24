# TNT Arrow — spécification

Document vivant. Il décrit **ce qu'on construit** et **les décisions prises**.
Les instructions de travail permanentes sont dans `CLAUDE.md`, les pièges de la
machine et de Minecraft 26.3 dans `../SETUP-MC-MODDING-26.3.md` (et le §1 de
`../SETUP-MC-MODDING-26.2.md`).

Statut : étape 2 validée en jeu et relue (24/10/2026). Étape 3 (release) en cours : README, icône et liens écrits, tag et publication en attente du feu vert de Jérôme.
Dernière mise à jour : 24/10/2026.

Conventions de ce fichier :

- ✅ **Confirmé** — validé par Jérôme, ne pas changer sans le lui demander.
- 🔷 **Proposé** — choix de Claude Code, modifiable librement, à valider en passant.
- ❓ **Ouvert** — décision à prendre, listée en §9.

Toute valeur numérique de ce document est un **défaut de configuration** : elle
est ajustable dans le fichier de config (§6), sauf mention contraire.

---

## 1. Concept

Une flèche explosive : une TNT qu'on envoie à l'arc. On la crafte avec une
flèche et un bloc de TNT, on la tire à l'arc, à l'arbalète ou au distributeur,
et elle explose comme une TNT dès qu'elle touche quelque chose. Pour ouvrir une
paroi à distance, nettoyer un groupe de mobs ou faire sauter une base adverse —
avec le même coût qu'une TNT posée à la main.

**Hors périmètre** : plusieurs puissances de flèche (une seule flèche, la
puissance se règle en config) ; mèche ou retardement ; flèche incendiaire ;
effets de pluie de fragments ; compatibilité avec les enchantements qui ne
visent que la flèche vanilla (Infinité).

---

## 2. La flèche explosive

### 2.1 Recette ✅

Sans forme : **1 flèche + 1 TNT → 1 flèche explosive**. Catégorie `equipment`,
comme la flèche spectrale. Débloquée dans le livre de recettes par un
advancement qui se déclenche dès qu'on possède une TNT 🔷.

### 2.2 Tir ✅ / 🔷

- Arc et arbalète ✅ : l'item rejoint le tag `#minecraft:arrows`, que les deux
  armes lisent comme munitions. Le type d'entité rejoint aussi le tag d'entité
  `#minecraft:arrows`, que lisent Puissance, Frappe et l'advancement « Bien
  visé ». Tir multiple : comportement vanilla, sans code 🔷. Perforation :
  **neutralisée**, la flèche explose sur la première cible 🔷.
- Distributeur 🔷 : tire la flèche comme une flèche normale (vérifié :
  `DispenserBlock.registerProjectileBehavior(ItemLike)` est public).
- Onglet créatif 🔷 : **Combat**, juste après la flèche spectrale.

### 2.3 Explosion ✅

| Propriété | Valeur | Statut |
| --- | --- | --- |
| Déclenchement | **À l'impact**, sur un bloc comme sur une entité | ✅ |
| Puissance | **4,0**, celle d'une TNT ; clé `explosion.power` | ✅ réglable en config |
| Blocs | **Cassés**, comme une TNT (`ExplosionInteraction.TNT`) | ✅ |
| Gamerule `tnt_explodes` à `false` | La flèche ne fait **rien** : ni explosion, ni dégâts d'explosion 🔷 — elle disparaît | 🔷 |
| Feu | Non | 🔷 |
| Dégâts de la flèche elle-même sur une entité | Gardés (dégâts vanilla de flèche), **puis** explosion. Sur la cible touchée, ils ne s'additionnent pas : le coup de flèche arme l'invulnérabilité, l'explosion du même tick n'inflige que la différence, soit au total le plus fort des deux | 🔷 |
| Bloc touché | Réagit comme à une flèche (bloc cible, cloche, fleur de chorus), avant l'explosion | ✅ 24/10/2026 |
| Flèche ramassable | Non : elle n'existe plus après l'impact. **Limite acceptée** : une flèche qui apparaît déjà dans un bloc (distributeur collé à un mur, bloc tombé dessus) se plante sans impact, n'explose pas et reste ramassable | ✅ (limite acceptée 24/10/2026) |

⚠️ Vérifié au `javap` le 24/10/2026 : **c'est `PrimedTnt.explode` qui teste
`GameRules.TNT_EXPLODES`**, pas `Level.explode` ni l'`ExplosionInteraction`.
La flèche doit donc faire ce test elle-même.

Le tireur peut être pris dans l'explosion s'il tire à bout portant : voulu, comme
une TNT 🔷.

---

## 3. Rendu

- Texture d'item et texture d'entité (flèche en vol) **produites par script**
  dans `tools/` 🔷 : la flèche vanilla avec une pointe rouge et blanche de TNT.
- Renderer d'entité : un `ArrowRenderer` qui ne change que la texture, sur le
  modèle de `SpectralArrowRenderer` 🔷.

---

## 4. Textes

Nom : **Flèche explosive** (`fr_fr`), **TNT Arrow** (`en_us`) 🔷.

---

## 5. (sans objet)

Pas de génération de monde ni de bloc.

---

## 6. Configuration 🔷

Même format que Smart Chest : JSON dans `config/tntarrow.json`, lu au
démarrage (`FabricLoader.getInstance().getConfigDir()`), **redémarrage
requis**. Écrit seulement s'il manque, avec une clé `_comment` ; jamais
réécrit ; fichier invalide → défauts en mémoire et raison au log.

| Clé | Défaut | Règle |
| --- | --- | --- |
| `explosion.power` | `4.0` | décimal, de 0,5 à 10 🔷 |

- ⚠️ Rien n'est synchronisé : la valeur lue est celle du **serveur** (c'est
  lui qui fait exploser), le client n'en a pas besoin.

---

## 7. Décisions techniques

### 7.1 Identité

| Élément | Valeur | Statut |
| --- | --- | --- |
| Mod id | `tntarrow` | 🔷 |
| Nom affiché du mod | TNT Arrow | 🔷 |
| Package racine | `re.jerome.tntarrow` | 🔷 |
| `group` Gradle | `re.jerome` | ✅ |
| Version | `0.1.0+26.3` | ✅ |
| Versions du jeu | 26.3 seule | 🔷 |
| Licence | **MIT**, `Copyright (c) 2026 Jerome` | 🔷 |

Le dossier s'appelle `MOD TNT ARROW` et non `MOD MC TNT ARROW` comme le veut la
méthode : laissé tel quel 🔷.

### 7.2 Versions 🔷

Relevées sur le maven et le méta Fabric le 24/10/2026 :

```properties
minecraft_version=26.3
loader_version=0.19.5
loom_version=1.17.21
fabric_api_version=0.165.0+26.3
```

Gradle **9.6.0**, JDK 25, aucune mappings. Wrapper copié depuis Smart Chest.

⚠️ **Nouveau depuis les mémos** : le loader **0.20.1** est sorti et marqué
`stable`. Non essayé sur cette machine. On reste sur **0.19.5**, éprouvé, sauf
si Fabric API 0.165 exige 0.20 (à lire dans son `fabric.mod.json` à l'étape 1).
Loom : toujours la ligne 1.17 (1.17.21 la plus récente).

### 7.3 Architecture 🔷

Aucun mixin. Données là où c'est possible.

| Fichier | Rôle |
| --- | --- |
| `TntArrowMod` | point d'entrée : config, enregistrements, distributeur, onglet créatif |
| `TntArrowConfig` | lecture de `config/tntarrow.json` (§6) |
| `TntArrowItem extends ArrowItem` | surcharge `createArrow` (arc, arbalète) et `asProjectile` (distributeur) pour rendre une `TntArrow` |
| `TntArrow extends AbstractArrow` | `onHitEntity` → dégâts vanilla puis explosion ; `onHitBlock` → réaction du bloc (`onProjectileHit`) puis explosion ; `getDefaultPickupItem` → la flèche explosive |
| `client/TntArrowClient` | enregistre le renderer (`EntityRendererRegistry` de Fabric) |
| `client/TntArrowRenderer extends ArrowRenderer` | ne fournit que la texture |
| `data/minecraft/tags/item/arrows.json` | `"replace": false`, ajoute l'item `tntarrow:tnt_arrow` (munition de l'arc et de l'arbalète) |
| `data/minecraft/tags/entity_type/arrows.json` | `"replace": false`, ajoute le type d'entité (Puissance, Frappe, « Bien visé ») |
| `data/tntarrow/recipe/tnt_arrow.json` | recette sans forme |
| `data/tntarrow/advancement/recipes/combat/tnt_arrow.json` | déblocage de la recette |
| `assets/tntarrow/…` | `items/`, `models/item/`, `textures/item/`, `textures/entity/projectiles/`, `lang/` |
| `tools/GenerateTextures.java` | les deux textures, depuis celles de la flèche vanilla (§3) ; `java tools/GenerateTextures.java <minecraft-merged.jar>` |

Enregistrement de l'entité : la chaîne exacte de `EntityTypes.ARROW`, relevée
en §7.4.

### 7.4 À vérifier avant d'écrire du code

Déjà vérifié au `javap` sur `minecraft-merged.jar` 26.3, le 24/10/2026 :

- ~~`ArrowItem`~~ : `createArrow(Level, ItemStack, LivingEntity, ItemStack)` et
  `asProjectile(Level, Position, ItemStack, Direction)`, publics ;
  `implements ProjectileItem`.
- ~~`AbstractArrow`~~ (paquet `entity.projectile.arrow`) : `onHitEntity(EntityHitResult)`,
  `onHitBlock(BlockHitResult)` protégés ; `getDefaultPickupItem()` abstraite.
- ~~`SpectralArrow`~~ : trois constructeurs à reproduire — `(EntityType, Level)`,
  `(Level, LivingEntity, ItemStack, ItemStack)`, `(Level, double, double, double, ItemStack, ItemStack)`.
- ~~`Level.explode(Entity, double, double, double, float, Level$ExplosionInteraction)`~~
  existe ; `ExplosionInteraction` : `NONE, BLOCK, MOB, TNT, TRIGGER`.
- ~~`GameRules.TNT_EXPLODES`~~ : testé par `PrimedTnt.explode`, voir §2.3.
- ~~`ItemTags.ARROWS`~~ : tag vanilla `arrow`, `tipped_arrow`, `spectral_arrow`.
- ~~`DispenserBlock.registerProjectileBehavior(ItemLike)`~~ public.
- ~~`ArrowRenderer<T, S>`~~ abstrait, seul `getTextureLocation(S)` à fournir ;
  `SpectralArrowRenderer` utilise `ArrowRenderState`.
- ~~`EntityType.Builder`~~ : `of`, `sized`, `clientTrackingRange`, `updateInterval`,
  `build(ResourceKey)`.

Vérifié ensuite au `javap`, le 24/10/2026 (avant l'étape 2) :

- ~~Valeurs de `EntityTypes.ARROW`~~ (clé `EntityTypeIds.ARROW`) :
  `Builder.of(…, MISC).noLootTable().sized(0.5F, 0.5F).eyeHeight(0.13F).clientTrackingRange(4).updateInterval(20)`.
  Reprendre la même chaîne, `noLootTable()` et `eyeHeight` compris.
- ~~Distributeur automatique ?~~ **Non** : `DispenseItemBehavior.bootStrap`
  appelle `DispenserBlock.registerProjectileBehavior` **item par item**, en
  dur. Le mod doit l'appeler pour son item.
- ~~Corps de `PrimedTnt.explode`~~ : `if (level instanceof ServerLevel && gameRules.get(TNT_EXPLODES))`
  puis `level.explode(this, Explosion.getDefaultDamageSource(level, this),
  calculator /* null, sauf portail */, getX(), getY(0.0625), getZ(),
  explosionPower, false, ExplosionInteraction.TNT)` — surcharge
  `explode(Entity, DamageSource, ExplosionDamageCalculator, double, double, double, float, boolean, ExplosionInteraction)`.
  Le `false` est le feu.
- ~~Imputation~~ : **automatique**. `Explosion.getDefaultDamageSource` passe par
  `getIndirectSourceEntity`, qui rend `Projectile.getOwner()` s'il est un
  `LivingEntity` ; `DamageSources.explosion(source, owner)` donne alors
  `PLAYER_EXPLOSION`. Passer la flèche comme source suffit.
- ~~`AbstractArrow.onHitEntity`~~ : appelle `Projectile.onHitEntity`, calcule
  les dégâts, puis `hurtOrSimulate`. Deux sorties sans retrait de l'entité à
  connaître : le **rebond** (dégâts refusés : cible invulnérable ou en créatif,
  bouclier qui bloque tout ; l'Enderman, lui, n'est pas un rebond, la flèche
  vanilla le traverse) et
  la **perforation** (`getPierceLevel() > 0`, la flèche traverse et continue).
  Il y a aussi un `discard` + `return` anticipé quand la liste de perforation
  est pleine. → Dans la flèche explosive : `super.onHitEntity` **puis**
  explosion **inconditionnelle** (côté serveur), puis `discard`. Garde contre la
  double explosion : ne rien faire si `isRemoved()`, testé **avant** `super` —
  après, il serait toujours vrai sur un coup normal, que `super` termine par
  `discard`. (La boucle `stepMoveAndHit` teste déjà `isAlive()` avant chaque
  impact : la garde double cette protection.)
  ⚠️ `onHitEntity` fait aussi `setArrowCount(+1)` sur une cible vivante : un
  joueur touché garde une flèche **vanilla** plantée dans le corps (rendu
  vanilla des flèches plantées). Accepté 🔷, c'est cosmétique.
- ~~`AbstractArrow.onHitBlock`~~ : stocke `lastState`, effets d'enchantement,
  recule la flèche, joue le son, passe en `inGround`. → Dans la flèche
  explosive : sans `super` (la flèche ne doit pas se planter), mais avec la part
  de `Projectile.onHitBlock`, `state.onProjectileHit(level, state, hit, this)`,
  pour que le bloc réagisse (cible, cloche) ; puis explosion ✅. Les effets
  d'enchantement `hit_block` sont sautés.
- ~~Sous l'eau~~ : **automatique**. `ServerExplosion` lit le `FluidState` de
  chaque position du rayon et sa résistance passe par
  `ExplosionDamageCalculator.getBlockExplosionResistance(…, BlockState, FluidState)` :
  l'eau (résistance 100) protège les blocs comme pour une TNT.
- ~~Format de l'advancement~~ (`recipes/combat/spectral_arrow.json` du jar
  26.3) : `"parent": "minecraft:recipes/root"`, critères `inventory_changed`
  (`"items": [{"items": "minecraft:tnt"}]`) et `recipe_unlocked` avec
  **`"recipes"`** (pluriel), `requirements` en OU, `rewards.recipes`. L'id
  `tntarrow:tnt_arrow` doit exister dans `data/tntarrow/recipe/`.

---

## 8. Plan d'implémentation 🔷

Chaque étape se termine par un build vert et, quand c'est visible, un test en
jeu. Sous-agent de vérification avant chaque commit dans les catégories
obligatoires de `CLAUDE.md`.

| # | Étape | Contenu | Validé quand | État |
| --- | --- | --- | --- | --- |
| 1 | Squelette | git, wrapper Gradle, `fabric.mod.json`, licence, `.gitattributes`, `.gitignore`, config (§6) lue au démarrage | le jeu se lance avec le mod listé, sans crash ; `config/tntarrow.json` apparaît | ✅ validée 24/10/2026 |
| 2 | La flèche | item, entité, explosion, recette, advancement, tag, distributeur, onglet, textures par script, renderer, traductions | voir ci-dessous | ✅ validée 24/10/2026 |
| 3 | Release | README (page Modrinth), icône, liens `contact`, version, tag, jar dans `_Publiés` | Jérôme a le jar dans son instance | 🔶 README, icône et liens commités ; tag et publication à venir |

« Validé quand » de l'étape 2, joué par Jérôme :

1. Flèche + TNT dans la table de craft → 1 flèche explosive ; la recette est
   dans le livre après avoir eu une TNT.
2. Tir à l'arc sur un mur de terre : explosion de la taille d'une TNT, trou dans
   le mur.
3. Tir à l'arc sur un zombie : il prend la flèche puis l'explosion.
4. Tir à l'arbalète, et à l'arbalète Tir multiple : 3 explosions, 1 flèche
   consommée.
5. Distributeur alimenté par un bouton : il tire, ça explose.
6. `/gamerule tnt_explodes false` : la flèche ne casse rien et ne blesse pas
   d'explosion.
7. `explosion.power` à 1 dans la config, relance : petite explosion.
8. Flèche en vol au moment d'un « Sauvegarder et quitter » (tir vers le ciel),
   relance : elle retombe et explose.
9. La flèche en vol a sa texture, l'item la sienne, nom en français.

Étape 2 : relecture Opus obligatoire (dégâts).

---

## 9. Questions ouvertes ❓

| # | Question | Impact |
| --- | --- | --- |
| 1 | ~~Puissance~~ — 24/10/2026 : **réglable en config, 4 par défaut** (comme une TNT). | §2.3, §6 |
| 2 | ~~Casse les blocs ?~~ — 24/10/2026 : **oui, comme la TNT**. | §2.3 |
| 3 | ~~Déclenchement~~ — 24/10/2026 : **à l'impact**. | §2.3 |
| 4 | ~~Recette~~ — 24/10/2026 : **1 flèche + 1 TNT → 1**. | §2.1 |
| 5 | ~~Publication~~ — 24/10/2026 : **dépôt GitHub public `Jacob-Ps231/tnt-arrow`, release GitHub et page Modrinth faite à la main**, comme les autres mods. Chaque action publique (création du dépôt, tag, push, release) attend un accord explicite dans la session qui l'exécute. | §8 étape 3 |

---

## 10. Journal des décisions

| Date | Décision |
| --- | --- |
| 24/10/2026 | Spec écrite. Jérôme a tranché les questions 1 à 4. Les vérifications de §7.4 ont montré que le test de la gamerule `tnt_explodes` est dans `PrimedTnt`, pas dans `Level.explode` : à reproduire dans la flèche. Fabric loader 0.20.1 découvert, stable, non adopté (§7.2). |
| 24/10/2026 | Étape 1 codée. Copié de Smart Chest : `build.gradle` (sans access widener), `settings.gradle`, `gradle.properties`, `.gitattributes`, `LICENSE`, wrapper (déjà Gradle 9.6.0), et le patron de `SmartChestConfig` (deux codecs, `_comment`, fichier jamais réécrit). Fabric API 0.165.0+26.3 déclare `fabricloader >=0.19.3` : le loader 0.19.5 convient, le 0.20.1 reste inutile. `GSON` de la config avec `disableHtmlEscaping()` : sans cela, l'apostrophe du `_comment` s'écrit `'` dans un fichier fait pour être lu à la main (Smart Chest a le même défaut). `runServer` demande d'abord `run/eula.txt` à `eula=true` (accord de Jérôme du 24/10/2026, dossier `run/` non suivi). Le bruit de log hors-ligne du client (authlib 401, Realms, Perflib) n'a rien à voir avec le mod. |
| 24/10/2026 | Étape 2 codée : `TntArrowItem`, `TntArrow`, enregistrements dans `TntArrowMod` (champs statiques : item puis type d'entité ; distributeur et onglet dans `onInitialize`), `client/TntArrowClient` et `TntArrowRenderer`, données et textures. **Révélé faux** : la gamerule s'appelle **`tnt_explodes`** en 26.3 (ids en snake_case), pas `tntExplodes` — corrigé en §2.3 et §8. **Piège évité** : la garde `isRemoved()` contre la double explosion, placée après `super.onHitEntity`, aurait supprimé l'explosion de tout coup normal (`super` y finit par `discard`) ; elle est avant (§7.4). `EntityRendererRegistry` de Fabric est déprécié sans remplaçant (`EntityRenderers.register` privé, aucun access widener) : gardé, comme `BlockEntityRendererRegistry` dans Smart Chest. Textures : la pointe grise de la flèche vanilla recolorée avec les quatre rouges de `tnt_side.png` selon sa luminosité, une bande blanche en travers (diagonale sur l'item, une colonne par face sur l'entité). `runServer` exercé après `Done` par des commandes : flèche invoquée au-dessus d'un mur de terre → mur percé et flèche retirée ; `tnt_explodes` à `false` → mur intact et flèche retirée ; flèche en vol (`NoGravity`) sauvegardée par `save-all flush` sans erreur. Laissé à la relecture : une flèche **renvoyée** (rebond) presque à l'arrêt lâche son item (`pickup` `ALLOWED`) dans `super`, puis explose ; l'explosion détruit normalement l'item lâché. |
| 24/10/2026 | Étape 2 validée en jeu par Jérôme (9 gestes). Relecture Opus : ni double explosion, ni fuite côté client, ni duplication. **Manque trouvé** : le tag d'**entité** `#minecraft:arrows`, que lisent Puissance et Frappe (sur l'attaquant direct) ; sans lui, un arc Puissance V n'augmentait pas les dégâts de flèche → ajouté. **Révélé faux** : l'Enderman n'est pas un rebond (la flèche vanilla le traverse) ; la perforation n'est pas vanilla mais neutralisée ; les dégâts de flèche et d'explosion ne s'additionnent pas sur la cible (invulnérabilité de 20 ticks). Décisions de Jérôme : le bloc touché réagit comme à une flèche (`onProjectileHit` avant l'explosion, vérifié par `runServer` : un bloc cible émet son signal avec `tnt_explodes` à `false`) ; une flèche qui apparaît déjà dans un bloc reste plantée sans exploser (limite acceptée, §2.3). Le rebond quasi à l'arrêt qui lâche l'item : l'explosion le détruit ; avec `tnt_explodes` à `false` l'item survit, sans duplication. Clé d'onglet Combat : reconstruite, bien que Fabric API la rende accessible (mémo 26.3 §11). |
| 24/10/2026 | Étape 3 préparée. `README.md` écrit d'après le code et les données, pas de mémoire : **Infinité ne s'applique pas** (`infinity.json` ne vise que `minecraft:arrow` via `match_tool` : la flèche explosive est toujours consommée) ; l'advancement « Bien visé » s'appelle *Take Aim* en anglais (pas *Sniper Duel*) ; kills crédités au tireur via `Explosion.getDefaultDamageSource`. Icône : capture « craft » de Jérôme (flèche et TNT dans la grille 2×2), recadrée en carré 112×112 et agrandie ×4 au plus proche voisin (448×448) ; source gardée dans `ref/` (non suivi). Liens `contact` et `icon` ajoutés à `fabric.mod.json`. `clean build` vert, jar vérifié (classes main et client, assets, données, icône, licence ; ni `tools/` ni PNG de travail). |
