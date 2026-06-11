# Dynamic Lantern

Dynamic Lantern is a Forge 1.20.1 mod that makes Curios lanterns behave like a visible waist lantern while staying friendly to shader-pack held-item lighting.

Equip a vanilla lantern or soul lantern in a Curios belt slot and it will render on the player's waist with a subtle pendulum swing. When Iris or Oculus is installed, compatible shader packs can recognize the Curios lantern for held-item glow without adding a separate dynamic light source.

## Features

- Supports `minecraft:lantern` and `minecraft:soul_lantern` in Curios.
- Adds a Curios belt slot tag for vanilla lanterns.
- Renders the equipped lantern on the player's waist.
- Supports custom waist-rendered belt items configured by item ID.
- Renders block items as blocks and non-block items as item models.
- Supports left-side and back-side placement options.
- Adds configurable pendulum-style swinging physics.
- Lets Iris/Oculus shader held-item lighting use configured waist items that are already light-emitting blocks.
- Includes Epic Fight combat model compatibility.
- Does not add dynamic light sources.

## Configuration

The client config screen includes:

- Waist lantern rendering toggle
- Swing physics toggle
- Swing strength
- Left-side placement
- Back placement
- Curios lantern glow for shader-pack recognition
- Waist display item whitelist

## Requirements

- Minecraft 1.20.1
- Forge 47+
- Curios 5.x

## Optional Compatibility

- Iris or Oculus for shader-pack lighting.
- Epic Fight for combat model support.

## Version

Current version: `1.2.0`

### 1.2.0

- Added a configurable waist display item whitelist.
- Added runtime Curios belt support for configured item IDs.
- Added item-model rendering for non-block waist items.
- Restricted shader glow recognition to configured light-emitting block items.

### 1.1.0

- Improved left-side lantern placement.
- Updated README and project metadata.

## Author

spake404
