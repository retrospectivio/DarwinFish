package com.darwinfish.client.renderer;

import com.darwinfish.block.FishEggBlock;
import com.darwinfish.blockentity.FishEggBlockEntity;
import com.darwinfish.FishType;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FishEggBlockEntityRenderer implements BlockEntityRenderer<FishEggBlockEntity> {

    private static final ResourceLocation TEXTURE =
            ResourceLocation.fromNamespaceAndPath("darwinfish", "textures/block/tropical_fish_eggs.png");

    public FishEggBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {}

    @Override
    public void render(FishEggBlockEntity be, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight, int packedOverlay) {
        if (!(be.getBlockState().getBlock() instanceof FishEggBlock eggBlock)) return;
        if (eggBlock.getFishType() != FishType.TROPICAL_FISH) return;

        // 1. Получаем чистый генетический цвет (микс родителей)
        int geneticTint = be.hasColorData()
                ? be.getBaseColor().getTextureDiffuseColor()
                : 0xFFFFFF; // Белый по умолчанию

        float genR = ((geneticTint >> 16) & 0xFF) / 255f;
        float genG = ((geneticTint >> 8)  & 0xFF) / 255f;
        float genB = ( geneticTint        & 0xFF) / 255f;

        // 2. Задаем наш идеальный "бежевый" цвет основы (RGB от 0.0 до 1.0)
        // Сейчас настроен на приятный песочно-желтоватый оттенок
        float beigeR = 0.90f; // 230 / 255
        float beigeG = 0.82f; // 210 / 255
        float beigeB = 0.65f; // 165 / 255

        // 3. Сила генетического влияния (от 0.0 до 1.0)
        // 0.25f означает, что икра будет на 75% бежевой и на 25% цветной
        float geneticInfluence = 0.35f;

        // 4. Смешиваем! Высчитываем средневзвешенное значение
        float r = (genR * geneticInfluence) + (beigeR * (1f - geneticInfluence));
        float g = (genG * geneticInfluence) + (beigeG * (1f - geneticInfluence));
        float b = (genB * geneticInfluence) + (beigeB * (1f - geneticInfluence));

        poseStack.pushPose();
        poseStack.translate(0.5, 0.0, 0.5);

        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));

        // Габариты нашего 3D кубика (10x10 пикселей в ширину, 3 пикселя в высоту)
        float hw = 0.3125f;
        float hd = 0.3125f;
        float h_top = 0.1875f;
        float h_bot = 0.0f;

        // Верхняя грань
        addVertex(consumer, poseStack, -hw, h_top, -hd, 0f, 0f, r, g, b, packedLight, packedOverlay, 0f, 1f, 0f);
        addVertex(consumer, poseStack,  hw, h_top, -hd, 1f, 0f, r, g, b, packedLight, packedOverlay, 0f, 1f, 0f);
        addVertex(consumer, poseStack,  hw, h_top,  hd, 1f, 1f, r, g, b, packedLight, packedOverlay, 0f, 1f, 0f);
        addVertex(consumer, poseStack, -hw, h_top,  hd, 0f, 1f, r, g, b, packedLight, packedOverlay, 0f, 1f, 0f);

        // Северная грань
        addVertex(consumer, poseStack,  hw, h_top, -hd, 0f, 0f, r, g, b, packedLight, packedOverlay, 0f, 0f, -1f);
        addVertex(consumer, poseStack, -hw, h_top, -hd, 1f, 0f, r, g, b, packedLight, packedOverlay, 0f, 0f, -1f);
        addVertex(consumer, poseStack, -hw, h_bot, -hd, 1f, 1f, r, g, b, packedLight, packedOverlay, 0f, 0f, -1f);
        addVertex(consumer, poseStack,  hw, h_bot, -hd, 0f, 1f, r, g, b, packedLight, packedOverlay, 0f, 0f, -1f);

        // Южная грань
        addVertex(consumer, poseStack, -hw, h_top,  hd, 0f, 0f, r, g, b, packedLight, packedOverlay, 0f, 0f, 1f);
        addVertex(consumer, poseStack,  hw, h_top,  hd, 1f, 0f, r, g, b, packedLight, packedOverlay, 0f, 0f, 1f);
        addVertex(consumer, poseStack,  hw, h_bot,  hd, 1f, 1f, r, g, b, packedLight, packedOverlay, 0f, 0f, 1f);
        addVertex(consumer, poseStack, -hw, h_bot,  hd, 0f, 1f, r, g, b, packedLight, packedOverlay, 0f, 0f, 1f);

        // Восточная грань
        addVertex(consumer, poseStack,  hw, h_top,  hd, 0f, 0f, r, g, b, packedLight, packedOverlay, 1f, 0f, 0f);
        addVertex(consumer, poseStack,  hw, h_top, -hd, 1f, 0f, r, g, b, packedLight, packedOverlay, 1f, 0f, 0f);
        addVertex(consumer, poseStack,  hw, h_bot, -hd, 1f, 1f, r, g, b, packedLight, packedOverlay, 1f, 0f, 0f);
        addVertex(consumer, poseStack,  hw, h_bot,  hd, 0f, 1f, r, g, b, packedLight, packedOverlay, 1f, 0f, 0f);

        // Западная грань
        addVertex(consumer, poseStack, -hw, h_top, -hd, 0f, 0f, r, g, b, packedLight, packedOverlay, -1f, 0f, 0f);
        addVertex(consumer, poseStack, -hw, h_top,  hd, 1f, 0f, r, g, b, packedLight, packedOverlay, -1f, 0f, 0f);
        addVertex(consumer, poseStack, -hw, h_bot,  hd, 1f, 1f, r, g, b, packedLight, packedOverlay, -1f, 0f, 0f);
        addVertex(consumer, poseStack, -hw, h_bot, -hd, 0f, 1f, r, g, b, packedLight, packedOverlay, -1f, 0f, 0f);

        poseStack.popPose();
    }

    private void addVertex(VertexConsumer consumer, PoseStack poseStack,
                           float x, float y, float z,
                           float u, float v,
                           float r, float g, float b,
                           int packedLight, int packedOverlay,
                           float nx, float ny, float nz) {
        consumer.addVertex(poseStack.last().pose(), x, y, z)
                .setColor(r, g, b, 1f)
                .setUv(u, v)
                .setOverlay(packedOverlay)
                .setLight(packedLight)
                .setNormal(nx, ny, nz);
    }
}