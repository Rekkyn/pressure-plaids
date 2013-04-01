package rekkyn.pressureplaids;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkMod.SidedPacketHandler;

@Mod(modid = "PressurePlaids", name = "Pressure Plaids", version = "-1.0")
@NetworkMod(
        clientSideRequired = true, serverSideRequired = false)
/*        clientPacketHandlerSpec = @SidedPacketHandler(
                channels = { "PressurePlaids" }, packetHandler = PlaidClientPacketHandler.class),
        serverPacketHandlerSpec = @SidedPacketHandler(
                channels = { "PressurePlaids" }, packetHandler = PlaidServerPacketHandler.class))
*/
public class PressurePlaids {
    
}
