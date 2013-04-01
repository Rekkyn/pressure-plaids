package rekkyn.pressureplaids;

import net.minecraft.block.Block;
import net.minecraft.block.EnumMobType;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkMod.SidedPacketHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;


@Mod(modid = PressurePlaids.modid, name = "Pressure Plaids", version = "-1.0")
@NetworkMod(
        clientSideRequired = true, serverSideRequired = false,
        clientPacketHandlerSpec = @SidedPacketHandler(
                channels = { "PressurePlaids" }, packetHandler = PlaidClientPacketHandler.class),
        serverPacketHandlerSpec = @SidedPacketHandler(
                channels = { "PressurePlaids" }, packetHandler = PlaidServerPacketHandler.class))

public class PressurePlaids {
    
    public static final String modid = "PressurePlaids";
    
    public static final Block pressurePlaid = new BlockPressurePlaid(500, EnumMobType.everything, Material.grass)
    .setHardness(1F).setStepSound(Block.soundStoneFootstep)
    .setUnlocalizedName("pressurePlaid").setCreativeTab(CreativeTabs.tabRedstone);

    
    
    @Instance("PressurePlaids")
    public static PressurePlaids instance;
    
    @SidedProxy(clientSide="rekkyn.pressureplaids.client.ClientProxy", serverSide="rekkyn.pressureplaids.CommonProxy")
    public static CommonProxy proxy;
    
    @PreInit
    public void preInit(FMLPreInitializationEvent event) {
    }
    
    @Init
    public void load(FMLInitializationEvent event) {
            proxy.registerRenderers();
            
            GameRegistry.registerBlock(pressurePlaid, "pressurePlaid");
            LanguageRegistry.addName(pressurePlaid, "Pressure Plaid!");

    }
    
    @PostInit
    public void postInit(FMLPostInitializationEvent event) {
    }

}
