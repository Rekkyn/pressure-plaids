package rekkyn.pressureplaids;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockFence;
import net.minecraft.block.EnumMobType;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPressurePlaid extends Block
{
    /** The mob type that can trigger this pressure plate. */
    private EnumMobType triggerMobType;

    protected BlockPressurePlaid(int id, EnumMobType triggerMobType, Material material)
    {
        super(id, material);
        this.triggerMobType = triggerMobType;
        this.setCreativeTab(CreativeTabs.tabRedstone);
        this.setTickRandomly(true);
        float var5 = 0.0625F;
        this.setBlockBounds(var5, 0.0F, var5, 1.0F - var5, 0.03125F, 1.0F - var5);
    }

    /**
     * How many world ticks before ticking
     */
    public int tickRate()
    {
        return 20;
    }

    /**
     * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
     * cleared to be reused)
     */
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4)
    {
        return null;
    }

    /**
     * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
     */
    public boolean isOpaqueCube()
    {
        return false;
    }

    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    public boolean getBlocksMovement(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
        return true;
    }

    /**
     * Checks to see if its valid to put this block at the specified coordinates. Args: world, x, y, z
     */
    public boolean canPlaceBlockAt(World world, int x, int y, int z)
    {
        return world.doesBlockHaveSolidTopSurface(x, y - 1, z) || BlockFence.isIdAFence(world.getBlockId(x, y - 1, z));
    }

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor blockID
     */
    public void onNeighborBlockChange(World world, int x, int y, int z, int neighborID)
    {
        boolean unsupported = false;

        if (!world.doesBlockHaveSolidTopSurface(x, y - 1, z) && !BlockFence.isIdAFence(world.getBlockId(x, y - 1, z)))
        {
            unsupported = true;
        }

        if (unsupported)
        {
            this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
            world.setBlockToAir(x, y, z);
        }
    }

    /**
     * Ticks the block if it's been scheduled
     */
    public void updateTick(World world, int x, int y, int z, Random rand)
    {
        if (!world.isRemote)
        {
            if (world.getBlockMetadata(x, y, z) != 0)
            {
                this.setStateIfMobInteractsWithPlate(world, x, y, z);
            }
        }
    }

    /**
     * Triggered whenever an entity collides with this block (enters into the block). Args: world, x, y, z, entity
     */
    public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity)
    {
        if (!world.isRemote)
        {
            if (world.getBlockMetadata(x, y, z) != 1)
            {
                this.setStateIfMobInteractsWithPlate(world, x, y, z);
            }
        }
    }

    /**
     * Checks if there are mobs on the plate. If a mob is on the plate and it is off, it turns it on, and vice versa.
     */
    private void setStateIfMobInteractsWithPlate(World world, int x, int y, int z)
    {
        boolean alreadyPressed = world.getBlockMetadata(x, y, z) == 1;
        boolean currentlyPressed = false;
        float i = 0.125F;
        List list = null;

        if (this.triggerMobType == EnumMobType.everything)
        {
            list = world.getEntitiesWithinAABBExcludingEntity((Entity)null, boundingBox(x, y, z));
        }

        if (this.triggerMobType == EnumMobType.mobs)
        {
            list = world.getEntitiesWithinAABB(EntityLiving.class, boundingBox(x, y, z));
        }

        if (this.triggerMobType == EnumMobType.players)
        {
            list = world.getEntitiesWithinAABB(EntityPlayer.class, boundingBox(x, y, z));
        }

        if (!list.isEmpty())
        {
            Iterator entityList = list.iterator();

            while (entityList.hasNext())
            {
                Entity selectedEntity = (Entity)entityList.next();

                if (!selectedEntity.doesEntityNotTriggerPressurePlate())
                {
                    currentlyPressed = true;
                    break;
                }
            }
        }

        if (currentlyPressed && !alreadyPressed)
        {
            world.setBlockMetadataWithNotify(x, y, z, 1, 2);
            world.notifyBlocksOfNeighborChange(x, y, z, this.blockID);
            world.notifyBlocksOfNeighborChange(x, y - 1, z, this.blockID);
            world.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
            world.playSoundEffect((double)x + 0.5D, (double)y + 0.1D, (double)z + 0.5D, "random.click", 0.3F, 0.6F);
        }

        if (!currentlyPressed && alreadyPressed)
        {
            world.setBlockMetadataWithNotify(x, y, z, 0, 2);
            world.notifyBlocksOfNeighborChange(x, y, z, this.blockID);
            world.notifyBlocksOfNeighborChange(x, y - 1, z, this.blockID);
            world.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
            world.playSoundEffect((double)x + 0.5D, (double)y + 0.1D, (double)z + 0.5D, "random.click", 0.3F, 0.5F);
        }

        if (currentlyPressed)
        {
            world.scheduleBlockUpdate(x, y, z, this.blockID, this.tickRate());
        }
    }
    
    protected AxisAlignedBB boundingBox(int x, int y, int z)
    {
        float f = 0.125F;
        return AxisAlignedBB.getAABBPool().getAABB((double)((float)x + f), (double)y, (double)((float)z + f), (double)((float)(x + 1) - f), (double)y + 0.25D, (double)((float)(z + 1) - f));
    }


    /**
     * ejects contained items into the world, and notifies neighbours of an update, as appropriate
     */
    public void breakBlock(World par1World, int par2, int par3, int par4, int par5, int par6)
    {
        if (par6 > 0)
        {
            par1World.notifyBlocksOfNeighborChange(par2, par3, par4, this.blockID);
            par1World.notifyBlocksOfNeighborChange(par2, par3 - 1, par4, this.blockID);
        }

//        dropItems(par1World, par2, par3, par4);
        super.breakBlock(par1World, par2, par3, par4, par5, par6);
    }
    
