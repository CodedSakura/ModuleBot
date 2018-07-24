package eu.thephisics101.modulebot.modules.info;

import eu.thephisics101.modulebot.hosts.Command;
import net.dv8tion.jda.core.entities.Category;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;

import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class ChannelInfo extends Command {
    @Override
    public String getName() {
        return "channel";
    }

    @Override
    public String getHelp() {
        return "Gives information about a channel";
    }

    @Override
    public String[] getUsages() {
        return new String[]{"[ID]", "[channel name]"};
    }

    @Override
    public void run(Message m) {
        String o = get(getArg(m), m);
        if (o.startsWith("$ERROR$")) o = o.substring(7);
        send(o);
    }

    private static final String[] ERR = {
            "$ERROR$No channel specified",
            "$ERROR$Multiple channels found, use ID if possible",
            "$ERROR$Channel not found"
    };

    static String get(String args, Message m) {
        TextChannel tc = null;
        VoiceChannel vc = null;
        Category cc = null;
        if (args.equals("")) return ERR[0];
        else {
            if (args.matches("\\d{17,18}")) {
                if (m.getGuild().getTextChannelById(args) != null)
                    tc = m.getGuild().getTextChannelById(args);
                else if (m.getGuild().getVoiceChannelById(args) != null)
                    vc = m.getGuild().getVoiceChannelById(args);
                else if (m.getGuild().getCategoryById(args) != null)
                    cc = m.getGuild().getCategoryById(args);
            } else {
                List<TextChannel> tcs = m.getGuild().getTextChannelsByName(args, true);
                List<VoiceChannel> vcs = m.getGuild().getVoiceChannelsByName(args, true);
                List<Category> ccs = m.getGuild().getCategoriesByName(args, true);
                if (m.getMentionedChannels().size() > 0) {
                    if (m.getMentionedChannels().size() > 1) return ERR[1];
                    else tc = m.getMentionedChannels().get(0);
                } else if (tcs.size() != 0) {
                    if (tcs.size() > 1) {
                        List<TextChannel> tcs2 = m.getGuild().getTextChannelsByName(args, false);
                        if (tcs2.size() != 0) {
                            if (tcs2.size() > 1) return ERR[1];
                            else tc = tcs2.get(0);
                        } else return ERR[2];
                    } else tc = tcs.get(0);
                }
                if (vcs.size() != 0) {
                    if (vcs.size() > 1) {
                        List<VoiceChannel> vcs2 = m.getGuild().getVoiceChannelsByName(args, false);
                        if (vcs2.size() != 0) {
                            if (vcs2.size() > 1) return ERR[1];
                            else vc = vcs2.get(0);
                        } else return ERR[2];
                    }
                }
                if (ccs.size() != 0) {
                    if (ccs.size() > 1) {
                        List<Category> ccs2 = m.getGuild().getCategoriesByName(args, false);
                        if (ccs2.size() != 0) {
                            if (ccs2.size() > 1) return ERR[1];
                            else cc = ccs2.get(0);
                        } else return ERR[2];
                    }
                }
            }
        }
        if (tc != null && vc != null || vc != null && cc != null || tc != null && cc != null) return ERR[1];
        if (tc != null) {
            StringBuilder sb = new StringBuilder("```\n");
            Map<String, String> s = new LinkedHashMap<>();
            s.put("name",          tc.getName());
            s.put("id",            tc.getId());
            s.put("created",       tc.getCreationTime().format(DateTimeFormatter.RFC_1123_DATE_TIME));
            s.put("position",      Integer.toString(tc.getPosition()));
            s.put("topic",         tc.getTopic());
            s.put("pin count",     Integer.toString(tc.getPinnedMessages().complete().size()));
            s.put("is NSFW",       tc.isNSFW() ? "yes" : "no");
            for (String k : s.keySet()) sb.append(k).append(Info.spaces(k)).append(s.get(k)).append("\n");
            return sb.append("\n```").toString();
        } else if (vc != null) {
            StringBuilder sb = new StringBuilder("```\n");
            Map<String, String> s = new LinkedHashMap<>();
            s.put("name",          vc.getName());
            s.put("id",            vc.getId());
            s.put("created",       vc.getCreationTime().format(DateTimeFormatter.RFC_1123_DATE_TIME));
            s.put("position",      Integer.toString(vc.getPosition()));
            s.put("bitrate",       Integer.toString(vc.getBitrate()));
            s.put("max users",     Integer.toString(vc.getUserLimit()));
            s.put("members",       Integer.toString(vc.getMembers().size()));
            for (String k : s.keySet()) sb.append(k).append(Info.spaces(k)).append(s.get(k)).append("\n");
            return sb.append("\n```").toString();
        } else if (cc != null) {
            StringBuilder sb = new StringBuilder("```\n");
            Map<String, String> s = new LinkedHashMap<>();
            s.put("name",          cc.getName());
            s.put("id",            cc.getId());
            s.put("created",       cc.getCreationTime().format(DateTimeFormatter.RFC_1123_DATE_TIME));
            s.put("position",      Integer.toString(cc.getPosition()));
            s.put("text channels", Integer.toString(cc.getTextChannels().size()));
            StringJoiner sj = new StringJoiner(", "); for (TextChannel c : cc.getTextChannels()) sj.add(c.getName());
            if (cc.getTextChannels().size() > 0) s.put("tc:", sj.toString());
            s.put("voice channels",Integer.toString(cc.getVoiceChannels().size()));
            sj = new StringJoiner(", "); for (VoiceChannel c : cc.getVoiceChannels()) sj.add(c.getName());
            if (cc.getVoiceChannels().size() > 0) s.put("vc:", sj.toString());
            for (String k : s.keySet()) sb.append(k).append(Info.spaces(k)).append(s.get(k)).append("\n");
            return sb.append("\n```").toString();
        }
        return ERR[2];
    }
}
