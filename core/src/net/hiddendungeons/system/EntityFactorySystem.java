package net.hiddendungeons.system;

import net.hiddendungeons.component.base.Dimensions;
import net.hiddendungeons.component.base.Transform;
import net.hiddendungeons.component.base.Velocity;
import net.hiddendungeons.component.logic.Player;
import net.hiddendungeons.component.object.Enemy;
import net.hiddendungeons.component.object.Fireball;
import net.hiddendungeons.component.object.LeftHand;
import net.hiddendungeons.component.object.RightHand;
import net.hiddendungeons.component.render.DecalComponent;
import net.hiddendungeons.component.render.Renderable;
import net.hiddendungeons.component.render.SpriteComponent;
import net.hiddendungeons.enums.CollisionGroups;
import net.hiddendungeons.enums.Constants;
import net.hiddendungeons.enums.Tags;
import net.hiddendungeons.manager.base.TagManager;
import net.hiddendungeons.system.base.collision.Collider;
import net.hiddendungeons.system.view.render.RenderSystem;
import net.mostlyoriginal.api.system.core.PassiveSystem;

import com.artemis.Entity;
import com.artemis.EntityEdit;
import com.artemis.annotations.Wire;
import com.artemis.utils.EntityBuilder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalMaterial;
import com.badlogic.gdx.math.Vector3;

@Wire
public class EntityFactorySystem extends PassiveSystem {
	RenderSystem renderSystem;
	TagManager tagManager;


	public void createFireball(Vector3 start) {
		Entity entity = new EntityBuilder(world)
			.with(Fireball.class)
			.with(Transform.class)
			.with(DecalComponent.class)
			.with(Renderable.class)
			.build();
		entity.getComponent(Transform.class).desiredPos.set(start);
		Texture texture = new Texture("graphics/fireball.png");
		Decal decal = entity.getComponent(DecalComponent.class).decal = new Decal();
		decal.setTextureRegion(new TextureRegion(texture));
		decal.setBlending(DecalMaterial.NO_BLEND, DecalMaterial.NO_BLEND);
		decal.setColor(1, 1, 1, 1);
		
		renderSystem.registerToDecalRenderer(entity);
	}

	public void createPlayer(Vector3 playerPos, Vector3 playerDir) {
		Entity entity = world.createEntity();
		EntityEdit edit = entity.edit();
		edit.add(new Player(1.5f));
		edit.create(Transform.class).xyz(playerPos).rotation.set(playerDir);
		edit.create(Dimensions.class).set(1, 1, 1.5f);
		edit.create(Velocity.class).setup(Constants.Player.MaxSpeed, Constants.Player.Friction);
		edit.create(Collider.class).groups(CollisionGroups.PLAYER_MONSTERS);
		tagManager.register(Tags.PLAYER, entity.id);
		
		createLeftHand();
		createRightHand();
	}

	private void createLeftHand() {
		Entity leftHand = new EntityBuilder(world)
			.with(SpriteComponent.class)
			.with(LeftHand.class)
			.with(Renderable.class)
			.build();
		Texture texture = new Texture("graphics/hand_with_sword.png");
		Sprite sprite = leftHand.getComponent(SpriteComponent.class).sprite = new Sprite(texture);
		sprite.setTexture(texture);
		
		renderSystem.registerToSpriteRenderer(leftHand);
	}
	
	private void createRightHand() {
		Entity rightHand = new EntityBuilder(world)
			.with(SpriteComponent.class)
			.with(RightHand.class)
			.with(Renderable.class)
			.build();
		Texture texture = new Texture("graphics/hand.png");
		Sprite sprite = rightHand.getComponent(SpriteComponent.class).sprite = new Sprite(texture);
		sprite.setX(Gdx.graphics.getWidth() - sprite.getWidth());
		
		renderSystem.registerToSpriteRenderer(rightHand);
	}
	
	public void createBaseEnemy(Vector3 position) {
		int collisionGroup = 1; // todo from enum
		Entity entity = new EntityBuilder(world)
			.with(Transform.class)
			.with(Enemy.class)
			.with(DecalComponent.class)
			.with(Renderable.class)
			.with(Collider.class)
			.with(Dimensions.class)
			.build();
		entity.getComponent(Dimensions.class).set(10, 10, 1);
		entity.getComponent(Collider.class).groups = collisionGroup;
		entity.getComponent(Transform.class).desiredPos.set(position);
		Texture texture = new Texture("graphics/monster_mouth.png");
		Decal decal = entity.getComponent(DecalComponent.class).decal = new Decal();
		decal.setDimensions(3f, 2f);
		decal.setTextureRegion(new TextureRegion(texture));
		decal.setBlending(DecalMaterial.NO_BLEND, DecalMaterial.NO_BLEND);
		decal.setColor(1, 1, 1, 1);
		
		Collider collider = entity.getComponent(Collider.class);
		//collider.groups(collisionGroup).enterListener = world.getSystem(PlayerStateSystem.class); todo move to player component
		renderSystem.registerToDecalRenderer(entity);
	}

}
