package com.xinyanyuan.project.at15.model;

public class Praises {
	private String nickNameP;
	private String userPhoneP;
	private int praiseId;

	public String getNickNameP() {
		return nickNameP;
	}

	public void setNickNameP(String nickNameP) {
		this.nickNameP = nickNameP;
	}

	public String getUserPhoneP() {
		return userPhoneP;
	}

	public void setUserPhoneP(String userPhoneP) {
		this.userPhoneP = userPhoneP;
	}

	public int getPraiseId() {
		return praiseId;
	}

	public void setPraiseId(int praiseId) {
		this.praiseId = praiseId;
	}

	@Override
	public String toString() {
		return "Praises [nickNameP=" + nickNameP + ", userPhoneP=" + userPhoneP
				+ ", praiseId=" + praiseId + "]";
	}

	public Praises(String nickNameP, String userPhoneP, int praiseId) {
		super();
		this.nickNameP = nickNameP;
		this.userPhoneP = userPhoneP;
		this.praiseId = praiseId;
	}

	public Praises() {
		// TODO Auto-generated constructor stub
	}
}
