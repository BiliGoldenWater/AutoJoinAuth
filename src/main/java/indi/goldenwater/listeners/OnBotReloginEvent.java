package indi.goldenwater.listeners;

import indi.goldenwater.AutoJoinAuth;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.Listener;
import net.mamoe.mirai.event.events.BotReloginEvent;

public class OnBotReloginEvent {
    private static Listener<BotReloginEvent> listener;

    public static void register() {
        listener = GlobalEventChannel.INSTANCE.subscribeAlways(BotReloginEvent.class,
                event -> {
                    AutoJoinAuth.INSTANCE.setBotInstance(event.getBot());
                    OnGroupMessageEvent.unregister();
                    OnMemberJoinRequestEvent.unregister();
                    OnGroupMessageEvent.register();
                    OnMemberJoinRequestEvent.register();
                });

    }

    public static void unregister() {
        listener.complete();
    }
}
