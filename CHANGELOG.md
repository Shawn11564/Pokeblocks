# Changelog

The **topmost `## ` section** of this file is posted to Discord, along with the Fabric, Forge and
NeoForge jars, by the `discord-build` GitHub Action on every push (see README → "Discord build
notifications"). Write the notes for the build you are about to push at the top, under a new
`## ` heading; older sections are kept for history and are not re-posted.

## 2.0.0. — JEI integration

- Added JEI support
- Fixed a dedicated-server crash when a doll was popped (it tried to read the doll's texture, which
  only the client has). On a dedicated server popped dolls now drop white and light gray wool.
- Trapped dolls can now be made with other mods' explosives, not just TNT: any modded TNT block works
  automatically, and more items can be added to the `pokeblocks:trap_explosives` item tag. A popped
  doll sets its explosive off as itself (that mod's own blast or effect); vanilla TNT keeps the usual
  blast, and an explosive that does nothing falls back to it. The doll's tooltip names its explosive.
