package net.hiddendungeons.system;

import net.hiddendungeons.component.base.Dimensions;
import net.hiddendungeons.component.base.Transform;
import net.hiddendungeons.component.base.Velocity;
import net.hiddendungeons.component.logic.Player;
import net.hiddendungeons.component.object.Damage;
import net.hiddendungeons.component.object.Enemy;
import net.hiddendungeons.component.object.Fireball;
import net.hiddendungeons.component.object.LeftHand;
import net.hiddendungeons.component.object.RightHand;
import net.hiddendungeons.component.object.ViewFinder;
import net.hiddendungeons.component.render.DecalComponent;
import net.hiddendungeons.component.render.Renderable;
import net.hiddendungeons.component.render.SpriteComponent;
import net.hiddendungeons.enums.CollisionGroups;
import net.hiddendungeons.enums.Constants;
import net.hiddendungeons.enums.RenderLayers;
import net.hiddendungeons.enums.Tags;
import net.hiddendungeons.system.base.collision.Collider;
import net.hiddendungeons.system.logic.EnemySystem;
import net.hiddendungeons.system.view.render.RenderSystem;
import net.mostlyoriginal.api.system.core.PassiveSystem;

import com.artemis.Entity;
import com.artemis.EntityEdit;
import com.artemis.annotations.Wire;
import com.artemis.managers.TagManager;
import com.artemis.utils.EntityBuilder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
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
			.with(Damage.class)
			.build();
		
		entity.getComponent(Damage.class).dmg = Constants.Fireball.Dmg;
		entity.getComponent(Transform.class).desiredPos.set(start);
		float size = Constants.Fireball.MinRadius;
		entity.getComponent(DecalComponent.class).decal = createDecal("graphics/fireball.png", size, size);

		renderSystem.registerToDecalRenderer(entity);
	}

	public void createPlayer(Vector3 playerPos, Vector3 playerDir) {
		Entity entity = world.createEntity();
		EntityEdit edit = entity.edit();
		edit.add(new Player(1.5f, 1.0f));
		edit.create(Transform.class).xyz(playerPos).direction(playerDir);
		edit.create(Dimensions.class).set(1, 1, 1.5f);
		edit.create(Velocity.class).setup(Constants.Player.MaxSpeed, Constants.Player.Friction);
		edit.create(Collider.class).groups(CollisionGroups.PLAYER)
			.enterListener = world.getSystem(PlayerStateSystem.class);

		tagManager.register(Tags.PLAYER, entity);

		createLeftHand();
		createRightHand();
		createViewFinder();
	}

	private void createLeftHand() {
		Entity entity = world.createEntity();
		EntityEdit edit = entity.edit();
		edit.create(Transform.class);
		edit.create(LeftHand.class);
		edit.create(Renderable.class).layer(RenderLayers.HUD);
		edit.create(DecalComponent.class);
		edit.create(Damage.class).dmg = Constants.LeftHand.Dmg;
		edit.create(Collider.class).groups(CollisionGroups.SWORD)
			.enterListener = world.getSystem(EnemySystem.class);
		
		float size = Constants.Player.LeftHandSize;
		DecalComponent decalComponent = entity.getComponent(DecalComponent.class);
		Decal decal = decalComponent.decal = createDecal("graphics/hand_with_sword.png", size, size);
		edit.create(Dimensions.class).set(1f, 1f, 3f);

		renderSystem.registerToDecalRenderer(entity);
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
		rightHand.getComponent(Renderable.class)
			.layer(RenderLayers.HUD)
			.renderer(Renderable.SPRITE);

		renderSystem.registerToSpriteRenderer(rightHand);
	}

	public void createBaseEnemy(Vector3 position) {
		Entity entity = world.createEntity();
		EntityEdit edit = entity.edit();
		edit.add(new Enemy(Constants.Enemy.Hp));
		edit.add(new Damage(Constants.Enemy.Dmg));
		edit.create(DecalComponent.class);
		edit.create(Renderable.class).layer(RenderLayers.ENTITIES);
		edit.create(Velocity.class);
		Collider collider = edit.create(Collider.class).groups(CollisionGroups.ENEMY);
		collider.enterListener = world.getSystem(EnemySystem.class);
		collider.exitListener = world.getSystem(EnemySystem.class);
		
		float size = Constants.Enemy.Size;
		Decal decal = entity.getComponent(DecalComponent.class).decal = createDecal("graphics/monster_mouth.png", size, size);
		position.y = decal.getHeight() / 2f;
		edit.create(Transform.class).desiredPos.set(position);
		edit.create(Dimensions.class).set(decal.getWidth(), decal.getHeight(), Constants.Enemy.Depth);

		renderSystem.registerToDecalRenderer(entity);
	}

	public void createViewFinder() {
		Entity entity = world.createEntity();
		EntityEdit edit = entity.edit();
		edit.create(Transform.class);
		edit.create(ViewFinder.class);
		edit.create(Renderable.class).layer(RenderLayers.HUD);
		edit.create(DecalComponent.class);

		float size = Constants.Player.ViewFinderSize;
		Decal decal = entity.getComponent(DecalComponent.class).decal = createDecal("graphics/view_finder.jpg", size, size);

		tagManager.register(Tags.VIEW_FINDER, entity);

		renderSystem.registerToDecalRenderer(entity);
	}

	Decal createDecal(String texturePath, float width, float height) {
		Decal decal = new Decal();
		Texture texture = new Texture(texturePath);
		decal.setTextureRegion(new TextureRegion(texture));
		decal.setBlending(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		decal.setDimensions(width, height);
		decal.setColor(1, 1, 1, 1);

		return decal;
	}

}
