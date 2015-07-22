package net.hiddendungeons.manager.base;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.artemis.Entity;
import com.artemis.Manager;

public class TagManager extends Manager {

	/** Tags mapped to entities. */
	private final Map<String, Integer> entitiesByTag;
	/** Tagged entities mapped to tags. */
	private final Map<Integer, String> tagsByEntity;
	
	/** Flyweight helper for entities. */
	private Entity flyweight;


	/**
	 * Creates a new TagManager.
	 */
	public TagManager() {
		entitiesByTag = new HashMap<String, Integer>();
		tagsByEntity = new HashMap<Integer, String>();
	}

	@Override
	protected void initialize() {
		flyweight = world.createEntity();
	}


	/**
	 * Tag an entity.
	 * <p>
	 * Each tag can only be given to one entity at a time.
	 * </p>
	 *
	 * @param tag
	 *			the tag
	 * @param entityId
	 *			the entity to get tagged
	 */
	public void register(String tag, int entityId) {
		entitiesByTag.put(tag, entityId);
		tagsByEntity.put(entityId, tag);
	}

	/**
	 * Remove a tag from the entity being tagged with it.
	 *
	 * @param tag
	 *			the tag to remove
	 */
	public void unregister(String tag) {
		tagsByEntity.remove(entitiesByTag.remove(tag));
	}

	/**
	 * Check if a tag is in use.
	 *
	 * @param tag
	 *			the tag to check
	 *
	 * @return {@code true} if the tag is in use
	 */
	public boolean isRegistered(String tag) {
		return entitiesByTag.containsKey(tag);
	}

	/**
	 * Get entity flyweight.
	 */
	public Entity getEntity(String tag) {
		flyweight.id = entitiesByTag.get(tag);
		return flyweight;
	}
	
	/**
	 * Get entity id.
	 */
	public int getEntityId(String tag) {
		return entitiesByTag.get(tag);
	}

	/**
	 * Get all used tags.
	 *
	 * @return all used tags as collection
	 */
	public Collection<String> getRegisteredTags() {
		return tagsByEntity.values();
	}

	/**
	 * If the entity gets deleted, remove the tag used by it.
	 *
	 * @param e
	 *			the deleted entity
	 */
	@Override
	public void deleted(Entity e) {
		String removedTag = tagsByEntity.remove(e);
		if(removedTag != null) {
			entitiesByTag.remove(removedTag);
		}
	}

}