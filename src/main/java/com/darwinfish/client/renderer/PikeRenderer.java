package com.darwinfish.client.renderer;

import com.darwinfish.entity.PikeEntity;
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
public class PikeRenderer extends MobRenderer<PikeEntity, SalmonModel<PikeEntity>> {

    // Ссылка на твою текстуру
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("darwinfish", "textures/entity/fish/pike.png");

    public PikeRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new SalmonModel<>(ctx.bakeLayer(ModelLayers.SALMON)), 0.4F);
    }

    @Override
    public ResourceLocation getTextureLocation(PikeEntity entity) {
        return TEXTURE;
    }

    @Override
    protected void setupRotations(PikeEntity entity, PoseStack poseStack,
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