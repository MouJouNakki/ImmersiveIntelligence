package pl.pabilo8.immersiveintelligence.client.util.tmt;

import net.minecraft.util.math.Vec3d;

public class PositionTextureVertex extends net.minecraft.client.model.PositionTextureVertex
{
	public float texturePositionW;

	public PositionTextureVertex(float par1, float par2, float par3, float par4, float par5)
	{
		this(par1, par2, par3, par4, par5, 1F);
	}

	public PositionTextureVertex(float par1, float par2, float par3, float par4, float par5, float par6)
	{
		this(new Vec3d(par1, par2, par3), par4, par5);
	}

	@Override
	public PositionTextureVertex setTexturePosition(float par1, float par2)
	{
		return new PositionTextureVertex(this, par1, par2, 1F);
	}

	public PositionTextureVertex setTexturePosition(float par1, float par2, float q)
	{
		return new PositionTextureVertex(this, par1, par2, q);
	}

	public PositionTextureVertex(PositionTextureVertex par1PositionTextureVertex, float par2, float par3)
	{
		this(par1PositionTextureVertex, par2, par3, 1F);
	}

	public PositionTextureVertex(PositionTextureVertex par1PositionTextureVertex, float par2, float par3, float q)
	{
		super(par1PositionTextureVertex, par2, par3);
		this.texturePositionW = q;
	}

	public PositionTextureVertex(Vec3d par1Vec3, float par2, float par3)
	{
		this(par1Vec3, par2, par3, 1F);
	}

	public PositionTextureVertex(Vec3d par1Vec3, float par2, float par3, float par4)
	{
		super(par1Vec3, par2, par3);
		this.texturePositionW = par4;
	}
}
