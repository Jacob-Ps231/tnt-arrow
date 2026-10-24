# TNT Arrow — spécification

Document vivant. Il décrit **ce qu'on construit** et **les décisions prises**.
Les instructions de travail permanentes sont dans `CLAUDE.md`, les pièges de la
machine et de Minecraft 26.3 dans `../SETUP-MC-MODDING-26.3.md` (et le §1 de
`../SETUP-MC-MODDING-26.2.md`).

Statut : étape 1 codée, en attente de validation en jeu. Prochaine session : étape 2 (la flèche).
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
  armes lisent comme munitions. Tir multiple et perforation de l'arbalète :
  comportement vanilla, sans code 🔷.
- Distributeur 🔷 : tire la flèche comme une flèche normale (vérifié :
  `DispenserBlock.registerProjectileBehavior(ItemLike)` est public).
- Onglet créatif 🔷 : **Combat**, juste après la flèche spectrale.

### 2.3 Explosion ✅

| Propriété | Valeur | Statut |
| --- | --- | --- |
| Déclenchement | **À l'impact**, sur un bloc comme sur une entité | ✅ |
| Puissance | **4,0**, celle d'une TNT ; clé `explosion.power` | ✅ réglable en config |
| Blocs | **Cassés**, comme une TNT (`ExplosionInteraction.TNT`) | ✅ |
| Gamerule `tntExplodes` à `false` | La flèche ne fait **rien** : ni explosion, ni dégâts d'explosion 🔷 — elle disparaît | 🔷 |
| Feu | Non | 🔷 |
| Dégâts de la flèche elle-même sur une entité | Gardés (dégâts vanilla de flèche), **puis** explosion | 🔷 |
| Flèche ramassable | Non : elle n'existe plus après l'impact | ✅ (découle du déclenchement) |

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
| `TntArrow extends AbstractArrow` | `onHitEntity` → dégâts vanilla puis explosion ; `onHitBlock` → explosion ; `getDefaultPickupItem` → la flèche explosive |
| `client/TntArrowClient` | enregistre le renderer (`EntityRendererRegistry` de Fabric) |
| `client/TntArrowRenderer extends ArrowRenderer` | ne fournit que la texture |
| `data/minecraft/tags/item/arrows.json` | `"replace": false`, ajoute `tntarrow:tnt_arrow` |
| `data/tntarrow/recipe/tnt_arrow.json` | recette sans forme |
| `data/tntarrow/advancement/recipes/combat/tnt_arrow.json` | déblocage de la recette |
| `assets/tntarrow/…` | `items/`, `models/item/`, `textures/item/`, `textures/entity/projectiles/`, `lang/` |

Enregistrement de l'entité : `EntityType.Builder.of(TntArrow::new,
MobCategory.MISC).sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(20)`,
valeurs de la flèche vanilla à relever.

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

Reste à vérifier :

- [ ] Les valeurs de `EntityType.ARROW` (taille, portée, intervalle) — dans
      `EntityTypes` en 26.3 (mémo 26.3 §9).
- [ ] Le distributeur n'enregistre-t-il pas déjà tout `ProjectileItem` tout
      seul ? (enregistrement au bootstrap, donc avant les items du mod.)
- [ ] Le corps de `PrimedTnt.explode` : source de dégâts
      (`Explosion.getDefaultDamageSource`), position (au centre ou `getY(0.0625)`),
      pour reprendre le même appel.
- [ ] Ordre dans `onHitEntity` : la flèche vanilla peut **rebondir** (cible
      invulnérable, Enderman) sans être retirée — exploser quand même dans ce
      cas. Lire le corps d'`AbstractArrow.onHitEntity`.
- [ ] Sous l'eau : la TNT n'y casse pas de blocs, l'explosion le gère seule ?
- [ ] Imputation : un mob tué par l'explosion est-il crédité au tireur
      (`getOwner()`) ?
- [ ] Format de l'advancement de recette en 26.3 : `"recipes"` (mémo 26.3 §7 d),
      et l'id de recette doit exister (§9 f), sinon tout le datapack est refusé.

---

## 8. Plan d'implémentation 🔷

Chaque étape se termine par un build vert et, quand c'est visible, un test en
jeu. Sous-agent de vérification avant chaque commit dans les catégories
obligatoires de `CLAUDE.md`.

| # | Étape | Contenu | Validé quand | État |
| --- | --- | --- | --- | --- |
| 1 | Squelette | git, wrapper Gradle, `fabric.mod.json`, licence, `.gitattributes`, `.gitignore`, config (§6) lue au démarrage | le jeu se lance avec le mod listé, sans crash ; `config/tntarrow.json` apparaît | ✅ codée, validation de Jérôme en attente |
| 2 | La flèche | item, entité, explosion, recette, advancement, tag, distributeur, onglet, textures par script, renderer, traductions | voir ci-dessous | |
| 3 | Release | README (page Modrinth), version, tag, jar dans `_Publiés` | Jérôme a le jar dans son instance | |

« Validé quand » de l'étape 2, joué par Jérôme :

1. Flèche + TNT dans la table de craft → 1 flèche explosive ; la recette est
   dans le livre après avoir eu une TNT.
2. Tir à l'arc sur un mur de terre : explosion de la taille d'une TNT, trou dans
   le mur.
3. Tir à l'arc sur un zombie : il prend la flèche puis l'explosion.
4. Tir à l'arbalète, et à l'arbalète Tir multiple : 3 explosions, 1 flèche
   consommée.
5. Distributeur alimenté par un bouton : il tire, ça explose.
6. `/gamerule tntExplodes false` : la flèche ne casse rien et ne blesse pas
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
| 5 | Dépôt GitHub public `Jacob-Ps231/tnt-arrow` et page Modrinth, comme les autres mods ? | §8 étape 3 |

---

## 10. Journal des décisions

| Date | Décision |
| --- | --- |
| 24/10/2026 | Spec écrite. Jérôme a tranché les questions 1 à 4. Les vérifications de §7.4 ont montré que le test de la gamerule `tntExplodes` est dans `PrimedTnt`, pas dans `Level.explode` : à reproduire dans la flèche. Fabric loader 0.20.1 découvert, stable, non adopté (§7.2). |
| 24/10/2026 | Étape 1 codée. Copié de Smart Chest : `build.gradle` (sans access widener), `settings.gradle`, `gradle.properties`, `.gitattributes`, `LICENSE`, wrapper (déjà Gradle 9.6.0), et le patron de `SmartChestConfig` (deux codecs, `_comment`, fichier jamais réécrit). Fabric API 0.165.0+26.3 déclare `fabricloader >=0.19.3` : le loader 0.19.5 convient, le 0.20.1 reste inutile. `GSON` de la config avec `disableHtmlEscaping()` : sans cela, l'apostrophe du `_comment` s'écrit `'` dans un fichier fait pour être lu à la main (Smart Chest a le même défaut). `runServer` demande d'abord `run/eula.txt` à `eula=true` (accord de Jérôme du 24/10/2026, dossier `run/` non suivi). Le bruit de log hors-ligne du client (authlib 401, Realms, Perflib) n'a rien à voir avec le mod. |
