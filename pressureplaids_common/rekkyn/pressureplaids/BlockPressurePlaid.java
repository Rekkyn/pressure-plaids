package rekkyn.pressureplaids;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.EnumMobType;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPressurePlaid extends Block {
    /** The mob type that can trigger this pressure plate. */
    private EnumMobType triggerMobType;
    
    protected BlockPressurePlaid(int id, EnumMobType triggerMobType, Material material) {
        super(id, material);
        this.triggerMobType = triggerMobType;
        this.setCreativeTab(CreativeTabs.tabRedstone);
        this.setTickRandomly(true);
        float dim = 0.0625F;
        this.setBlockBounds(dim, 0.0F, dim, 1.0F - dim, 0.03125F, 1.0F - dim);
    }
    
    /**
     * How many world ticks before ticking
     */
    public int tickRate() {
        return 20;
    }
    
    /**
     * Returns a bounding box from the pool of bounding boxes (this means this
     * box can change after the pool has been cleared to be reused)
     */
    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return null;
    }
    
    /**
     * Is this block (a) opaque and (b) a full 1m cube? This determines whether
     * or not to render the shared face of two adjacent blocks and also whether
     * the player can attach torches, redstone wire, etc to this block.
     */
    @Override
    public boolean isOpaqueCube() {
        return false;
    }
    
    /**
     * If this block doesn't render as an ordinary block it will return False
     * (examples: signs, buttons, stairs, etc)
     */
    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }
    
    @Override
    public boolean getBlocksMovement(IBlockAccess block, int x, int y, int z) {
        return true;
    }
    
    /**
     * Checks to see if its valid to put this block at the specified
     * coordinates. Args: world, x, y, z
     */
    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        return world.doesBlockHaveSolidTopSurface(x, y - 1, z) || BlockFence.isIdAFence(world.getBlockId(x, y - 1, z));
    }
    
    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which
     * neighbor changed (coordinates passed are their own) Args: x, y, z,
     * neighbor blockID
     */
    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, int neighborID) {
        boolean unsupported = false;
        
        if (!world.doesBlockHaveSolidTopSurface(x, y - 1, z) && !BlockFence.isIdAFence(world.getBlockId(x, y - 1, z))) {
            unsupported = true;
        }
        
        if (unsupported) {
            this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
            world.setBlockToAir(x, y, z);
        }
    }
    
    /**
     * Ticks the block if it's been scheduled
     */
    @Override
    public void updateTick(World world, int x, int y, int z, Random rand) {
        if (!world.isRemote) {
            if (world.getBlockMetadata(x, y, z) != 0) {
                this.setStateIfMobInteractsWithPlate(world, x, y, z);
            }
        }
    }
    
    /**
     * Triggered whenever an entity collides with this block (enters into the
     * block). Args: world, x, y, z, entity
     */
    @Override
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
        if (!world.isRemote) {
            if (world.getBlockMetadata(x, y, z) != 1) {
                this.setStateIfMobInteractsWithPlate(world, x, y, z);
            }
        }
    }
    
    /**
     * Checks if there are mobs on the plate. If a mob is on the plate and it is
     * off, it turns it on, and vice versa.
     */
    private void setStateIfMobInteractsWithPlate(World world, int x, int y, int z) {
        boolean alreadyPressed = world.getBlockMetadata(x, y, z) == 1;
        boolean currentlyPressed = false;
        float i = 0.125F;
        List list = null;
        
        if (triggerMobType == EnumMobType.everything) {
            list = world.getEntitiesWithinAABBExcludingEntity((Entity) null, boundingBox(x, y, z));
        }
        
        if (triggerMobType == EnumMobType.mobs) {
            list = world.getEntitiesWithinAABB(EntityLiving.class, boundingBox(x, y, z));
        }
        
        if (triggerMobType == EnumMobType.players) {
            list = world.getEntitiesWithinAABB(EntityPlayer.class, boundingBox(x, y, z));
        }
        
        if (!list.isEmpty()) {
            Iterator entityList = list.iterator();
            
            while (entityList.hasNext()) {
                Entity selectedEntity = (Entity) entityList.next();
                
                if (!selectedEntity.doesEntityNotTriggerPressurePlate()) {
                    currentlyPressed = true;
                    break;
                }
            }
        }
        
        if (currentlyPressed && !alreadyPressed) {
            world.setBlockMetadataWithNotify(x, y, z, 1, 2);
            world.notifyBlocksOfNeighborChange(x, y, z, blockID);
            world.notifyBlocksOfNeighborChange(x, y - 1, z, blockID);
            world.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
            world.playSoundEffect(x + 0.5D, y + 0.1D, z + 0.5D, "random.click", 0.3F, 0.6F);
        }
        
        if (!currentlyPressed && alreadyPressed) {
            world.setBlockMetadataWithNotify(x, y, z, 0, 2);
            world.notifyBlocksOfNeighborChange(x, y, z, blockID);
            world.notifyBlocksOfNeighborChange(x, y - 1, z, blockID);
            world.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
            world.playSoundEffect(x + 0.5D, y + 0.1D, z + 0.5D, "random.click", 0.3F, 0.5F);
        }
        
        if (currentlyPressed) {
            world.scheduleBlockUpdate(x, y, z, blockID, this.tickRate());
        }
    }
    
    protected AxisAlignedBB boundingBox(int x, int y, int z) {
        float f = 0.125F;
        return AxisAlignedBB.getAABBPool().getAABB((x + f), y, (z + f), ((x + 1) - f), y + 0.25D, ((z + 1) - f));
    }
    
    /**
     * ejects contained items into the world, and notifies neighbours of an
     * update, as appropriate
     */
    @Override
    public void breakBlock(World world, int x, int y, int z, int i, int metadata) {
        if (metadata > 0) {
            world.notifyBlocksOfNeighborChange(x, y, z, blockID);
            world.notifyBlocksOfNeighborChange(x, y - 1, z, blockID);
        }
        
        // dropItems(par1World, par2, par3, par4);
        super.breakBlock(world, x, y, z, i, metadata);
    }
    
    /*
     * private void dropItems(World world, int x, int y, int z){ Random rand =
     * new Random();
     * 
     * TileEntity tile_entity = world.getBlockTileEntity(x, y, z);
     * 
     * if(!(tile_entity instanceof IInventory)){ return; }
     * 
     * IInventory inventory = (IInventory) tile_entity;
     * 
     * for(int i = 0; i < inventory.getSizeInventory(); i++){ ItemStack item =
     * inventory.getStackInSlot(i);
     * 
     * if(item != null && item.stackSize > 0){ float rx = rand.nextFloat() *
     * 0.6F + 0.1F; float ry = rand.nextFloat() * 0.6F + 0.1F; float rz =
     * rand.nextFloat() * 0.6F + 0.1F;
     * 
     * EntityItem entity_item = new EntityItem(world, x + rx, y + ry, z + rz,
     * new ItemStack(item.itemID, item.stackSize, item.getItemDamage()));
     * 
     * if(item.hasTagCompound()){
     * entity_item.func_92014_d().setTagCompound((NBTTagCompound)
     * item.getTagCompound().copy()); }
     * 
     * float factor = 0.5F;
     * 
     * entity_item.motionX = rand.nextGaussian() * factor; entity_item.motionY =
     * rand.nextGaussian() * factor + 0.2F; entity_item.motionZ =
     * rand.nextGaussian() * factor; world.spawnEntityInWorld(entity_item);
     * item.stackSize = 0; } } }
     */
    
    /**
     * Updates the blocks bounds based on its current state. Args: world, x, y,
     * z
     */
    /*
     * public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, int
     * par2, int par3, int par4) { boolean var5 =
     * par1IBlockAccess.getBlockMetadata(par2, par3, par4) == 1; float var6 =
     * 0.0625F;
     * 
     * if (var5) { this.setBlockBounds(var6, 0.0F, var6, 1.0F - var6, 0.03125F,
     * 1.0F - var6); } else { this.setBlockBounds(var6, 0.0F, var6, 1.0F - var6,
     * 0.0625F, 1.0F - var6); } }
     */
    
    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess block, int x, int y, int z) {
        boolean on = block.getBlockMetadata(x, y, z) == 1;
        float height = 0.0625F;
        if (on) {
            height = 0.03125F;
        }
        float dim1 = 0.0625F;
        float dim2 = 0.0625F;
        float dim3 = 0.9375F;
        float dim4 = 0.9375F;
        
        if (block.getBlockId(x, y, z - 1) == blockID) {
            dim2 = 0.0F;
        }
        if (block.getBlockId(x, y, z + 1) == blockID) {
            dim4 = 1.0F;
        }
        if (block.getBlockId(x - 1, y, z) == blockID) {
            dim1 = 0.0F;
        }
        if (block.getBlockId(x + 1, y, z) == blockID) {
            dim3 = 1.0F;
        }
        setBlockBounds(dim1, 0.0F, dim2, dim3, height, dim4);
    }
    
    /**
     * Returns true if the block is emitting indirect/weak redstone power on the
     * specified side. If isBlockNormalCube returns true, standard redstone
     * propagation rules will apply instead and this will not be called. Args:
     * World, X, Y, Z, side. Note that the side is reversed - eg it is 1 (up)
     * when checking the bottom of the block.
     */
    @Override
    public int isProvidingWeakPower(IBlockAccess block, int x, int y, int z, int side) {
        return block.getBlockMetadata(x, y, z) == 1 ? 15 : 0;
    }
    
    /**
     * Returns true if the block is emitting direct/strong redstone power on the
     * specified side. Args: World, X, Y, Z, side. Note that the side is
     * reversed - eg it is 1 (up) when checking the bottom of the block.
     */
    @Override
    public int isProvidingStrongPower(IBlockAccess block, int x, int y, int z, int side) {
        if (block.getBlockMetadata(x, y, z) == 1 && side == 1) {
            return 15;
        } else {
            return 0;
        }
        
    }
    
    /**
     * Can this block provide power. Only wire currently seems to have this
     * change based on its state.
     */
    @Override
    public boolean canProvidePower() {
        return true;
    }
    
    /**
     * Sets the block's bounds for rendering it as an item
     */
    @Override
    public void setBlockBoundsForItemRender() {
        float i = 0.5F;
        float j = 0.125F;
        float k = 0.5F;
        this.setBlockBounds(0.5F - i, 0.5F - j, 0.5F - k, 0.5F + i, 0.5F + j, 0.5F + k);
    }
    
    /**
     * Returns the mobility information of the block, 0 = free, 1 = can't push
     * but can move over, 2 = total immobility and stop pistons
     */
    @Override
    public int getMobilityFlag() {
        return 1;
    }
    
    /*
     * @Override public String getTextureFile () { return CommonProxy.BLOCK_PNG;
     * }
     */
    
    /*
     * @Override public boolean onBlockActivated(World world, int x, int y, int
     * z, EntityPlayer player, int i, float f, float g, float t){ TileEntity
     * tile_entity = world.getBlockTileEntity(x, y, z);
     * 
     * if(tile_entity == null || player.isSneaking()){ return false; }
     * 
     * player.openGui(PressurePlaids.instance, 0, world, x, y, z); return true;
     * }
     * 
     * @Override public TileEntity createNewTileEntity(World var1) { return new
     * TilePressurePlaid(); }
     */
    
}
