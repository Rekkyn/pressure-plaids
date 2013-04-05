package rekkyn.pressureplaids;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class CommonProxy implements IGuiHandler {
    public void registerRenderInformation() {
    }
    
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        
        if (ID == 0) {
            TilePressurePlaid tilePressurePlaid = (TilePressurePlaid) world.getBlockTileEntity(x, y, z);
            return new ContainerPressurePlaid(player.inventory, tilePressurePlaid);
        }
        
        return null;
    }
    
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        if (ID == 0) {
            TilePressurePlaid tilePressurePlaid = (TilePressurePlaid) world.getBlockTileEntity(x, y, z);
            return new GuiPressurePlaid(player.inventory, tilePressurePlaid);
        }
        return null;
    }
    
    public void registerTiles() {
        
    }
    
    public void registerBlocks() {
        
    }
    
    public void addNames() {
        
    }
    
    public void addRecipes() {
        
    }
}