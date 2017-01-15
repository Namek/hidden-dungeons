package net.hiddendungeons.system;

import com.artemis.Archetype;
import com.artemis.ArchetypeBuilder;
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

import net.hiddendungeons.component.base.Dimensions;
import net.hiddendungeons.component.base.Transform;
import net.hiddendungeons.component.base.Velocity;
import net.hiddendungeons.component.logic.Player;
import net.hiddendungeons.component.object.Damage;
import net.hiddendungeons.component.object.Enemy;
import net.hiddendungeons.component.object.EnergyBall;
import net.hiddendungeons.component.object.Growable;
import net.hiddendungeons.component.object.MagicHand;
import net.hiddendungeons.component.object.ViewFinder;
import net.hiddendungeons.component.object.WeaponHand;
import net.hiddendungeons.component.render.DecalComponent;
import net.hiddendungeons.component.render.Renderable;
import net.hiddendungeons.component.render.SpriteComponent;
import net.hiddendungeons.enums.CollisionGroups;
import net.hiddendungeons.enums.Constants;
import net.hiddendungeons.enums.Constants.MagicHand.MagicType;
import net.hiddendungeons.enums.RenderLayers;
import net.hiddendungeons.enums.Tags;
import net.hiddendungeons.system.base.collision.Collider;
import net.hiddendungeons.system.logic.EnemySystem;
import net.hiddendungeons.system.view.render.RenderSystem;
import net.mostlyoriginal.api.plugin.extendedcomponentmapper.M;
import net.mostlyoriginal.api.system.core.PassiveSystem;

@Wire
public class EntityFactorySystem extends PassiveSystem {
	M<Damage> mDamage;
	M<DecalComponent> mDecal;
	M<Renderable> mRenderable;
	M<Transform> mTransform;
	M<EnergyBall> mEnergyBall;

	RenderSystem renderSystem;
	TagManager tags;


	Archetype energyBallArchetype;


	@Override
	protected void initialize() {
		energyBallArchetype = new ArchetypeBuilder()
			.add(EnergyBall.class)
			.add(DecalComponent.class)
			.add(Transform.class)
			.add(Renderable.class)
			.add(Damage.class)
			.add(Growable.class)
			.build(world);
	}

	public Entity createEnergyBall(MagicType type) {
		assert(type != null);

		Entity entity = world.createEntity(energyBallArchetype);

		mEnergyBall.get(entity).type = type;
		mRenderable.get(entity).renderer(Renderable.DECAL);
		mDamage.get(entity).dmg = Constants.Fireball.Dmg;
		float size = Constants.Fireball.PulseMaxRadius;
		mDecal.get(entity).decal = createDecal("graphics/fireball.png", size, size);

		return entity;
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

		tags.register(Tags.Player, entity);

		createWeaponHand();
		createMagicHand();
		createViewFinder();
	}

	private void createWeaponHand() {
		Entity hand = world.createEntity();
		EntityEdit edit = hand.edit();
		edit.create(Transform.class);
		edit.create(WeaponHand.class);
		edit.create(Renderable.class).renderer(Renderable.DECAL).layer(RenderLayers.HUD);
		edit.create(DecalComponent.class);
		edit.create(Damage.class).dmg = Constants.WeaponHand.Dmg;
		edit.create(Collider.class).groups(CollisionGroups.SWORD)
			.enterListener = world.getSystem(EnemySystem.class);

		float size = Constants.Player.LeftHandSize;
		DecalComponent decalComponent = hand.getComponent(DecalComponent.class);
		Decal decal = decalComponent.decal = createDecal("graphics/hand_with_sword.png", size, size);
		decalComponent.lookAtCamera = false;
		edit.create(Dimensions.class).set(1f, 1f, 3f);

		tags.register(Tags.WeaponHand, hand);
	}

	private void createMagicHand() {
		Entity e = new EntityBuilder(world)
			.with(SpriteComponent.class)
			.with(MagicHand.class)
			.with(Renderable.class)
			.build();
		Texture texture = new Texture("graphics/hand.png");
		Sprite sprite = e.getComponent(SpriteComponent.class).sprite = new Sprite(texture);
		sprite.setX(Gdx.graphics.getWidth() - sprite.getWidth());
		e.getComponent(Renderable.class)
			.layer(RenderLayers.HUD)
			.renderer(Renderable.SPRITE);
		e.getComponent(MagicHand.class).magicType = MagicType.Fire;

		tags.register(Tags.MagicHand, e);
	}

	public void createBasicEnemy(Vector3 position) {
		Entity entity = world.createEntity();
		EntityEdit edit = entity.edit();
		edit.add(new Enemy(Constants.Enemy.Hp));
		edit.add(new Damage(Constants.Enemy.Dmg));
		edit.create(DecalComponent.class);
		edit.create(Renderable.class).renderer(Renderable.DECAL).layer(RenderLayers.ENTITIES);
		edit.create(Velocity.class);
		Collider collider = edit.create(Collider.class).groups(CollisionGroups.ENEMY);
		collider.enterListener = world.getSystem(EnemySystem.class);
		collider.exitListener = world.getSystem(EnemySystem.class);

		float size = Constants.Enemy.Size;
		Decal decal = entity.getComponent(DecalComponent.class).decal = createDecal("graphics/monster_mouth.png", size, size);
		position.y = decal.getHeight() / 2f;
		edit.create(Transform.class).desiredPos.set(position);
		edit.create(Dimensions.class).set(decal.getWidth(), decal.getHeight(), Constants.Enemy.Depth);
	}

	public void createViewFinder() {
		Entity entity = world.createEntity();
		EntityEdit edit = entity.edit();
		edit.create(Transform.class);
		edit.create(ViewFinder.class);
		edit.create(Renderable.class).renderer(Renderable.DECAL).layer(RenderLayers.HUD);
		edit.create(DecalComponent.class);

		float size = Constants.Player.ViewFinderSize;
		Decal decal = entity.getComponent(DecalComponent.class).decal = createDecal("graphics/view_finder.jpg", size, size);

		tags.register(Tags.VIEW_FINDER, entity);
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
