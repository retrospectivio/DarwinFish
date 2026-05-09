package com.darwinfish.client.renderer;

import com.darwinfish.entity.BabyTropicalFishEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.ColorableHierarchicalModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.TropicalFishModelA;
import net.minecraft.client.model.TropicalFishModelB;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.TropicalFish;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * Pattern overlay layer for baby tropical fish — copy of vanilla
 * {@code TropicalFishPatternLayer} but typed for {@link BabyTropicalFishEntity}.
 */
@OnlyIn(Dist.CLIENT)
public class BabyTropicalFishPatternLayer
    extends RenderLayer<BabyTropicalFishEntity, ColorableHierarchicalModel<BabyTropicalFishEntity>> {

    private static final ResourceLocation KOB       = rl("tropical_a_pattern_1");
    private static final ResourceLocation SUNSTREAK = rl("tropical_a_pattern_2");
    private static final ResourceLocation SNOOPER   = rl("tropical_a_pattern_3");
    private static final ResourceLocation DASHER    = rl("tropical_a_pattern_4");
    private static final ResourceLocation BRINELY   = rl("tropical_a_pattern_5");
    private static final ResourceLocation SPOTTY    = rl("tropical_a_pattern_6");
    private static final ResourceLocation FLOPPER   = rl("tropical_b_pattern_1");
    private static final ResourceLocation STRIPEY   = rl("tropical_b_pattern_2");
    private static final ResourceLocation GLITTER   = rl("tropical_b_pattern_3");
    private static final ResourceLocation BLOCKFISH = rl("tropical_b_pattern_4");
    private static final ResourceLocation BETTY     = rl("tropical_b_pattern_5");
    private static final ResourceLocation CLAYFISH  = rl("tropical_b_pattern_6");

    private final TropicalFishModelA<BabyTropicalFishEntity> modelA;
    private final TropicalFishModelB<BabyTropicalFishEntity> modelB;

    public BabyTropicalFishPatternLayer(
            RenderLayerParent<BabyTropicalFishEntity, ColorableHierarchicalModel<BabyTropicalFishEntity>> parent,
            EntityModelSet modelSet) {
        super(parent);
        this.modelA = new TropicalFishModelA<>(modelSet.bakeLayer(ModelLayers.TROPICAL_FISH_SMALL_PATTERN));
        this.modelB = new TropicalFishModelB<>(modelSet.bakeLayer(ModelLayers.TROPICAL_FISH_LARGE_PATTERN));
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight,
                       BabyTropicalFishEntity entity,
                       float limbSwing, float limbSwingAmount, float partialTicks,
                       float ageInTicks, float netHeadYaw, float headPitch) {

        TropicalFish.Pattern pattern = entity.getVariant();

        @SuppressWarnings("unchecked")
        EntityModel<BabyTropicalFishEntity> model = (EntityModel<BabyTropicalFishEntity>)
            (pattern.base() == TropicalFish.Base.SMALL ? modelA : modelB);

        ResourceLocation tex = switch (pattern) {
            case KOB       -> KOB;
            case SUNSTREAK -> SUNSTREAK;
            case SNOOPER   -> SNOOPER;
            case DASHER    -> DASHER;
            case BRINELY   -> BRINELY;
            case SPOTTY    -> SPOTTY;
            case FLOPPER   -> FLOPPER;
            case STRIPEY   -> STRIPEY;
            case GLITTER   -> GLITTER;
            case BLOCKFISH -> BLOCKFISH;
            case BETTY     -> BETTY;
            case CLAYFISH  -> CLAYFISH;
        };

        int patternColor = entity.getPatternColor().getTextureDiffuseColor() | 0xFF000000;
        coloredCutoutModelCopyLayerRender(
            this.getParentModel(), model, tex, poseStack, buffer, packedLight,
            entity, limbSwing, limbSwingAmount, ageInTicks,
            netHeadYaw, headPitch, partialTicks, patternColor);
    }

    private static ResourceLocation rl(String name) {
        return ResourceLocation.withDefaultNamespace("textures/entity/fish/" + name + ".png");
    }
}
