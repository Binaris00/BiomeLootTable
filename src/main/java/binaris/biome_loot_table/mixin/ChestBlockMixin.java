package binaris.biome_loot_table.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChestBlock.class)
public abstract class ChestBlockMixin {
    /**
     * Este mixin es solo para confirmar si un jugador puso un cofre
     * y si no es creativo, si estas dos condiciones se cumplen, se
     * establece el cofre como inválido para que genere loot
     */
    @Inject(at = @At("HEAD"), method = "onPlaced")
    private void BLT$onPlace(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack, CallbackInfo ci){
        if(!world.isClient){
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if(blockEntity instanceof ChestBlockEntity chestBlockEntity && placer instanceof PlayerEntity player){
                if(!player.isCreative()) {
                    chestBlockEntity.setLootTable(null, 1);
                    chestBlockEntity.markDirty();
                }
            }
        }
    }
}
