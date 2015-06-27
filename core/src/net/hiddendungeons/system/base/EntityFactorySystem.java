package net.hiddendungeons.system.base;

import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.utils.EntityBuilder;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalMaterial;
import com.badlogic.gdx.math.Vector3;

import net.hiddendungeons.component.base.Transform;
import net.hiddendungeons.component.object.Fireball;
import net.hiddendungeons.component.object.LeftHand;
import net.hiddendungeons.component.object.RightHand;
import net.hiddendungeons.component.render.DecalComponent;
import net.hiddendungeons.component.render.Renderable;
import net.hiddendungeons.component.render.SpriteComponent;
import net.hiddendungeons.system.view.render.RenderSystem;
import net.mostlyoriginal.api.system.core.PassiveSystem;

@Wire
public class EntityFactorySystem extends PassiveSystem {
	RenderSystem renderSystem;
	
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
	
	public void createLeftHand() {
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
	
	public void createRightHand() {
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
}
