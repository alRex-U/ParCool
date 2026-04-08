package com.alrex.parcool.server.command.impl;

import com.alrex.parcool.common.block.zipline.ZiplineHookTileEntity;
import com.alrex.parcool.common.block.zipline.ZiplineInfo;
import com.alrex.parcool.common.item.zipline.ZiplineRopeItem;
import com.alrex.parcool.common.zipline.Zipline;
import com.alrex.parcool.common.zipline.ZiplineType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.CompoundTagArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.util.Mth;

public class ZiplineCommand {
    private static final String ARGS_NAME_HOOK_POS_1 = "hook1";
    private static final String ARGS_NAME_HOOK_POS_2 = "hook2";
    private static final String ARGS_NAME_ZIPLINE_INFO = "zipline_info";

    public static ArgumentBuilder<CommandSourceStack, ?> getBuilder() {
        return Commands
                .literal("zipline")
                .requires(commandSource -> commandSource.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER))
                .then(Commands.literal("set")
                        .then(
                                Commands.argument(
                                        ARGS_NAME_HOOK_POS_1, BlockPosArgument.blockPos()
                                ).then(
                                        Commands.argument(
                                                ARGS_NAME_HOOK_POS_2, BlockPosArgument.blockPos()
                                        ).executes(c -> ZiplineCommand.setZipline(c, false)).then(
                                                Commands.argument(
                                                        ARGS_NAME_ZIPLINE_INFO, CompoundTagArgument.compoundTag()
                                                ).executes(c -> ZiplineCommand.setZipline(c, true))
                                        )
                                )
                        )
                );
    }

    private static int setZipline(CommandContext<CommandSourceStack> context, boolean hasInfo) throws CommandSyntaxException {
        BlockPos hook1 = BlockPosArgument.getLoadedBlockPos(context, ARGS_NAME_HOOK_POS_1);
        BlockPos hook2 = BlockPosArgument.getLoadedBlockPos(context, ARGS_NAME_HOOK_POS_2);
        var level = context.getSource().getLevel();

        double horizontalDistSqr = Mth.square(hook1.getX() - hook2.getX()) + Mth.square(hook1.getZ() - hook2.getZ());
        if (horizontalDistSqr > Zipline.MAXIMUM_HORIZONTAL_DISTANCE * Zipline.MAXIMUM_HORIZONTAL_DISTANCE) {
            context.getSource().sendFailure(Component.translatable("parcool.command.message.hookTooFar"));
        }
        double verticalDist = Math.abs(hook2.getY() - hook1.getY());
        if (verticalDist * Mth.fastInvSqrt(horizontalDistSqr) > 1. || verticalDist > Zipline.MAXIMUM_VERTICAL_DISTANCE) {
            context.getSource().sendFailure(Component.translatable("parcool.command.message.ziplineTooSteep"));
        }
        var entity = level.getBlockEntity(hook1);
        if (!(entity instanceof ZiplineHookTileEntity)) {
            context.getSource().sendFailure(Component.translatable("parcool.command.message.hookNotFound", hook1.toShortString()));
            return 1;
        }
        var entity2 = level.getBlockEntity(hook2);
        if (!(entity2 instanceof ZiplineHookTileEntity)) {
            context.getSource().sendFailure(Component.translatable("parcool.command.message.hookNotFound", hook2.toShortString()));
            return 1;
        }

        ZiplineInfo info;
        if (hasInfo) {
            var infoTag = CompoundTagArgument.getCompoundTag(context, ARGS_NAME_ZIPLINE_INFO);
            info = ZiplineInfo.CODEC.decode(NbtOps.INSTANCE, infoTag).getOrThrow().getFirst();
        } else {
            info = new ZiplineInfo(ZiplineType.STANDARD, ZiplineRopeItem.DEFAULT_COLOR);
        }

        if (!((ZiplineHookTileEntity) entity).connectTo((ZiplineHookTileEntity) entity2, info)) {
            return 1;
        }
        context.getSource().sendSuccess(() -> Component.translatable("parcool.command.message.success.setZipline", hook1.toShortString(), hook2.toShortString()), true);
        return 0;
    }
}
