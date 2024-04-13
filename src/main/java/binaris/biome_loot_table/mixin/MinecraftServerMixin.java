package binaris.biome_loot_table.mixin;

import binaris.biome_loot_table.config.BLT_Config;
import com.mojang.datafixers.DataFixer;
import net.minecraft.loot.LootManager;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.SaveLoader;
import net.minecraft.server.WorldGenerationProgressListenerFactory;
import net.minecraft.util.ApiServices;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.Proxy;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {

    @Shadow public abstract LootManager getLootManager();

    @Inject(method = "<init>", at = @At(
            value = "TAIL",
            target = "Lnet/minecraft/server/SaveLoader;combinedDynamicRegistries()Lnet/minecraft/registry/CombinedDynamicRegistries;"))
    private void BLT$onServerStart(Thread serverThread, LevelStorage.Session session, ResourcePackManager dataPackManager, SaveLoader saveLoader, Proxy proxy, DataFixer dataFixer, ApiServices apiServices, WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory, CallbackInfo ci) {
        BLT_Config.readConfig(getLootManager());
    }


}
