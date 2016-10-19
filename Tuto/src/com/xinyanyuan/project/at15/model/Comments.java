package com.xinyanyuan.project.at15.model;

import java.util.List;

import com.xinyanyuan.project.at15.interfaces.PingLun;

public class Comments implements PingLun {
	private int commentId;
	private String nickNameC;
	private String userPhoneC;
	private String iconC;
	private long timeC;
	private String contentC;
	private List<Replys> replys;

	public int getCommentId() {
		return commentId;
	}

	public void setCommentId(int commentId) {
		this.commentId = commentId;
	}

	public long getTimeC() {
		return timeC;
	}

	public void setTimeC(long timeC) {
		this.timeC = timeC;
	}

	public String getNickNameC() {
		return nickNameC;
	}

	public void setNickNameC(String nickNameC) {
		this.nickNameC = nickNameC;
	}

	public String getUserPhoneC() {
		return userPhoneC;
	}

	public void setUserPhoneC(String userPhoneC) {
		this.userPhoneC = userPhoneC;
	}

	public String getIconC() {
		return iconC;
	}

	public void setIconC(String iconC) {
		this.iconC = iconC;
	}

	public String getContentC() {
		return contentC;
	}

	public void setContentC(String contentC) {
		this.contentC = contentC;
	}

	public List<Replys> getReplys() {
		return replys;
	}

	public void setReplys(List<Replys> replys) {
		this.replys = replys;
	}

	public Comments(String nickNameC, String userPhoneC, long timeC,
			int commentId, String contentC, List<Replys> replys) {
		super();
		this.nickNameC = nickNameC;
		this.userPhoneC = userPhoneC;
		this.contentC = contentC;
		this.replys = replys;
		this.timeC = timeC;
		this.commentId = commentId;
	}

	@Override
	public String toString() {
		return "Comments [nickNameC=" + nickNameC + ", userPhoneC="
				+ userPhoneC + ", iconC=" + iconC + ", contentC=" + contentC
				+ ", replys=" + replys + "]";
	}

}
