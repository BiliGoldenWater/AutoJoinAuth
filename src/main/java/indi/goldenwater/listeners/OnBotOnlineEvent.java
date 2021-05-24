package indi.goldenwater.listeners;

import indi.goldenwater.AutoJoinAuth;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.Listener;
import net.mamoe.mirai.event.events.BotOnlineEvent;

public class OnBotOnlineEvent {
    private static Listener<BotOnlineEvent> listener;

    public static void register() {
        listener = GlobalEventChannel.INSTANCE.subscribeAlways(BotOnlineEvent.class,
                event -> {
                    AutoJoinAuth.INSTANCE.setBotInstance(event.getBot());
                    OnBotOnlineEvent.unregister();
                });

    }

    public static void unregister() {
        listener.complete();
    }
}
