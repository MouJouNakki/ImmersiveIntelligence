package pl.pabilo8.immersiveintelligence.common.entity.hans.tasks;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import pl.pabilo8.immersiveintelligence.common.commands.ii.CommandIIHans;
import pl.pabilo8.immersiveintelligence.common.entity.EntityHans;

public class AIHansSummonReinforcements extends EntityAIBase
{
    private final EntityHans hans;
    private int timer = 20;
    public AIHansSummonReinforcements(EntityHans hans)
    {
        this.hans = hans;
        this.setMutexBits(3);
    }
    @Override
    public boolean shouldExecute()
    {
        if(!hans.onGround)
            return false;
        if(timer > 0)
        {
            timer--;
            return false;
        }
        int allyCount = 0;
        int enemyCount = 0;
        for(Entity entity : hans.world.loadedEntityList)
        {
            if(hans.isValidTarget(entity))
                enemyCount++;
            else if(entity instanceof EntityHans&&entity.getTeam() == hans.getTeam())
                allyCount++;
        }
        return Math.random() < enemyCount / Math.pow(allyCount, 3);
    }
    public void startExecuting()
    {
        CommandIIHans.spawnReinforcements(hans.world, hans.getPositionVector(), hans.getTeam());
        timer = 20;
    }
}
