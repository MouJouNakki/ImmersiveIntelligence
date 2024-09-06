package pl.pabilo8.immersiveintelligence.common.entity.hans.tasks;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;
import pl.pabilo8.immersiveintelligence.common.entity.EntityHans;

import java.util.Optional;

public class AIHansAttackAnything extends EntityAITarget {
    private final EntityHans hans;
    private EntityLivingBase targetEntity;
    public AIHansAttackAnything(EntityHans hans)
    {
        super(hans, false);
        this.hans = hans;
        this.setMutexBits(1);
    }
    @Override
    public boolean shouldExecute()
    {
        Optional<Entity> optional = hans.world.loadedEntityList.stream().filter(this.hans::isValidTarget).findAny();
        if (!optional.isPresent())
            return false;
        this.targetEntity = (EntityLivingBase)optional.get();
        return true;
    }
    public void startExecuting()
    {
        this.hans.setAttackTarget(this.targetEntity);
        super.startExecuting();
    }
    protected double getTargetDistance()
    {
        return 128.0;
    }
}
