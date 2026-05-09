package com.darwinfish.client.renderer;

import com.darwinfish.entity.BabySalmonEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.SalmonModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BabySalmonRenderer extends MobRenderer<BabySalmonEntity, SalmonModel<BabySalmonEntity>> {

    private static final ResourceLocation TEXTURE =
        ResourceLocation.withDefaultNamespace("textures/entity/fish/salmon.png");

    public BabySalmonRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new SalmonModel<>(ctx.bakeLayer(ModelLayers.SALMON)), 0.2F);
    }

    @Override
    public ResourceLocation getTextureLocation(BabySalmonEntity entity) {
        return TEXTURE;
    }

    @Override
    protected void scale(BabySalmonEntity entity, PoseStack poseStack, float partialTickTime) {
        poseStack.scale(0.5F, 0.5F, 0.5F);
    }

    @Override
    protected void setupRotations(BabySalmonEntity entity, PoseStack poseStack,
                                   float bob, float yBodyRot,
                                   float partialTick, float scale) {
        super.setupRotations(entity, poseStack, bob, yBodyRot, partialTick, scale);
        float f = 4.3F * Mth.sin(0.6F * bob);
        poseStack.mulPose(Axis.YP.rotationDegrees(f));
        if (!entity.isInWater()) {
            poseStack.translate(0.1F, 0.1F, -0.1F);
            poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
        }
    }
}
