package binaris.biome_loot_table.registry;

import binaris.biome_loot_table.config.BLT_Config;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static binaris.biome_loot_table.config.BLT_Config.CONFIG_MAP;
import static binaris.biome_loot_table.config.BLT_Config.DEFAULT_LIST;
import static net.minecraft.server.command.CommandManager.literal;
public final class BLT_Commands {
    public static void registerCommands() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(literal("chestloot")
            .then(literal("reload")
                    .executes(BLT_Commands::executeReload))
            .then(literal("info")
                .executes(BLT_Commands::executeInfo))

        ));
    }

    private static int executeReload(CommandContext<ServerCommandSource> context) {
        BLT_Config.readConfig(context.getSource().getServer().getLootManager());
        context.getSource().sendMessage(Text.literal("Se ha recargado la configuración de las loot tables").formatted(Formatting.GREEN));
        context.getSource().sendMessage(Text.literal("Revisar la consola para ver si hay errores").formatted(Formatting.GREEN));

        return 1;
    }

    private static int executeInfo(CommandContext<ServerCommandSource> context){
        ServerCommandSource source = context.getSource();
        source.sendMessage(Text.literal("BiomeLootTable version: 1.0.0 By Binaris").formatted(Formatting.GREEN));
        source.sendMessage(Text.literal("Total de archivos: %d".formatted(CONFIG_MAP.keySet().size())).formatted(Formatting.GREEN));
        source.sendMessage(Text.literal("Loot Tables default: %d".formatted(DEFAULT_LIST.size())).formatted(Formatting.GREEN));
        CONFIG_MAP.forEach((biome, lootTables) -> source.sendMessage(Text.literal("Biome type: %s -> Loot tables: %s".formatted(biome, lootTables.size())).formatted(Formatting.GREEN)));
        return 1;
    }
}
