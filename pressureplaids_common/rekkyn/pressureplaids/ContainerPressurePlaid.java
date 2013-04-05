package rekkyn.pressureplaids;
 
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
 
 
public class ContainerPressurePlaid extends Container{
        protected TilePressurePlaid tile_entity;
       
        public ContainerPressurePlaid(InventoryPlayer inventoryPlayer, TilePressurePlaid tileEntity){
                this.tile_entity = tileEntity;
                int o=0;
                for(int q = 0; q <3; q++){
                for(int p = 0; p <9; p++){
               
               
                addSlotToContainer(new Slot(tileEntity, o, 9+p*18, 9+q*18));
               
               
                bindPlayerInventory(inventoryPlayer);
                o++;
                }
        }}
       
        @Override
        public boolean canInteractWith(EntityPlayer player){
                return tile_entity.isUseableByPlayer(player);
        }
       
        protected void bindPlayerInventory(InventoryPlayer player_inventory){
                for(int i = 0; i < 3; i++){
                        for(int j = 0; j < 9; j++){
                        	addSlotToContainer(new Slot(player_inventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
                               
                        }
                }
       
                for(int i = 0; i < 9; i++){
                	addSlotToContainer(new Slot(player_inventory, i, 8 + i * 18, 142));
                }
               
        }
 
        @Override
        public ItemStack transferStackInSlot(EntityPlayer player, int slot_index) {
                ItemStack stack = null;
                Slot slot_object = (Slot) inventorySlots.get(slot_index);
               
                if(slot_object != null && slot_object.getHasStack()){
                        ItemStack stack_in_slot = slot_object.getStack();
                        stack = stack_in_slot.copy();
                       
                        if(slot_index == 0){
                                if(!mergeItemStack(stack_in_slot, 1, inventorySlots.size(), true)){
                                        return null;
                                }
                        }
                        else if(!mergeItemStack(stack_in_slot, 0, 1, false)){
                                return null;
                        }
               
                        if(stack_in_slot.stackSize == 0){
                                slot_object.putStack(null);
                        }
                        else{
                                slot_object.onSlotChanged();
                        }
                }
       
                return stack;
        }
}
