package binaris.biome_loot_table;

import binaris.biome_loot_table.config.BLT_Config;
import binaris.biome_loot_table.registry.BLT_Commands;
import net.fabricmc.api.DedicatedServerModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class BiomeLootTable implements DedicatedServerModInitializer {

	/** Nombre registrado del mod */
	public static final String MOD_NAME = "Biome Loot Table";
	/** ID del mod */
	public static final String MODID = "biome_loot_table";
	/** Logger del mod */
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    public static final Random random = new Random();

	@Override
	public void onInitializeServer() {
		LOGGER.info("Ejecutando %s!".formatted(MOD_NAME));
		LOGGER.info("Mod creado como prueba para Eufonia");

		BLT_Commands.registerCommands();
		BLT_Config.createIfAbsent();

	}
}