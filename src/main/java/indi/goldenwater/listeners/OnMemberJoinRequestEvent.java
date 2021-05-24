package indi.goldenwater.listeners;

import indi.goldenwater.AutoJoinAuth;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.contact.NormalMember;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.Listener;
import net.mamoe.mirai.event.events.MemberJoinRequestEvent;
import net.mamoe.mirai.message.data.PlainText;

import java.util.List;
import java.util.Map;
import java.util.Properties;

public class OnMemberJoinRequestEvent {
    private static Listener<MemberJoinRequestEvent> listener;

    public static void register() {
        listener = GlobalEventChannel.INSTANCE.subscribeAlways(MemberJoinRequestEvent.class,
                event -> {
                    Properties prop = AutoJoinAuth.INSTANCE.getProp();
                    Group fromGroup = event.getGroup();
                    if (fromGroup == null) return;

                    if (!(fromGroup.getId() == Long.parseLong(prop.getProperty("target_group")))) return;

                    Map<Long, List<Object>> passedUsers = AutoJoinAuth.INSTANCE.getPassedUsers();
                    List<Object> passData = passedUsers.get(event.getFromId());

                    if (passData == null) {
                        reject(event, prop);
                        return;
                    }

                    String verifyCode = (String) passData.get(0);
                    long operatorID = (long) passData.get(1);
                    String operatorNameCard = (String) passData.get(2);
                    String operatorNick = (String) passData.get(3);

                    if (event.getMessage().contains(verifyCode)) {
                        event.accept();

                        NormalMember operator = fromGroup.get(operatorID);
                        if (operator != null) {
                            operatorNameCard = operator.getNameCard();
                            operatorNick = operator.getNick();
                        }

                        String operatorName;
                        if (operatorNameCard != null) {
                            operatorName = operatorNameCard;
                        } else {
                            operatorName = operatorNick;
                        }

                        fromGroup.sendMessage(new PlainText(
                                "欢迎 " + event.getFromNick() + "\n" +
                                        "通过人: " + operatorName));
                    } else {
                        reject(event, prop);
                    }

                    passedUsers.remove(event.getFromId());
                });

    }

    private static void reject(MemberJoinRequestEvent event, Properties prop) {
        if (prop.getProperty("auto_reject").equals("1")) {
            event.reject(false, "不正确的验证码");
        }
    }


    public static void unregister() {
        listener.complete();
    }
}
