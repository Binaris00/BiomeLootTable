package binaris.biome_loot_table.config;

import binaris.biome_loot_table.BiomeLootTable;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.loot.LootManager;
import net.minecraft.loot.LootTable;
import net.minecraft.util.Identifier;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import static binaris.biome_loot_table.BiomeLootTable.*;

@SuppressWarnings("unchecked")
public final class BLT_Config {
    // ================== Keys ==================
    public static final String BIOME_TYPE = "biome_type";
    public static final String LOOT_TABLES = "loot_tables";
    // ==========================================



    // ================== Files ==================
    /** Directorio de la config del mod */
    public static final File path = new File(FabricLoader.getInstance().getConfigDir().toFile(), MODID + "/");
    /** Archivo de la config del mod */
    public static final File config = new File(path, "config.json");
    /**Archivo para las loot table default**/
    public static final File defaultConfig = new File(path, "default.json");
    // ==========================================

    /**
     * Todos los datos de la configuracion del mod
     * usando el biome type como key y las loot tables como value
     * @see BLT_Config#readConfig(LootManager)
     * **/
    public static final HashMap<Identifier, ArrayList<LootTable>> CONFIG_MAP = new HashMap<>();
    /** Lista de loot tables default en caso de se abra un cofre
     * en un bioma que no está definido en {@link BLT_Config#CONFIG_MAP}
     * @see BLT_Config#readConfig(LootManager)
     * **/
    public static final ArrayList<LootTable> DEFAULT_LIST = new ArrayList<>();

    /**
     * Crea el archivo de configuracion si no existe
     * y escribe un json de ejemplo en el directorio {@link BLT_Config#path}
     *
     * <p>Usado en {@link BiomeLootTable#onInitializeServer()}
     * **/
    public static void createIfAbsent(){
        // Si no existe el directorio, lo crea
        if(!path.exists()) path.mkdirs();

        try {
            // Empezando a crear el json de ejemplo
            if(!config.exists()){
                config.createNewFile();

                // Escribiendo el contenido del json
                JSONObject json = new JSONObject();
                json.put(BIOME_TYPE, "minecraft:taiga");

                ArrayList<String> loot_tables = new ArrayList<>();
                loot_tables.add("minecraft:chests/village/village_weaponsmith");
                loot_tables.add("minecraft:chests/jungle_temple");
                loot_tables.add("minecraft:chests/abandoned_mineshaft");
                json.put(LOOT_TABLES, loot_tables);

                // Escribiendo el json en el archivo
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                FileWriter writer = new FileWriter(config);
                writer.write(gson.toJson(json));
                writer.close();
            }

            if(!defaultConfig.exists()){
                defaultConfig.createNewFile();
                JSONObject json = new JSONObject();

                ArrayList<String> loot_tables = new ArrayList<>();
                loot_tables.add("minecraft:chests/ancient_city");
                json.put(LOOT_TABLES, loot_tables);
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                FileWriter writer = new FileWriter(defaultConfig);
                writer.write(gson.toJson(json));
                writer.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Lee todos los archivos guardados en el directorio {@link BLT_Config#path}
     * y la guarda en {@link BLT_Config#CONFIG_MAP}
     *
     * <p>Usado en {@link binaris.biome_loot_table.mixin.MinecraftServerMixin MinecraftServerMixin} para obtener el Loot Manager
     *
     * @param lootManager el loot manager del servidor
     * **/
    public static void readConfig(LootManager lootManager){
        CONFIG_MAP.clear();
        DEFAULT_LIST.clear();

        // Obten todos los files que esten en el path
        File[] files = path.listFiles();
        if(files == null) throw new RuntimeException("No se encontró ningún archivo en la config del mod.");

        for(File file : files){
            // Si el archivo es un directorio, lo salta
            if(file.isDirectory()) {
                LOGGER.warn("Directorio invalido dentro de la config del mod cuando solo se esperan archivos JSON.");
                continue;
            }

            try {
                JSONObject json = (JSONObject) new JSONParser().parse(new FileReader(file));
                ArrayList<String> loot_tables = (ArrayList<String>) json.get(LOOT_TABLES);
                ArrayList<LootTable> lootTables = new ArrayList<>();

                if(file.getName().equals("default.json")){
                    for(String loot : loot_tables){
                        Identifier id = new Identifier(loot);
                        LootTable table = lootManager.getTable(id);
                        if(table == null) {
                            LOGGER.error("No se encontró la loot table con el id: %s".formatted(id));
                            continue;
                        }
                        DEFAULT_LIST.add(table);
                    }
                    continue;
                }

                String biome_type = (String) json.get(BIOME_TYPE);
                for(String loot : loot_tables){
                    Identifier id = new Identifier(loot);
                    LootTable table = lootManager.getTable(id);
                    if(table == null) {
                        LOGGER.error("No se encontró la loot table con el id: %s".formatted(id));
                        continue;
                    }
                    lootTables.add(table);
                }
                CONFIG_MAP.put(new Identifier(biome_type), lootTables);

            } catch (IOException | ParseException e) {
                throw new RuntimeException(e);
            }
        }



        // Logger
        LOGGER.info("=====================================");
        LOGGER.info("BiomeLootTable config cargada con éxito");
        LOGGER.info("En un total de %d archivos".formatted(CONFIG_MAP.keySet().size()));
        LOGGER.info("Loot Tables default: %d".formatted(DEFAULT_LIST.size()));
        LOGGER.info("Mas info: ");
        CONFIG_MAP.forEach((biome, lootTables) -> {
            LOGGER.info("Biome type: %s -> Loot tables: %s".formatted(biome, lootTables.size()));
        });
        LOGGER.info("=====================================");
    }

    // ================== Métodos auxiliares ==================
    /**
     * Obtiene una loot table aleatoria de la lista de loot tables
     * dependiendo del bioma en el que se encuentre el cofre,
     * si no se encuentra el bioma, se obtiene una loot table de la lista default
     *
     * @param entity el bloque que se está abriendo
     * @return una loot table aleatoria
     * **/
    public static LootTable getLootTable(BlockEntity entity){
        Identifier biome = entity.getWorld().getBiome(entity.getPos()).getKey().get().getRegistry();

        if(CONFIG_MAP.get(biome) != null){
            return CONFIG_MAP.get(biome).get(random.nextInt(CONFIG_MAP.get(biome).size()));
        } else {
            return DEFAULT_LIST.get(random.nextInt(DEFAULT_LIST.size()));
        }
    }
}
