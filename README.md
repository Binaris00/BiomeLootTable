# Biomes Loot Table

Mod utilizado como prueba para Eufonia Studio.

Consiste en poder limitar las loot tables de los biomas, 
para que solo puedan aparecer ciertos items en ciertos biomas.

Google Forms -> [Se buscan desarrolladores!](https://docs.google.com/forms/d/e/1FAIpQLScLUBHk3OAtqUKscx4gYpxuR-Ffb6Ag-SfEV_ZojwWy6WdBvg/viewform)

Sitios para poder ver la información:
- [Loot Tables](https://mcreator.net/wiki/minecraft-vanilla-loot-tables-list)
- [Biomes](https://mcreator.net/wiki/minecraft-biome-list)

## Guia de uso
(Por cuestiones de diseño hice unos pequeños cambios de como se usaria el mod, pero lo esencial sigue siendo
lo mismo que se pedia en el enunciado)

El mod es solo necesario en server side, entonces a la hora de ponerlo en la respectiva carpeta de mods e iniciar el servidor por primera vez 
este creara una carpeta junto con dos archivos .json:

Uno para las loot tables que se generaran cuando no se especifique un bioma en la config:
```json
{
  "loot_tables": [
    "minecraft:chests/ancient_city"
  ]
}
```

Y otro archivo de ejemplo para cuando quieras especificar loot tables por bioma:
```json
{
  "loot_tables": [
    "minecraft:chests/village/village_weaponsmith",
    "minecraft:chests/jungle_temple",
    "minecraft:chests/abandoned_mineshaft"
  ],
  "biome_type": "minecraft:taiga"
}
```

### Para crear un archivo de loot table por bioma
1. Crear un archivo .json en la carpeta del mod llamado `biome_loot_tables` con el nombre del bioma que se quiera modificar o el de tu preferencia.
2. Dentro del archivo .json se debe de poner la siguiente estructura:
```json
{
  "loot_tables": [
    "inserta aqui las loot tables que quieras que aparezcan en este bioma"
  ],
  "biome_type": "bioma aqui"
}
```
3. Reiniciar el servidor o ejecuta el comando `/chestloot reload` para que los cambios se apliquen.