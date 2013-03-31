package rekkyn.pressureplaids.client;

import net.minecraftforge.client.MinecraftForgeClient;
import rekkyn.pressureplaids.CommonProxy;

public class ClientProxy extends CommonProxy {
	
    @Override
    public void registerRenderers() {
            MinecraftForgeClient.preloadTexture(BLOCK_PNG);
    }


}
