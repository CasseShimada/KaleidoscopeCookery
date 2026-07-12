package com.github.ysbbbbbb.kaleidoscopecookery.client.particle;

import com.github.ysbbbbbb.kaleidoscopecookery.particle.StockpotParticleOptions;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SingleQuadParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.util.RandomSource;
import org.joml.Vector3fc;

public class StockpotParticle extends SingleQuadParticle {
    private final SpriteSet spriteSet;

    protected StockpotParticle(ClientLevel level, double posX, double posY, double posZ,
                               double xSpeed, double ySpeed, double zSpeed, SpriteSet spriteSet) {
        super(level, posX, posY, posZ, xSpeed, ySpeed, zSpeed, spriteSet.get(level.getRandom()));
        this.friction = 0.96F;
        this.spriteSet = spriteSet;
        this.scale(1.0F);
        this.hasPhysics = false;
        this.setSpriteFromAge(spriteSet);
    }

    @Override
    protected int getLightCoords(float partialTick) {
        return 240;
    }

    @Override
    public SingleQuadParticle.Layer getLayer() {
        return SingleQuadParticle.Layer.TRANSLUCENT;
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(this.spriteSet);
    }

    public record Provider(SpriteSet spriteSet) implements ParticleProvider<StockpotParticleOptions> {
        @Override
        public Particle createParticle(StockpotParticleOptions options, ClientLevel level,
                                       double posX, double posY, double posZ,
                                       double xSpeed, double ySpeed, double zSpeed, RandomSource random) {
            StockpotParticle particle = new StockpotParticle(level, posX, posY, posZ, xSpeed, ySpeed, zSpeed, this.spriteSet);
            Vector3fc color = options.getColor();
            float scale = options.getScale() - 0.1f + random.nextFloat() * 0.2f;
            particle.setAlpha(1);
            particle.setColor(color.x(), color.y(), color.z());
            particle.setSize(scale, scale);
            particle.setParticleSpeed(xSpeed, ySpeed, zSpeed);
            particle.setLifetime(random.nextInt(4) + 6);
            return particle;
        }
    }
}
