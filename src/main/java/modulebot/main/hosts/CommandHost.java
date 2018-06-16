package modulebot.main.hosts;

import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.emote.EmoteAddedEvent;
import net.dv8tion.jda.core.events.emote.EmoteRemovedEvent;
import net.dv8tion.jda.core.events.emote.update.EmoteUpdateNameEvent;
import net.dv8tion.jda.core.events.emote.update.EmoteUpdateRolesEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.guild.member.*;
import net.dv8tion.jda.core.events.guild.update.*;
import net.dv8tion.jda.core.events.guild.voice.*;
import net.dv8tion.jda.core.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageEmbedEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionRemoveAllEvent;
import net.dv8tion.jda.core.events.message.guild.react.GuildMessageReactionRemoveEvent;
import net.dv8tion.jda.core.events.role.RoleCreateEvent;
import net.dv8tion.jda.core.events.role.RoleDeleteEvent;
import net.dv8tion.jda.core.events.role.update.*;

public abstract class CommandHost implements CH {

    public void onReady(ReadyEvent event) {}

    public void onEnabled(long guild, TextChannel c) {}
    public void onDisabled(long guild, TextChannel c) {}
    public void onToggled(long guild, TextChannel c) {}

    //Message Events
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {}
    public void onGuildMessageUpdate(GuildMessageUpdateEvent event) {}
    public void onGuildMessageDelete(GuildMessageDeleteEvent event) {}
    public void onGuildMessageEmbed(GuildMessageEmbedEvent event) {}
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {}
    public void onGuildMessageReactionRemove(GuildMessageReactionRemoveEvent event) {}
    public void onGuildMessageReactionRemoveAll(GuildMessageReactionRemoveAllEvent event) {}

    //Guild Events
    public void onGuildJoin(GuildJoinEvent event) {}
    public void onGuildLeave(GuildLeaveEvent event) {}

    //Guild Update Events
    public void onGuildUpdateAfkChannel(GuildUpdateAfkChannelEvent event) {}
    public void onGuildUpdateSystemChannel(GuildUpdateSystemChannelEvent event) {}
    public void onGuildUpdateAfkTimeout(GuildUpdateAfkTimeoutEvent event) {}
    public void onGuildUpdateExplicitContentLevel(GuildUpdateExplicitContentLevelEvent event) {}
    public void onGuildUpdateIcon(GuildUpdateIconEvent event) {}
    public void onGuildUpdateMFALevel(GuildUpdateMFALevelEvent event) {}
    public void onGuildUpdateName(GuildUpdateNameEvent event){}
    public void onGuildUpdateNotificationLevel(GuildUpdateNotificationLevelEvent event) {}
    public void onGuildUpdateOwner(GuildUpdateOwnerEvent event) {}
    public void onGuildUpdateRegion(GuildUpdateRegionEvent event) {}
    public void onGuildUpdateSplash(GuildUpdateSplashEvent event) {}
    public void onGuildUpdateVerificationLevel(GuildUpdateVerificationLevelEvent event) {}
    public void onGuildUpdateFeatures(GuildUpdateFeaturesEvent event) {}

    //Guild Member Events
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {}
    public void onGuildMemberLeave(GuildMemberLeaveEvent event) {}
    public void onGuildMemberRoleAdd(GuildMemberRoleAddEvent event) {}
    public void onGuildMemberRoleRemove(GuildMemberRoleRemoveEvent event) {}
    public void onGuildMemberNickChange(GuildMemberNickChangeEvent event) {}

    //Guild Voice Events
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {}
    public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {}
    public void onGuildVoiceMove(GuildVoiceMoveEvent event) {}
    public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {}
    public void onGuildVoiceMute(GuildVoiceMuteEvent event) {}
    public void onGuildVoiceDeafen(GuildVoiceDeafenEvent event) {}
    public void onGuildVoiceGuildMute(GuildVoiceGuildMuteEvent event) {}
    public void onGuildVoiceGuildDeafen(GuildVoiceGuildDeafenEvent event) {}
    public void onGuildVoiceSelfMute(GuildVoiceSelfMuteEvent event) {}
    public void onGuildVoiceSelfDeafen(GuildVoiceSelfDeafenEvent event) {}
    public void onGuildVoiceSuppress(GuildVoiceSuppressEvent event) {}

    //Role events
    public void onRoleCreate(RoleCreateEvent event) {}
    public void onRoleDelete(RoleDeleteEvent event) {}

    //Role Update Events
    public void onRoleUpdateColor(RoleUpdateColorEvent event) {}
    public void onRoleUpdateHoisted(RoleUpdateHoistedEvent event) {}
    public void onRoleUpdateMentionable(RoleUpdateMentionableEvent event) {}
    public void onRoleUpdateName(RoleUpdateNameEvent event) {}
    public void onRoleUpdatePermissions(RoleUpdatePermissionsEvent event) {}
    public void onRoleUpdatePosition(RoleUpdatePositionEvent event) {}

    //Emote Events
    public void onEmoteAdded(EmoteAddedEvent event) {}
    public void onEmoteRemoved(EmoteRemovedEvent event) {}

    //Emote Update Events
    public void onEmoteUpdateName(EmoteUpdateNameEvent event) {}
    public void onEmoteUpdateRoles(EmoteUpdateRolesEvent event) {}
}
