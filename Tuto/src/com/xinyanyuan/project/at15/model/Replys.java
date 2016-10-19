package com.xinyanyuan.project.at15.model;

import com.xinyanyuan.project.at15.interfaces.PingLun;

public class Replys implements PingLun {
	private String nickNameR;
	private String userPhoneR;
	private String toNickNameR;
	private String toUserPhoneR;
	private String contentR;
	private long timeR;
	private int replyId;

	public int getReplyId() {
		return replyId;
	}

	public void setReplyId(int replyId) {
		this.replyId = replyId;
	}

	public String getNickNameR() {
		return nickNameR;
	}

	public void setNickNameR(String nickNameR) {
		this.nickNameR = nickNameR;
	}

	public String getUserPhoneR() {
		return userPhoneR;
	}

	public void setUserPhoneR(String userPhoneR) {
		this.userPhoneR = userPhoneR;
	}

	public String getToNickNameR() {
		return toNickNameR;
	}

	public void setToNickNameR(String toNickNameR) {
		this.toNickNameR = toNickNameR;
	}

	public String getToUserPhoneR() {
		return toUserPhoneR;
	}

	public void setToUserPhoneR(String toUserPhoneR) {
		this.toUserPhoneR = toUserPhoneR;
	}

	public String getContentR() {
		return contentR;
	}

	public void setContentR(String contentR) {
		this.contentR = contentR;
	}

	public long getTimeR() {
		return timeR;
	}

	public void setTimeR(long timeR) {
		this.timeR = timeR;
	}

	@Override
	public String toString() {
		return "Replys [nickNameR=" + nickNameR + ", userPhoneR=" + userPhoneR
				+ ", toNickNameR=" + toNickNameR + ", toUserPhoneR="
				+ toUserPhoneR + ", contentR=" + contentR + ", timeR=" + timeR
				+ "]";
	}

	public Replys(String nickNameR, String userPhoneR, String toNickNameR,
			String toUserPhoneR, String contentR, long timeR, int replyId) {
		super();
		this.nickNameR = nickNameR;
		this.userPhoneR = userPhoneR;
		this.toNickNameR = toNickNameR;
		this.toUserPhoneR = toUserPhoneR;
		this.contentR = contentR;
		this.timeR = timeR;
		this.replyId = replyId;
	}

}
