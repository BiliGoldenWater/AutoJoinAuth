package indi.goldenwater.listeners;

import indi.goldenwater.AutoJoinAuth;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.Member;
import net.mamoe.mirai.contact.MemberPermission;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.Listener;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.At;
import net.mamoe.mirai.message.data.MessageChain;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class OnGroupMessageEvent {
    private static Listener<GroupMessageEvent> listener;

    public static void register() {
        listener = GlobalEventChannel.INSTANCE.subscribeAlways(GroupMessageEvent.class,
                event -> {
                    Properties prop = AutoJoinAuth.INSTANCE.getProp();
                    Group fromGroup = event.getGroup();

                    if (fromGroup.getId() == Long.parseLong(prop.getProperty("audit_group"))) { // 处理消息
                        MessageChain messageChain = event.getMessage();
                        String message = messageChain.contentToString();

                        if (message.startsWith("/")) { // 处理指令
                            String[] command = message.split(" ");

                            if (!(command.length >= 1)) return;

                            final Member sender = event.getSender();
                            if ("/passed".equals(command[0])) {
                                if (!(sender.getPermission() == MemberPermission.OWNER ||
                                        sender.getPermission() == MemberPermission.ADMINISTRATOR)) {
                                    fromGroup.sendMessage(
                                            new At(sender.getId()).plus("你没有权限"));
                                    return;
                                }

                                if (!(command.length >= 2)) {
                                    fromGroup.sendMessage(
                                            new At(sender.getId()).plus("不正确的命令"));
                                    return;
                                }

                                if (!command[1].startsWith("@")) {
                                    fromGroup.sendMessage(
                                            new At(sender.getId()).plus("未知的群成员"));
                                    return;
                                } else {
                                    command[1] = command[1].replace("@", "");
                                }

                                long targetMemberID;
                                try {
                                    targetMemberID = Long.parseLong(command[1]);
                                } catch (NumberFormatException e) {
                                    fromGroup.sendMessage(
                                            new At(sender.getId()).plus("未知的群成员"));
                                    return;
                                }

                                NormalMember targetMember = fromGroup.get(targetMemberID);
                                if (targetMember == null) {
                                    fromGroup.sendMessage(
                                            new At(sender.getId()).plus("未知的群成员"));
                                    return;
                                }

                                String verifyCode;
                                verifyCode = Integer.toHexString(targetMember.hashCode());
                                List<Object> data = new ArrayList<>();
                                data.add(verifyCode);
                                data.add(sender.getId());
                                data.add(sender.getNameCard());
                                data.add(sender.getNick());

                                AutoJoinAuth.INSTANCE.getPassedUsers().put(targetMemberID, data);
                                fromGroup.sendMessage(
                                        new At(targetMember.getId()).plus("\n你已通过审核, 请加入群" +
                                                prop.getProperty("target_group") + "\n验证码:" + verifyCode));
                            } else {
                                fromGroup.sendMessage(new At(sender.getId())
                                        .plus("\n作者 by.Golden_Water\n" +
                                                "用法: /passed <@对应群成员> (通过对应群成员的审核)"));
                            }
                        }
                    }
                });

    }

    public static void unregister() {
        listener.complete();
    }
}
