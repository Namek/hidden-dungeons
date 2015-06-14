package net.mostlyoriginal.game.system;

import com.artemis.Entity;
import com.artemis.annotations.Wire;
import com.artemis.utils.EntityBuilder;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalMaterial;

import net.mostlyoriginal.api.system.core.PassiveSystem;
import net.mostlyoriginal.game.component.Fireball;
import net.mostlyoriginal.game.component.LeftHand;
import net.mostlyoriginal.game.component.RightHand;
import net.mostlyoriginal.game.component.logic.Position;
import net.mostlyoriginal.game.component.render.DecalComponent;
import net.mostlyoriginal.game.component.render.SpriteComponent;

@Wire
public class EntityFactory extends PassiveSystem {

	@Override
	protected void initialize() {
		createFireball();
	}
	
	public Entity createFireball() {
		Entity e = new EntityBuilder(world)
			.with(Fireball.class)
			.with(Position.class)
			.with(DecalComponent.class)
			.build();
		Position positionComponent = e.getComponent(Position.class);
		positionComponent.pos.set(0, 0, -2);
		Texture texture = new Texture("graphics/badlogic.jpg");
		Decal decal = e.getComponent(DecalComponent.class).decal;
		decal.setTextureRegion(new TextureRegion(texture));
		decal.setBlending(DecalMaterial.NO_BLEND, DecalMaterial.NO_BLEND);
		decal.setColor(1, 1, 1, 1);

		return e;
	}
	
	public void createPlayer() {
		Entity leftHand = new EntityBuilder(world)
			.with(Position.class)
			.with(SpriteComponent.class)
			.with(LeftHand.class)
			.build();
		Position leftHandPositionComponent = leftHand.getComponent(Position.class);
		leftHandPositionComponent.pos.x = 50.0f;
		leftHandPositionComponent.pos.y = 50.0f;
		
		Entity rightHand = new EntityBuilder(world)
			.with(Position.class)
			.with(SpriteComponent.class)
			.with(RightHand.class)
			.build();
		Position rightHandPositionComponent = rightHand.getComponent(Position.class);
		rightHandPositionComponent.pos.x = 100.0f;
		rightHandPositionComponent.pos.y = 100.0f;
	}
}
