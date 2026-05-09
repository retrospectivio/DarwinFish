package com.darwinfish.client.renderer;

import com.darwinfish.entity.BabyTropicalFishEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.ColorableHierarchicalModel;
import net.minecraft.client.model.TropicalFishModelA;
import net.minecraft.client.model.TropicalFishModelB;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.animal.TropicalFish;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BabyTropicalFishRenderer
    extends MobRenderer<BabyTropicalFishEntity, ColorableHierarchicalModel<BabyTropicalFishEntity>> {

    private static final ResourceLocation TEXTURE_A =
        ResourceLocation.withDefaultNamespace("textures/entity/fish/tropical_a.png");
    private static final ResourceLocation TEXTURE_B =
        ResourceLocation.withDefaultNamespace("textures/entity/fish/tropical_b.png");

    private final ColorableHierarchicalModel<BabyTropicalFishEntity> modelA;
    private final ColorableHierarchicalModel<BabyTropicalFishEntity> modelB;

    @SuppressWarnings("unchecked")
    public BabyTropicalFishRenderer(EntityRendererProvider.Context ctx) {
        super(ctx,
            new TropicalFishModelA<>(ctx.bakeLayer(ModelLayers.TROPICAL_FISH_SMALL)),
            0.1F);
        this.modelA = (ColorableHierarchicalModel<BabyTropicalFishEntity>) this.getModel();
        this.modelB = (ColorableHierarchicalModel<BabyTropicalFishEntity>)
            (Object) new TropicalFishModelB<BabyTropicalFishEntity>(ctx.bakeLayer(ModelLayers.TROPICAL_FISH_LARGE));

        // Our own typed pattern layer — no unsafe cross-type cast needed
        this.addLayer(new BabyTropicalFishPatternLayer(this, ctx.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(BabyTropicalFishEntity entity) {
        return entity.getVariant().base() == TropicalFish.Base.SMALL ? TEXTURE_A : TEXTURE_B;
    }

    @Override
    public void render(BabyTropicalFishEntity entity, float entityYaw, float partialTicks,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        ColorableHierarchicalModel<BabyTropicalFishEntity> active =
            entity.getVariant().base() == TropicalFish.Base.SMALL ? modelA : modelB;
        this.model = active;
        active.setColor(entity.getBaseColor().getTextureDiffuseColor() | 0xFF000000);
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
        active.setColor(-1);
    }

    @Override
    protected void scale(BabyTropicalFishEntity entity, PoseStack poseStack, float partialTickTime) {
        poseStack.scale(0.5F, 0.5F, 0.5F);
    }

    @Override
    protected void setupRotations(BabyTropicalFishEntity entity, PoseStack poseStack,
                                   float bob, float yBodyRot,
                                   float partialTick, float scale) {
        super.setupRotations(entity, poseStack, bob, yBodyRot, partialTick, scale);
        float f = 4.3F * Mth.sin(0.6F * bob);
        poseStack.mulPose(Axis.YP.rotationDegrees(f));
        if (!entity.isInWater()) {
            poseStack.translate(0.2F, 0.1F, 0.0F);
            poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
        }
    }
}
