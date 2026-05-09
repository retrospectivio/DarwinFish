package com.darwinfish.client.renderer;

import com.darwinfish.entity.BabyPikeEntity;
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
public class BabyPikeRenderer extends MobRenderer<BabyPikeEntity, SalmonModel<BabyPikeEntity>> {

    // Указываем путь к текстуре твоего мода
    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("darwinfish", "textures/entity/fish/pike.png");

    public BabyPikeRenderer(EntityRendererProvider.Context ctx) {
        // Оставляем каркас лосося, так как форма тела у них одинаковая
        super(ctx, new SalmonModel<>(ctx.bakeLayer(ModelLayers.SALMON)), 0.2F);
    }

    @Override
    public ResourceLocation getTextureLocation(BabyPikeEntity entity) {
        return TEXTURE;
    }

    @Override
    protected void scale(BabyPikeEntity entity, PoseStack poseStack, float partialTickTime) {
        // Уменьшаем щуку в два раза (логика малька)
        poseStack.scale(0.5F, 0.5F, 0.5F);
    }

    @Override
    protected void setupRotations(BabyPikeEntity entity, PoseStack poseStack,
                                  float bob, float yBodyRot,
                                  float partialTick, float scale) {
        super.setupRotations(entity, poseStack, bob, yBodyRot, partialTick, scale);
        // Анимация барахтанья на суше
        float f = 4.3F * Mth.sin(0.6F * bob);
        poseStack.mulPose(Axis.YP.rotationDegrees(f));
        if (!entity.isInWater()) {
            poseStack.translate(0.1F, 0.1F, -0.1F);
            poseStack.mulPose(Axis.ZP.rotationDegrees(90.0F));
        }
    }
}