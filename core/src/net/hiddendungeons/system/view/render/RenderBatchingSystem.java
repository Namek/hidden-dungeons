package net.hiddendungeons.system.view.render;

/**
 * @author Daan van Yperen
 * @author Namek
 */

import net.hiddendungeons.component.render.Renderable;
import net.mostlyoriginal.api.utils.BagUtils;

import com.artemis.BaseSystem;
import com.artemis.ComponentMapper;
import com.artemis.Entity;
import com.artemis.utils.Bag;

/**
 * Extensible rendering system.
 * <p/>
 * Create specialized rendering systems with DeferredEntityProcessingSystems.
 * RenderBatchingSystem will take care of overarching concerns like grouping
 * and sorting, while the specialist systems take care of the actual rendering.
 * <p/>
 * Currently only supports one specialist handling system per entity.
 *
 * @author Daan van Yperen
 * @see net.mostlyoriginal.api.component.graphics.Anim
 */
public class RenderBatchingSystem extends BaseSystem {

    protected ComponentMapper<Renderable> mRenderable;

    Entity flyweight;
    protected final Bag<Job> sortedJobs = new Bag<>();
    public boolean sortedDirty = false;


    @Override
	protected void initialize() {
		flyweight = Entity.createFlyweight(world);
	}

	/**
       * Declare entity relevant for agent.
       *
       * After this is called, the principal can use the agent
       * interface to begin/endLeft and process the given entity.
       *
       * @param e entity to process
       * @param agent interface to dispatch with.
       */
    public void registerAgent(int entityId, EntityProcessAgent agent) {
    	flyweight.id = entityId;
        if (!mRenderable.has(flyweight)) {
        	throw new RuntimeException("RenderBatchingSystem requires agents entities to have component Renderable.");
        }

        // register new job. this will influence sorting order.
        sortedJobs.add(new Job(entityId, agent));
        sortedDirty = true;
    }
    
    public void registerAgent(Entity entity, EntityProcessAgent agent) {
    	registerAgent(entity.id, agent);
    }

    /**
     * Revoke relevancy of entity for agent.
     *
     * After this is called, the principal should no longer
     * attempt to process the entity with the agent.

     * @param e entity to process
     * @param agent interface to dispatch with.
     */
    public void unregisterAgent(Entity e, EntityProcessAgent agent) {
        // forget about the job.
        final Object[] data = sortedJobs.getData();
        for (int i = 0, s = sortedJobs.size(); i < s; i++) {
            final Job e2 = (Job) data[i];
            if (e2.entityId == e.id && e2.agent == agent) {
                sortedJobs.remove(i);
                sortedDirty=true;
                break;
            }
        }
    }

	@Override
	protected void processSystem() {
        if (sortedDirty) {
            // sort our jobs (by layer).
            sortedDirty = false;
            BagUtils.sort(sortedJobs);
        }

        // iterate through all the jobs.
        // @todo add support for entities being deleted.
        EntityProcessAgent activeAgent = null;
        int activeAgentType = Integer.MIN_VALUE;
        final Object[] data = sortedJobs.getData();
        for (int i=0, s=sortedJobs.size(); i<s; i++)
        {
            final Job job = (Job)data[i];
            final EntityProcessAgent agent = job.agent;
            int agentType = agent.getType();

            // agent changed? endLeft() the last agent, and begin() the next agent.
            // @todo extend this with eventual texture/viewport/etc demarcation.
            if (agentType != activeAgentType) {
                if (activeAgent != null) {
                    activeAgent.end();
                }
                activeAgent = agent;
                activeAgentType = agentType;
                activeAgent.begin();
            }

            // process the entity!
            flyweight.id = job.entityId;
            processByAgent(agent, flyweight);
        }

        // finished, terminate final agent.
        if ( activeAgent != null )
        {
            activeAgent.end();
        }
    }
	
	/**
	 * Can be overriden for debug purposes.
	 */
	protected void processByAgent(EntityProcessAgent agent, Entity entity) {
		agent.process(entity);
	}

    @Override
    protected boolean checkProcessing() {
        return true;
    }

    /** Rendering job wrapper. */
    public class Job implements Comparable<Job> {
        public final int entityId;
        public final EntityProcessAgent agent;

        /**
         * @param entity entity we will process
         * @param agent agent responsible for processing.
         */
        public Job(final int entityId, final EntityProcessAgent agent) {
            this.entityId = entityId;
            this.agent = agent;
        }

        @Override
        public int compareTo(Job o) {
            return mRenderable.get(this.entityId).layer - mRenderable.get(o.entityId).layer;
        }
    }

    /**
     * Delegated processing is achieved by implementing
     * the EntityProcessAgent interface.
     *
     * @author Daan van Yperen
     * @author Namek
     */
    public static interface EntityProcessAgent {

        /** Prepare to receive a set of entities. */
        public void begin();

        /** Done receiving entities. */
        public void end();

        /** Process the entity. */
        public void process(Entity entity);

        /** Gets agent type to differ from other agents. */
        public int getType();

    }
}