/*    private void dropItems(World world, int x, int y, int z){
        Random rand = new Random();
       
        TileEntity tile_entity = world.getBlockTileEntity(x, y, z);
       
        if(!(tile_entity instanceof IInventory)){
                return;
        }

        IInventory inventory = (IInventory) tile_entity;

        for(int i = 0; i < inventory.getSizeInventory(); i++){
                ItemStack item = inventory.getStackInSlot(i);
               
                if(item != null && item.stackSize > 0){
                float rx = rand.nextFloat() * 0.6F + 0.1F;
                float ry = rand.nextFloat() * 0.6F + 0.1F;
                float rz = rand.nextFloat() * 0.6F + 0.1F;
               
                EntityItem entity_item = new EntityItem(world, x + rx, y + ry, z + rz, new ItemStack(item.itemID, item.stackSize, item.getItemDamage()));
               
                if(item.hasTagCompound()){
                        entity_item.func_92014_d().setTagCompound((NBTTagCompound) item.getTagCompound().copy());
                }

                float factor = 0.5F;
               
                entity_item.motionX = rand.nextGaussian() * factor;
                entity_item.motionY = rand.nextGaussian() * factor + 0.2F;
                entity_item.motionZ = rand.nextGaussian() * factor;
                world.spawnEntityInWorld(entity_item);
                item.stackSize = 0;
                }
        }
}*/


    /**
     * Updates the blocks bounds based on its current state. Args: world, x, y, z
     */
/*    public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
        boolean var5 = par1IBlockAccess.getBlockMetadata(par2, par3, par4) == 1;
        float var6 = 0.0625F;

        if (var5)
        {
            this.setBlockBounds(var6, 0.0F, var6, 1.0F - var6, 0.03125F, 1.0F - var6);
        }
        else
        {
            this.setBlockBounds(var6, 0.0F, var6, 1.0F - var6, 0.0625F, 1.0F - var6);
        }
    }*/
    
    public void setBlockBoundsBasedOnState(IBlockAccess block, int x, int y, int z)
    {
        boolean on = block.getBlockMetadata(x, y, z) == 1;
        float height = 0.0625F;
        if (on)
        {
            height = 0.03125F;
        }
        float dim1 = 0.0625F;
        float dim2 = 0.0625F;
        float dim3 = 0.9375F;
        float dim4 = 0.9375F;

        if (block.getBlockId(x, y, z - 1) == this.blockID)
        {
            dim2 = 0.0F;
        }
        if (block.getBlockId(x, y, z + 1) == this.blockID)
        {
            dim4 = 1.0F;
        }
        if (block.getBlockId(x - 1, y, z) == this.blockID)
        {
            dim1 = 0.0F;
        }
        if (block.getBlockId(x + 1, y, z) == this.blockID)
        {
            dim3 = 1.0F;
        }
        setBlockBounds(dim1, 0.0F, dim2, dim3,height, dim4);
    }



    /**
     * Returns true if the block is emitting indirect/weak redstone power on the specified side. If isBlockNormalCube
     * returns true, standard redstone propagation rules will apply instead and this will not be called. Args: World, X,
     * Y, Z, side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
     */
    public int isProvidingWeakPower(IBlockAccess block, int x, int y, int z, int side)
    {
        return block.getBlockMetadata(x, y, z) == 1 ? 15 : 0;
    }

    /**
     * Returns true if the block is emitting direct/strong redstone power on the specified side. Args: World, X, Y, Z,
     * side. Note that the side is reversed - eg it is 1 (up) when checking the bottom of the block.
     */
    public int isProvidingStrongPower(IBlockAccess block, int x, int y, int z, int side)
    {
        if (block.getBlockMetadata(x, y, z) == 1 && side == 1){
            return 15;
        } else {
            return 0;
        }
        
    }

    /**
     * Can this block provide power. Only wire currently seems to have this change based on its state.
     */
    public boolean canProvidePower()
    {
        return true;
    }

    /**
     * Sets the block's bounds for rendering it as an item
     */
    public void setBlockBoundsForItemRender()
    {
        float var1 = 0.5F;
        float var2 = 0.125F;
        float var3 = 0.5F;
        this.setBlockBounds(0.5F - var1, 0.5F - var2, 0.5F - var3, 0.5F + var1, 0.5F + var2, 0.5F + var3);
    }

    /**
     * Returns the mobility information of the block, 0 = free, 1 = can't push but can move over, 2 = total immobility
     * and stop pistons
     */
    public int getMobilityFlag()
    {
        return 1;
    }
    
/*    @Override
    public String getTextureFile () {
            return CommonProxy.BLOCK_PNG;
    }*/
    
    
    
/*    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int i, float f, float g, float t){
            TileEntity tile_entity = world.getBlockTileEntity(x, y, z);
   
                    if(tile_entity == null || player.isSneaking()){
                    return false;
                    }
   
            player.openGui(PressurePlaids.instance, 0, world, x, y, z);
            return true;
            }

    @Override
    public TileEntity createNewTileEntity(World var1) {
        return new TilePressurePlaid();
    }*/

}
