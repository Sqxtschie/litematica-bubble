# Litematica Bubble

**Only see the part of your schematic that's right around you.**

Litematica Bubble is a small client-side addon for [Litematica](https://modrinth.com/mod/litematica) that adds a new render layer mode: **Bubble**. Instead of slicing the schematic into layers, it renders only the blocks within a sphere around you, and the sphere moves with you.

This is useful for big builds where the full schematic gets in the way: you see exactly what you have to place next to you, without a wall of ghost blocks in front of your face.

**Contents:** [Features](#features) · [How to use](#how-to-use) · [Controls](#controls) · [Installation](#installation) · [Compatibility & limitations](#compatibility--limitations) · [FAQ](#faq) · [Building](#building-from-source)

---

## Features

- 🫧 **Bubble render mode**: a new layer mode next to *All*, *Single Layer*, *All Above*, *All Below*, *Layer Range*, ...
- 🚶 **Follows you**: the bubble is centered on your eyes and moves with you as you walk or fly
- 📏 **Adjustable radius**: from 1 to 64 blocks (default 10), with Page Up / Page Down or typed into the render layer menu
- ❄️ **Freeze**: pin the bubble in place and walk around it to look at it from outside
- ↕️ **Height limits**: cut off the bubble above and/or below a Y level, like *All Below* / *All Above*, so you can work floor by floor
- 🧱 **Clean edges**: block faces on the surface of the bubble and at the height cuts are rendered, just like at a normal Litematica layer cut
- 🖐️ **Works with Litematica's tools**: Easy Place, pick block and schematic entity rendering only see what's inside the bubble
- 📊 **HUD info**: the Litematica info HUD shows the radius, height limits and frozen state, e.g. `Layer Mode: Bubble - r = 10, Y 60..80 (frozen)`
- 💾 **Saved settings**: radius and height limits are kept between game restarts
- 🌍 **English and German** translations
- 💻 **Client-side only**: works on any server, nothing to install there

---

## How to use

### 1. Switch to the Bubble mode
Load a schematic placement as usual. Then switch the render layer mode to **Bubble**, either:
- with **`M + Page Up`** / **`M + Page Down`** (cycles through Litematica's layer modes), or
- in Litematica's settings (**`M + C`**) → **Render Layers** tab → click the mode button until it says **Bubble**.

Now only the part of the schematic within 10 blocks of you is visible.

### 2. Change the size
- **`Page Up`** makes the bubble bigger, **`Page Down`** makes it smaller (1 block per press).
- Or type the radius directly into the text field in the render layer menu.

The new radius is shown in the action bar and saved automatically.

### 3. Freeze the bubble
Sometimes you want to step back and look at a section from outside. In the render layer menu (`M + C` → **Render Layers**), click **Freeze**. The bubble stays where it is while you move. Click **Unfreeze** to let it follow you again.

Tip: you can also bind Litematica's **`layerSetHere`** hotkey (Litematica settings → Hotkeys). In Bubble mode it toggles freezing, so you don't have to open the menu.

### 4. Limit the height (optional)
Building a multi-story house? Use height limits to hide everything above or below a certain floor:
- **`Shift + Page Up/Down`** moves the **upper** limit up or down.
- **`Ctrl + Page Up/Down`** moves the **lower** limit up or down.
- **`Shift + Ctrl + Page Up/Down`** removes both limits.

When a limit isn't set yet, it starts at your eye level, so pressing `Shift + Page Down` once puts the upper limit at your feet. The limits are absolute Y levels: they stay where they are while the bubble moves with you. The current value is shown in the action bar and in the HUD.

### 5. Back to normal
Switch to any other layer mode (e.g. *All*) with `M + Page Up/Down` and Litematica behaves exactly as before.

---

## Controls

| Keys | In Bubble mode |
|---|---|
| `M + Page Up` / `M + Page Down` | Switch layer mode (Litematica) |
| `Page Up` / `Page Down` | Radius +1 / −1 |
| `Shift + Page Up` / `Shift + Page Down` | Upper height limit +1 / −1 |
| `Ctrl + Page Up` / `Ctrl + Page Down` | Lower height limit +1 / −1 |
| `Shift + Ctrl + Page Up/Down` | Remove both height limits |
| `layerSetHere` hotkey (unbound by default) | Freeze / unfreeze |
| Render layer menu → **Freeze** | Freeze / unfreeze |

`Page Up` / `Page Down` are Litematica's `layerNext` / `layerPrevious` hotkeys, so if you rebound those, use your keys instead. The height limit combos only work in Bubble mode and don't interfere with anything outside of it.

---

## Installation

### Requirements
| | Version |
|---|---|
| Minecraft | 26.3 |
| [Fabric Loader](https://fabricmc.net/use/) | 0.16.0 or newer |
| [Fabric API](https://modrinth.com/mod/fabric-api) | any for 26.3 |
| [Litematica](https://modrinth.com/mod/litematica) | 0.29.1 or newer |
| [MaLiLib](https://modrinth.com/mod/malilib) | 0.30.2 or newer |

### With the Modrinth App (easiest)
1. Open your Fabric 26.3 instance.
2. Click **Add content**, search for **Litematica Bubble** and click **Install**.
3. Litematica, MaLiLib and Fabric API are installed automatically as dependencies.
4. Start the game.

### Manually
1. Install [Fabric Loader](https://fabricmc.net/use/) for Minecraft 26.3.
2. Download **Fabric API**, **MaLiLib**, **Litematica** and **Litematica Bubble** ([Releases](https://github.com/Sqxtschie/litematica-bubble/releases) or [Modrinth](https://modrinth.com/mod/litematica-bubble)).
3. Put all four `.jar` files into your `.minecraft/mods` folder.
4. Start Minecraft with the Fabric profile.

Only one version of Litematica Bubble may be in the mods folder at a time. When updating, delete the old jar first.

### Configuration file
The radius and height limits are stored in `config/litematica_bubble.txt`. You normally don't need to touch it; deleting it resets everything to the defaults.

---

## Compatibility & limitations

- **Client-side only.** Works on vanilla servers, Paper, Realms and singleplayer.
- **Easy Place, pick block and entities** respect the bubble: you can only place what you can see.
- **Material List and Schematic Verifier** use the bubble's bounding box along the selected axis, not the exact sphere. They still work, they just count a slightly larger area.
- **Frozen state isn't saved.** After a restart the bubble follows you again.
- Very large radii (40+) need more chunk rebuilds while moving, which can cost some FPS on slower PCs.

---

## FAQ

**The bubble doesn't show up / nothing changes.**
Make sure the layer mode is really set to *Bubble* (check the Litematica info HUD, toggle it with `I`) and that the schematic placement is rendered (`M + G`).

**Page Up changes the layer instead of the radius.**
You're not in Bubble mode. Switch with `M + Page Up/Down`.

**The game crashes on start saying a newer Fabric Loader is required.**
You're using version 1.0.1. Update to 1.0.2 or newer, which work with Fabric Loader 0.16.0+.

**Does it work with older Minecraft versions?**
Currently only 26.3 is supported.

**Can I change the height limit keys?**
Not yet. They are fixed to Shift/Ctrl + Page Up/Down. If you need different keys, open an issue on GitHub.

---

## Building from source

Requires Java 25.

```
./gradlew build
```

The jar ends up in `build/libs/`. `./gradlew runClient` starts a test client with Litematica and MaLiLib.

---

## License

Licensed under [LGPL-3.0](LICENSE), like Litematica and MaLiLib. This is an unofficial addon and isn't affiliated with masa.
