package com.darwinfish.client.renderer;

import com.darwinfish.entity.BabyCodEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.CodModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BabyCodRenderer extends MobRenderer<BabyCodEntity, CodModel<BabyCodEntity>> {

    private static final ResourceLocation TEXTURE =
        ResourceLocation.withDefaultNamespace("textures/entity/fish/cod.png");

    public BabyCodRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new CodModel<>(ctx.bakeLayer(ModelLayers.COD)), 0.2F);
    }

    @Override
    public ResourceLocation getTextureLocation(BabyCodEntity entity) {
        return TEXTURE;
    }

    @Override
    protected void scale(BabyCodEntity entity, PoseStack poseStack, float partialTickTime) {
        poseStack.scale(0.5F, 0.5F, 0.5F);
    }

    @Override
    protected void setupRotations(BabyCodEntity entity, PoseStack poseStack,
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
