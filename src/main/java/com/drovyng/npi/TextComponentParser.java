package com.drovyng.npi;

import net.coreprotect.spigot.SpigotAdapter;
import net.coreprotect.utility.Color;
import net.coreprotect.utility.Util;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;

public class TextComponentParser {

    public static TextComponent getComponent(String string, String bypass) {
        TextComponent message = Component.text("");
        StringBuilder builder = new StringBuilder();

        Matcher matcher = Util.tagParser.matcher(string);

        while(matcher.find()) {
            String value = matcher.group(1);
            if (value != null) {
                if (builder.length() > 0) {
                    message = addBuilder(message, builder);
                }

                String[] data = value.split("\\|", 3);
                if (data[0].equals("COMMAND")) {

                    TextComponent component = Component.text(data[2]);

                    if (component.content().contains(Color.GREY)){
                        component = component.style(Style.style(TextColor.fromHexString("#F0F0F0")));
                        component = component.content(component.content().replace(Color.GREY, ""));
                    }

                    if (data[1].contains("/co teleport")){
                        data[1] = data[1].substring(data[1].indexOf(" ", 13)+1);
                        data[1] = data[1].replace(".5", "");
                    }

                    component = component.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.SUGGEST_COMMAND, data[1]));
                    component = component.hoverEvent(HoverEvent.showText(Component.text(data[1])));

                    message = message.append(component);
                } else if (data[0].equals("POPUP")) {
                    TextComponent component = Component.text(data[2]);

                    if (component.content().contains(Color.GREY)){
                        component = component.style(Style.style(TextColor.fromHexString("#D8D8D8")));
                        component = component.content(component.content().replace(Color.GREY, ""));
                    }

                    TextComponent hoverText = Component.text(data[1]);
                    if (hoverText.content().contains("MSK")){
                        hoverText = hoverText.style(Style.style(TextColor.fromHexString("#FFFFFF")));
                        hoverText = hoverText.content(hoverText.content().replace(Color.GREY, ""));
                    }

                    component = component.hoverEvent(HoverEvent.showText(hoverText));

                    message = message.append(component);
                }
            } else {
                builder.append(matcher.group(2));
            }
        }

        if (builder.length() > 0) {
            message = addBuilder(message, builder);
        }

        if (bypass != null) {
            message = Component.text(bypass).append(message);
        }

        return message;
    }

    private static TextComponent addBuilder(TextComponent message, StringBuilder builder) {
        String[] splitBuilder = builder.toString().split(DARK_AQUA.toString());

//        for(int i = 0; i < splitBuilder.length; ++i) {
//            if (i > 0) {
//                message = Component.text(splitBuilder[i], TextColor.color(0x00FFFF)).append(message);
//            } else {
////                if (splitBuilder[i].contains(Color.GREY)){
////                    message = Component.text(splitBuilder[i].replace(Color.GREY, ""), TextColor.fromCSSHexString("#F0F0F0")).append(message);
////                }
////                else{
//                message = Component.text(splitBuilder[i]).append(message);
////                }
//            }
//        }

        message = message.append(Component.text(builder.toString()));

        builder.setLength(0);

        return message;
    }
    private static ChatColor DARK_AQUA;
    static {
        DARK_AQUA = ChatColor.AQUA;
    }
}