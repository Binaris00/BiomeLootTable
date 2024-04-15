package binaris.biome_loot_table.mixin;

import binaris.biome_loot_table.BiomeLootTable;
import binaris.biome_loot_table.config.BLT_Config;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

/**
 * Mixin encargado en cambiar el comportamiento de como se genera el loot en los cofres
 * **/
@Mixin(ChestBlockEntity.class)
public abstract class ChestBlockEntityMixin extends LootableContainerBlockEntity {
    @Unique
    ChestBlockEntity blockEntity = (ChestBlockEntity)(Object)this;
    @Shadow private DefaultedList<ItemStack> inventory;
    @Shadow public abstract int size();

    protected ChestBlockEntityMixin(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    /**
     * Metodo para confirmar cuando y como generar el loot del cofre
     * para poder revisar a detalle como funciona la config en esta parte ver
     * {@link BLT_Config#getLootTable(BlockEntity)} y {@link BLT_Config#getLootTableId(LootTable)}
     * **/
    @Override
    public void checkLootInteraction(@Nullable PlayerEntity player) {
        if (lootTableSeed == 0 && blockEntity.getWorld().getServer() != null) {
            LootTable lootTable = BLT_Config.getLootTable(blockEntity);

            if (player instanceof ServerPlayerEntity) {
                Criteria.PLAYER_GENERATES_CONTAINER_LOOT.trigger((ServerPlayerEntity)player, BLT_Config.getLootTableId(lootTable));
            }

            LootContext.Builder builder = (new LootContext.Builder((ServerWorld)blockEntity.getWorld()))
                    .parameter(LootContextParameters.ORIGIN, Vec3d.ofCenter(blockEntity.getPos())).random(BiomeLootTable.random.nextLong());
            if (player != null) {
                builder.luck(player.getLuck()).parameter(LootContextParameters.THIS_ENTITY, player);
            }

            lootTableSeed = 1;
            lootTable.supplyInventory(blockEntity, builder.build(LootContextTypes.CHEST));
        }
    }

    // =================== NBT ===================
    // Si no lo entiendes, jamas lo toques ;;
    // ===========================================

    /**
     * Inicializar el inventario y el lootTableSeed
     *
     * @author Binaris
     * @reason no me pregunten porque, pero te marca una advertencia si no pones este comentario...
     */
    @Overwrite
    public void readNbt(NbtCompound nbt) {
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
        if(nbt.contains(LOOT_TABLE_SEED_KEY, NbtElement.LONG_TYPE)){
            lootTableSeed = nbt.getLong(LOOT_TABLE_SEED_KEY);
        }
        Inventories.readNbt(nbt, this.inventory);
    }
    /**
     * Poner el lootTableSeed en el NBT, además de guardar el inventario
     *
     * @author Binaris
     * @reason no me pregunten porque, pero te marca una advertencia si no pones este comentario...
     */
    @Overwrite
    public void writeNbt(NbtCompound nbt) {
        if(lootTableSeed != 0) {
            nbt.putLong(LOOT_TABLE_SEED_KEY, lootTableSeed);
        }
        Inventories.writeNbt(nbt, this.inventory);

    }
}
