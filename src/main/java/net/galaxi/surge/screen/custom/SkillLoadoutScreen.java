package net.galaxi.surge.screen.custom;

import com.mojang.blaze3d.vertex.PoseStack;
import net.galaxi.surge.skill.ModAttachments;
import net.galaxi.surge.skill.PlayerSkillData;
import net.galaxi.surge.skill.Skill;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public class SkillLoadoutScreen extends AbstractContainerScreen<SkillLoadoutMenu> {
    private static final int CARD_WIDTH = 93;
    private static final int CARD_HEIGHT = 140;
    private static final int MAX_CARD_SPACING = 20;

    private int hoveredCard = -1;
    private int draggedCard = -1;
    private int targetSlot = -1;
    private double dragStartX = 0;
    private double dragStartY = 0;
    private double dragCurrentX = 0;
    private double dragCurrentY = 0;
    private double dragLastX = 0;
    private double dragLastY = 0;
    private double dragOffsetX = 0;
    private double dragOffsetY = 0;
    private double dragRotation = 0;
    private float[] cardAnimations;
    private float[] cardShiftAnimations;
    private float[] cardPositionX; // Current animated X position
    private float[] cardTargetX; // Target X position
    private float[] idleOffsets;
    private long openTime;
    private List<String> skillOrder = new ArrayList<>();
    private List<String> displayOrder = new ArrayList<>();

    public SkillLoadoutScreen(SkillLoadoutMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, Component.literal("Skill Loadout"));
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.openTime = System.currentTimeMillis();
    }

    @Override
    protected void init() {
        super.init();
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            PlayerSkillData skillData = player.getData(ModAttachments.PLAYER_SKILLS);
            this.skillOrder = new ArrayList<>(skillData.getSkillIds());
            this.displayOrder = new ArrayList<>(this.skillOrder);
            int skillCount = this.skillOrder.size();
            this.cardAnimations = new float[skillCount];
            this.cardShiftAnimations = new float[skillCount];
            this.cardPositionX = new float[skillCount];
            this.cardTargetX = new float[skillCount];
            this.idleOffsets = new float[skillCount];

            // Initialize positions
            int baseSpacing = calculateSpacing(skillCount);
            int currentX = 0;
            for (int i = 0; i < skillCount; i++) {
                cardPositionX[i] = currentX;
                cardTargetX[i] = currentX;
                currentX += CARD_WIDTH + baseSpacing;
            }

            // Initialize unique idle offsets based on original index
            for (int i = 0; i < skillCount; i++) {
                idleOffsets[i] = (float) (Math.sin(i * 0.7) * 3.0f);
            }
        }
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        // Dark background
        graphics.fill(0, 0, this.width, this.height, 0xAA000000);

        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        if (skillOrder.isEmpty()) {
            // Display "No Skills" message
            Component noSkills = Component.literal("No Skills Acquired");
            int textWidth = this.font.width(noSkills);
            graphics.drawString(this.font, noSkills, (this.width - textWidth) / 2, this.height / 2, 0xFFFFFF);
            return;
        }

        renderSkillCards(graphics, mouseX, mouseY);
    }

    private void renderSkillCards(GuiGraphics graphics, int mouseX, int mouseY) {
        int cardCount = displayOrder.size();
        float time = (System.currentTimeMillis() - openTime) / 1000f;

        // Update target slot if dragging
        if (draggedCard != -1) {
            int newTargetSlot = getCardIndexAtPosition(dragCurrentX);
            if (newTargetSlot != targetSlot && newTargetSlot != -1) {
                targetSlot = newTargetSlot;
                updateDisplayOrder();
            }

            // Update physics-based rotation
            double deltaX = dragCurrentX - dragLastX;
            double deltaY = dragCurrentY - dragLastY;
            double targetRotation = Math.atan2(deltaY, deltaX) * 5.0;
            dragRotation += (targetRotation - dragRotation) * 0.2;
            dragRotation = Mth.clamp(dragRotation, -15, 15);
        }

        // Calculate spacing - add extra gap when dragging
        int baseSpacing = calculateSpacing(cardCount);
        int gapSize = draggedCard != -1 ? CARD_WIDTH + 30 : 0; // Extra space for the gap

        // Calculate TARGET positions with gap
        int currentX = 0;
        for (int i = 0; i < cardCount; i++) {
            cardTargetX[i] = currentX;
            currentX += CARD_WIDTH + baseSpacing;

            // Add gap space at target slot
            if (draggedCard != -1 && i == targetSlot) {
                currentX += gapSize;
            }
        }

        // Smoothly interpolate current positions toward target positions
        for (int i = 0; i < cardCount; i++) {
            String skillId = displayOrder.get(i);
            int originalIndex = skillOrder.indexOf(skillId);

            // Skip the dragged card for position animation
            if (originalIndex == draggedCard) continue;

            // Smooth interpolation with acceleration/deceleration
            float diff = cardTargetX[i] - cardPositionX[i];
            cardPositionX[i] += diff * 0.25f; // Smooth easing factor
        }

        int totalWidth = currentX - baseSpacing;
        int startX = (this.width - totalWidth) / 2;
        int startY = (this.height - CARD_HEIGHT) / 2;

        // Update idle animations - keep them time-based but unique per original index
        for (int i = 0; i < cardCount; i++) {
            float baseOffset = (float) Math.sin(i * 0.7) * 3.0f;
            float timeOffset = Mth.sin(time * 1.5f + i * 1.3f) * 2.0f;
            idleOffsets[i] = baseOffset + timeOffset;
        }

        // Update shift animations
        for (int i = 0; i < cardCount; i++) {
            float targetShift = (draggedCard != -1 && i >= targetSlot) ? 1f : 0f;
            cardShiftAnimations[i] += (targetShift - cardShiftAnimations[i]) * 0.25f;
        }

        // Check hover (skip dragged card)
        hoveredCard = -1;
        if (draggedCard == -1) {
            for (int i = cardCount - 1; i >= 0; i--) {
                int cardX = startX + (int) cardPositionX[i];
                float baseY = startY + idleOffsets[i];

                int padding = 10;
                if (mouseX >= cardX - padding && mouseX <= cardX + CARD_WIDTH + padding &&
                        mouseY >= baseY - padding && mouseY <= baseY + CARD_HEIGHT + padding) {
                    hoveredCard = i;
                    break;
                }
            }
        }

        // Render non-dragged cards first
        for (int i = 0; i < cardCount; i++) {
            String skillId = displayOrder.get(i);
            int originalIndex = skillOrder.indexOf(skillId);

            if (originalIndex == draggedCard) continue;

            Skill skill = net.galaxi.surge.skill.SkillRegistry.get(skillId);
            if (skill == null) continue;

            float targetAnim = hoveredCard == originalIndex ? 1.0f : 0.0f;
            cardAnimations[originalIndex] += (targetAnim - cardAnimations[originalIndex]) * 0.2f;

            int cardX = startX + (int) cardPositionX[i];
            float hoverLift = cardAnimations[originalIndex] * -15.0f;
            float totalY = startY + hoverLift + idleOffsets[originalIndex];

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

            // Render card texture
            graphics.blit(skill.getTexture(), 0, 0, 0, 0, CARD_WIDTH, CARD_HEIGHT, CARD_WIDTH, CARD_HEIGHT);

            pose.translate(0, 0, 0.1f);

            // Render text
            float scaleX = CARD_WIDTH / 32f;
            float scaleY = CARD_HEIGHT / 48f;

            graphics.drawString(this.font, skill.getName(), (int)(2 * scaleX), (int)(3 * scaleY), skill.getRarity().getColor(), false);

            int maxWidth = (int)(27 * scaleX);
            int y = (int)(26.5 * scaleY);
            List<FormattedCharSequence> lines = this.font.split(skill.getDescription(), maxWidth);
            for (FormattedCharSequence line : lines) {
                graphics.drawString(this.font, line, (int)(2 * scaleX), y, 0x000000, false);
                y += 9;
            }

            pose.popPose();
        }

        // Render dragged card on top
        if (draggedCard != -1) {
            String skillId = skillOrder.get(draggedCard);
            Skill skill = net.galaxi.surge.skill.SkillRegistry.get(skillId);
            if (skill != null) {
                PoseStack pose = graphics.pose();
                pose.pushPose();

                float centerX = (float) (dragCurrentX - dragOffsetX);
                float centerY = (float) (dragCurrentY - dragOffsetY);

                pose.translate(centerX, centerY, 500);

                // Apply physics-based rotation around the pivot point
                pose.mulPose(com.mojang.math.Axis.ZP.rotationDegrees((float) dragRotation));

                pose.scale(1.15f, 1.15f, 1.0f);
                pose.translate(-CARD_WIDTH / 2f, -CARD_HEIGHT / 2f, 0);

                // Render card texture
                graphics.blit(skill.getTexture(), 0, 0, 0, 0, CARD_WIDTH, CARD_HEIGHT, CARD_WIDTH, CARD_HEIGHT);

                pose.translate(0, 0, 0.1f);

                // Render text
                float scaleX = CARD_WIDTH / 32f;
                float scaleY = CARD_HEIGHT / 48f;

                graphics.drawString(this.font, skill.getName(), (int)(2 * scaleX), (int)(3 * scaleY), skill.getRarity().getColor(), false);

                int maxWidth = (int)(27 * scaleX);
                int y = (int)(26.5 * scaleY);
                List<FormattedCharSequence> lines = this.font.split(skill.getDescription(), maxWidth);
                for (FormattedCharSequence line : lines) {
                    graphics.drawString(this.font, line, (int)(2 * scaleX), y, 0x000000, false);
                    y += 9;
                }

                pose.popPose();
            }
        }
    }

    private int calculateSpacing(int cardCount) {
        int availableWidth = this.width - 80;
        int totalCardWidth = CARD_WIDTH * cardCount;
        int spacingWidth = availableWidth - totalCardWidth;
        int spacing = spacingWidth / Math.max(1, cardCount - 1);
        return Mth.clamp(spacing, -CARD_WIDTH + 10, MAX_CARD_SPACING);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        // Title
        graphics.drawString(this.font, this.title, 8, 6, 0xFFFFFF, false);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        renderBg(graphics, partialTick, mouseX, mouseY);
        super.renderLabels(graphics, mouseX, mouseY);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && hoveredCard != -1) {
            draggedCard = hoveredCard;
            targetSlot = hoveredCard;
            dragStartX = mouseX;
            dragStartY = mouseY;
            dragCurrentX = mouseX;
            dragCurrentY = mouseY;
            dragLastX = mouseX;
            dragLastY = mouseY;
            dragRotation = 0;

            // Calculate offset from card center
            Player player = Minecraft.getInstance().player;
            if (player != null) {
                int cardCount = skillOrder.size();
                int spacing = calculateSpacing(cardCount);
                int totalWidth = CARD_WIDTH * cardCount + spacing * (cardCount - 1);
                int startX = (this.width - totalWidth) / 2;
                int startY = (this.height - CARD_HEIGHT) / 2;

                int cardX = startX + hoveredCard * (CARD_WIDTH + spacing);
                float baseY = startY + idleOffsets[hoveredCard];

                dragOffsetX = mouseX - (cardX + CARD_WIDTH / 2.0);
                dragOffsetY = mouseY - (baseY + CARD_HEIGHT / 2.0);
            }
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0 && draggedCard != -1) {
            // Apply the final order from displayOrder
            if (targetSlot != -1 && targetSlot != draggedCard) {
                skillOrder = new ArrayList<>(displayOrder);

                // Send to server
                net.neoforged.neoforge.network.PacketDistributor.sendToServer(
                    new net.galaxi.surge.network.ReorderSkillsPacket(skillOrder)
                );
            }

            draggedCard = -1;
            targetSlot = -1;
            dragRotation = 0;
            displayOrder = new ArrayList<>(skillOrder);
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (draggedCard != -1) {
            dragLastX = dragCurrentX;
            dragLastY = dragCurrentY;
            dragCurrentX = mouseX;
            dragCurrentY = mouseY;
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    private void updateDisplayOrder() {
        if (draggedCard == -1 || targetSlot == -1) return;

        displayOrder = new ArrayList<>(skillOrder);
        String draggedSkillId = skillOrder.get(draggedCard);
        displayOrder.remove(draggedSkillId);
        displayOrder.add(targetSlot, draggedSkillId);
    }

    private int getCardIndexAtPosition(double mouseX) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return -1;

        int cardCount = displayOrder.size();
        int baseSpacing = calculateSpacing(cardCount);
        int gapSize = draggedCard != -1 ? CARD_WIDTH + 30 : 0;

        // Recalculate positions
        int[] cardPositions = new int[cardCount];
        int currentX = 0;
        for (int i = 0; i < cardCount; i++) {
            cardPositions[i] = currentX;
            currentX += CARD_WIDTH + baseSpacing;
            if (draggedCard != -1 && i == targetSlot) {
                currentX += gapSize;
            }
        }

        int totalWidth = currentX - baseSpacing;
        int startX = (this.width - totalWidth) / 2;

        // Convert mouse position to relative position
        double relativeX = mouseX - startX;

        // Before first card
        if (relativeX < cardPositions[0] + CARD_WIDTH / 2) {
            return 0;
        }

        // After last card
        if (relativeX > cardPositions[cardCount - 1] + CARD_WIDTH / 2) {
            return cardCount - 1;
        }

        // Find the slot between cards
        for (int i = 0; i < cardCount - 1; i++) {
            int card1CenterX = cardPositions[i] + CARD_WIDTH / 2;
            int card2CenterX = cardPositions[i + 1] + CARD_WIDTH / 2;
            int midpoint = (card1CenterX + card2CenterX) / 2;

            if (relativeX < midpoint) {
                return i;
            }
        }

        return cardCount - 1;
    }
}
