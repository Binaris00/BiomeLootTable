package binaris.biome_loot_table.mixin;

import binaris.biome_loot_table.BiomeLootTable;
import binaris.biome_loot_table.config.BLT_Config;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(LootableContainerBlockEntity.class)
public class LootableContainerBlockEntityMixin {
    @Unique
    LootableContainerBlockEntity blockEntity = (LootableContainerBlockEntity)(Object)this;

    @Shadow @Nullable protected Identifier lootTableId;

    /**
     * @author
     * @reason
     */
    @Overwrite
    public void checkLootInteraction(@Nullable PlayerEntity player) {
        if (this.lootTableId != null && blockEntity.getWorld().getServer() != null) {
            LootTable lootTable = BLT_Config.getLootTable(blockEntity);

            if (player instanceof ServerPlayerEntity) {
                Criteria.PLAYER_GENERATES_CONTAINER_LOOT.trigger((ServerPlayerEntity)player, this.lootTableId);
            }

            LootContext.Builder builder = (new LootContext.Builder((ServerWorld)blockEntity.getWorld()))
                    .parameter(LootContextParameters.ORIGIN, Vec3d.ofCenter(blockEntity.getPos())).random(BiomeLootTable.random.nextLong());
            if (player != null) {
                builder.luck(player.getLuck()).parameter(LootContextParameters.THIS_ENTITY, player);
                BiomeLootTable.LOGGER.warn("Check");
            }

            this.lootTableId = null;
            lootTable.supplyInventory(blockEntity, builder.build(LootContextTypes.CHEST));
        }

        if(lootTableId == null){
            BiomeLootTable.LOGGER.warn("LootTableId is null");
        }
    }
    /**
     * @author
     * @reason
     */
    @Overwrite
    public boolean deserializeLootTable(NbtCompound nbt) {
        return true;
    }
    /**
     * @author
     * @reason
     */
    @Overwrite
    public boolean serializeLootTable(NbtCompound nbt) {
        return true;
    }
}
