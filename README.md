# Litematica Bubble

An addon for [Litematica](https://modrinth.com/mod/litematica) that adds a **Bubble** render layer mode:
only the part of the schematic within a sphere around you is rendered, and the sphere moves with you.

## Features

- New layer mode **Bubble** next to All, Single Layer, All Above, ... (render layer menu or `M + Page Up/Down`)
- **Page Up / Page Down** changes the radius (default 10, range 1–64); it can also be typed in the render layer menu
- **Freeze**: the "Freeze" button in the render layer menu (or Litematica's `layerSetHere` hotkey) keeps the bubble in place so you can walk around it
- **Height limits**, like All Below / All Above: `Shift + Page Up/Down` moves the upper limit, `Ctrl + Page Up/Down` the lower limit, `Shift + Ctrl + Page Up/Down` removes both
- Easy Place, pick block and entity rendering respect the bubble
- The radius and height limits are saved in `config/litematica_bubble.txt`

## Requirements

- Minecraft 26.3
- Fabric Loader and Fabric API
- Litematica 0.29.1+ and MaLiLib 0.30.2+

## Installation

Download the jar from [Releases](https://github.com/Sqxtschie/litematica-bubble/releases) and put it into your `mods` folder next to Litematica and MaLiLib.

## Building

```
./gradlew build
```

The jar ends up in `build/libs/`.

## License

[LGPL-3.0](LICENSE), like Litematica and MaLiLib.
