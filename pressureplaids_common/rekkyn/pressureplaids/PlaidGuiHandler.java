package rekkyn.pressureplaids;
 
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;
 
public class PlaidGuiHandler implements IGuiHandler{
 
        @Override
        public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z){
       
                TileEntity tile_entity = world.getBlockTileEntity(x, y, z);
               
                if(tile_entity instanceof TilePressurePlaid){
               
                        return new ContainerPressurePlaid((TilePressurePlaid) tile_entity, player.inventory);
                }
               
               
                return null;
        }
       
       
        @Override
        public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z){
       
                TileEntity tile_entity = world.getBlockTileEntity(x, y, z);
               
               
                if(tile_entity instanceof TilePressurePlaid){
       
                        return new GuiPressurePlaid(player.inventory, (TilePressurePlaid) tile_entity);
                }
       
        return null;
        }
}
