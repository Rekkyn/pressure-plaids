package rekkyn.pressureplaids;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkMod.SidedPacketHandler;
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


@Mod(modid = PressurePlaids.modid, name = "Pressure Plaids", version = "-1.0")
@NetworkMod(
        clientSideRequired = true, serverSideRequired = false,
        clientPacketHandlerSpec = @SidedPacketHandler(
                channels = { "PressurePlaids" }, packetHandler = PlaidClientPacketHandler.class),
        serverPacketHandlerSpec = @SidedPacketHandler(
                channels = { "PressurePlaids" }, packetHandler = PlaidServerPacketHandler.class))

public class PressurePlaids {
    
    public static final String modid = "PressurePlaids";
    
    
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
    }
    
    @PostInit
    public void postInit(FMLPostInitializationEvent event) {
    }

}
