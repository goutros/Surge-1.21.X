package net.galaxi.surge.screen.custom;

import net.galaxi.surge.Surge;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import com.mojang.blaze3d.vertex.PoseStack;

import java.util.List;

public class SkillOrbScreen extends AbstractContainerScreen<SkillOrbMenu> {

    private static final int CARD_WIDTH = 93;
    private static final int CARD_HEIGHT = 140;
    private static final int MAX_CARD_SPACING = 20;

    private static final ResourceLocation CARD_BASE = ResourceLocation.fromNamespaceAndPath(Surge.MOD_ID, "textures/skill_cards/skill_card_base.png");

    private int hoveredCard = -1;
    private float[] cardAnimations;
    private float[] idleOffsets;
    private long openTime;
    private int cardCount = 3;

    public SkillOrbScreen(SkillOrbMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = Math.min(this.width - 40, 600);
        this.imageHeight = 220;
        this.openTime = System.currentTimeMillis();
        this.cardAnimations = new float[cardCount];
        this.idleOffsets = new float[cardCount];
    }

    private int calculateSpacing() {
        int availableWidth = this.width - 80;
        int totalCardWidth = CARD_WIDTH * cardCount;
        int spacingWidth = availableWidth - totalCardWidth;
        int spacing = spacingWidth / Math.max(1, cardCount - 1);
        return Mth.clamp(spacing, -CARD_WIDTH + 10, MAX_CARD_SPACING);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        float time = (System.currentTimeMillis() - openTime) / 455f;
        int spacing = calculateSpacing();
        int totalWidth = CARD_WIDTH * cardCount + spacing * (cardCount - 1);
        int startX = (this.width - totalWidth) / 2;
        int startY = (this.height - CARD_HEIGHT) / 2;

        float basePopTime = 0.0f;
        float staggerPerCard = Math.max(0.05f, 0.055f / cardCount);

        for (int i = 0; i < cardCount; i++) {
            float targetAnim = hoveredCard == i ? 1.0f : 0.0f;
            cardAnimations[i] += (targetAnim - cardAnimations[i]) * 0.2f;

            float popProgress = Math.min(1.0f, (time - basePopTime - i * staggerPerCard) / 0.2f);
            float popUp = popProgress < 0 ? 0 : (1 - Mth.cos(popProgress * Mth.PI)) * 0.5f;

            idleOffsets[i] = Mth.sin(time * 2.0f + i * 2.0f) * 3.0f;

            int cardX = startX + i * (CARD_WIDTH + spacing);
            int cardY = startY;

            float hoverLift = cardAnimations[i] * -15.0f;
            float totalY = cardY + hoverLift + idleOffsets[i] + (1 - popUp) * 50;

            PoseStack pose = graphics.pose();
            pose.pushPose();

            float centerX = cardX + CARD_WIDTH / 2f;
            float centerY = totalY + CARD_HEIGHT / 2f;

            pose.translate(centerX, centerY, 100 + i * 50);

            if (hoveredCard == i) {
                float relativeX = Mth.clamp((mouseX - centerX) / (CARD_WIDTH / 2f), -1, 1);
                float relativeY = Mth.clamp((mouseY - centerY) / (CARD_HEIGHT / 2f), -1, 1);

                pose.mulPose(com.mojang.math.Axis.YP.rotationDegrees(relativeX * 15));
                pose.mulPose(com.mojang.math.Axis.XP.rotationDegrees(-relativeY * 15));

                float scale = 1.0f + cardAnimations[i] * 0.1f;
                pose.scale(scale, scale, 1.0f);
            }

            pose.translate(-CARD_WIDTH / 2f, -CARD_HEIGHT / 2f, 0);

            graphics.blit(CARD_BASE, 0, 0, 0, 0, CARD_WIDTH, CARD_HEIGHT, CARD_WIDTH, CARD_HEIGHT);

            pose.translate(0, 0, 0.1f);

            float scaleX = CARD_WIDTH / 32f;
            float scaleY = CARD_HEIGHT / 48f;

            graphics.drawString(this.font, "Skill Card", (int)(2 * scaleX), (int)(3 * scaleY), 0x000000, false);

            String desc = "Lorem ipsum dolor sit amet, consectetur adipiscing elit.";
            int maxWidth = (int)(27 * scaleX);
            int y = (int)(26.5 * scaleY);
            List<FormattedCharSequence> lines = this.font.split(Component.literal(desc), maxWidth);
            for (FormattedCharSequence line : lines) {
                graphics.drawString(this.font, line, (int)(2 * scaleX), y, 0x000000, false);
                y += 9;
            }

            pose.popPose();
        }
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);

        float time = (System.currentTimeMillis() - openTime) / 1000f;
        int spacing = calculateSpacing();
        int totalWidth = CARD_WIDTH * cardCount + spacing * (cardCount - 1);
        int startX = (this.width - totalWidth) / 2;
        int startY = (this.height - CARD_HEIGHT) / 2;

        float basePopTime = 0.3f;
        float staggerPerCard = Math.max(0.05f, 0.3f / cardCount);

        for (int i = 0; i < cardCount; i++) {
            idleOffsets[i] = Mth.sin(time * 2.0f + i * 2.0f) * 3.0f;
        }

        hoveredCard = -1;
        for (int i = cardCount - 1; i >= 0; i--) {
            float popProgress = Math.min(1.0f, (time - basePopTime - i * staggerPerCard) / 0.2f);
            float popUp = popProgress < 0 ? 0 : (1 - Mth.cos(popProgress * Mth.PI)) * 0.5f;

            int cardX = startX + i * (CARD_WIDTH + spacing);
            float hoverLift = cardAnimations[i] * -15.0f;
            float baseY = startY + hoverLift + idleOffsets[i] + (1 - popUp) * 50;

            int padding = 10;
            if (mouseX >= cardX - padding && mouseX <= cardX + CARD_WIDTH + padding &&
                    mouseY >= baseY - padding && mouseY <= baseY + CARD_HEIGHT + padding) {
                hoveredCard = i;
                break;
            }
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (hoveredCard != -1) {
            this.onClose();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
}