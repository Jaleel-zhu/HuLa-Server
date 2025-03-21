package com.hula.core.chat.service.adapter;

import com.hula.core.chat.domain.entity.GroupMember;
import com.hula.core.chat.domain.enums.GroupRoleEnum;
import com.hula.core.chat.domain.vo.response.ChatMemberListResp;
import com.hula.core.user.domain.entity.User;
import com.hula.core.user.domain.entity.UserFriend;
import com.hula.core.user.domain.enums.WsBaseResp;
import com.hula.core.user.domain.enums.WSRespTypeEnum;
import com.hula.core.user.domain.vo.resp.ws.ChatMemberResp;
import com.hula.core.user.domain.vo.resp.ws.WSFeedMemberResp;
import com.hula.core.user.domain.vo.resp.ws.WSMemberChange;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.hula.core.user.domain.vo.resp.ws.WSMemberChange.CHANGE_TYPE_ADD;
import static com.hula.core.user.domain.vo.resp.ws.WSMemberChange.CHANGE_TYPE_REMOVE;


/**
 * @author nyh
 */
@Component
@Slf4j
public class MemberAdapter {

    public static List<ChatMemberResp> buildMember(List<User> list) {
        return list.stream().map(a -> {
            ChatMemberResp resp = new ChatMemberResp();
			resp.setAccountCode(a.getAccountCode());
            resp.setActiveStatus(a.getActiveStatus());
            resp.setLastOptTime(a.getLastOptTime());
            resp.setUid(a.getId().toString());
            return resp;
        }).collect(Collectors.toList());
    }

    public static List<ChatMemberResp> buildMember(List<UserFriend> list, List<User> userList) {
        Map<Long, User> userMap = userList.stream().collect(Collectors.toMap(User::getId, user -> user));
        return list.stream().map(userFriend -> {
            ChatMemberResp resp = new ChatMemberResp();
            resp.setUid(userFriend.getFriendUid().toString());
            User user = userMap.get(userFriend.getFriendUid());
            if (Objects.nonNull(user)) {
                resp.setActiveStatus(user.getActiveStatus());
                resp.setLastOptTime(user.getLastOptTime());
            }
            return resp;
        }).collect(Collectors.toList());
    }


    public static List<ChatMemberListResp> buildMemberList(List<User> memberList) {
        return memberList.stream()
                .map(a -> {
                    ChatMemberListResp resp = new ChatMemberListResp();
                    BeanUtils.copyProperties(a, resp);
                    resp.setUid(a.getId());
                    return resp;
                }).collect(Collectors.toList());
    }

    public static List<ChatMemberListResp> buildMemberList(Map<Long, User> batch) {
        return buildMemberList(new ArrayList<>(batch.values()));
    }

    public static List<GroupMember> buildMemberAdd(Long groupId, List<Long> waitAddUidList) {
        return waitAddUidList.stream().map(a -> {
            GroupMember member = new GroupMember();
            member.setGroupId(groupId);
            member.setUid(a);
            member.setRole(GroupRoleEnum.MEMBER.getType());
            return member;
        }).collect(Collectors.toList());
    }

    public static WsBaseResp<WSMemberChange> buildMemberAddWS(Long roomId, User user) {
        WsBaseResp<WSMemberChange> wsBaseResp = new WsBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.NEW_FRIEND_SESSION.getType());
        WSMemberChange wsMemberChange = new WSMemberChange();
        wsMemberChange.setActiveStatus(user.getActiveStatus());
        wsMemberChange.setLastOptTime(user.getLastOptTime());
        wsMemberChange.setUid(user.getId());
        wsMemberChange.setRoomId(roomId);
        wsMemberChange.setChangeType(CHANGE_TYPE_ADD);
        wsBaseResp.setData(wsMemberChange);
        return wsBaseResp;
    }

    public static WsBaseResp<WSMemberChange> buildMemberRemoveWS(Long roomId, Long uid) {
        WsBaseResp<WSMemberChange> wsBaseResp = new WsBaseResp<>();
        wsBaseResp.setType(WSRespTypeEnum.NEW_FRIEND_SESSION.getType());
        WSMemberChange wsMemberChange = new WSMemberChange();
        wsMemberChange.setUid(uid);
        wsMemberChange.setRoomId(roomId);
        wsMemberChange.setChangeType(CHANGE_TYPE_REMOVE);
        wsBaseResp.setData(wsMemberChange);
        return wsBaseResp;
    }

	/**
	 * 发朋友圈以后推送的消息
	 * @param uid 发布人
	 * @return
	 */
	public static WsBaseResp<WSFeedMemberResp> buildFeedPushWS(Long uid){
		WsBaseResp<WSFeedMemberResp> wsBaseResp = new WsBaseResp<>();
		wsBaseResp.setType(WSRespTypeEnum.FEED_SEND_MSG.getType());
		wsBaseResp.setData(new WSFeedMemberResp(uid));
		return wsBaseResp;
	}
}
