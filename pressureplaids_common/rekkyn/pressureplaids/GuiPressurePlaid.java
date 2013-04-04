package rekkyn.pressureplaids;
 
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.StatCollector;

import org.lwjgl.opengl.GL11;
 
 
public class GuiPressurePlaid extends GuiContainer{
        public GuiPressurePlaid(InventoryPlayer player_inventory, TilePressurePlaid tile_entity){
                super(new ContainerPressurePlaid(tile_entity, player_inventory));
        }
       
       
        @Override
        protected void drawGuiContainerForegroundLayer(int i, int j){
       
                fontRenderer.drawString("Pressure Plaid!", 8, 6, 4210752);
                fontRenderer.drawString(StatCollector.translateToLocal("container.inventory"), 6, ySize - 96 , 4210752);
        }
       
       
        @Override
        protected void drawGuiContainerBackgroundLayer(float f, int i, int j){
       
               
                GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
               
                this.mc.renderEngine.bindTexture("/rekkyn/pressureplaids/pressureplaidGUI.png");
               
                int x = (width - xSize) / 2;
               
                int y = (height - ySize) / 2;
               
                this.drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
        }
}
