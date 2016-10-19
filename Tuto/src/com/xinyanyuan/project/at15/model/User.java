package com.xinyanyuan.project.at15.model;

import android.text.TextUtils;

import com.xinyanyuan.project.at15.utils.Tools;

public class User {
	private String userId;
	private String nickName;
	private String icon;
	private String background;;
	private String userPhone;
	private String age;
	private String sex;
	private String constellation;
	private String personExplain;
	private String realName;
	private long birthDay;
	private String hometown;
	private String location;
	private String mobilePhone;
	private String email;

	@Override
	public String toString() {
		return "User [userId=" + userId + ", nickName=" + nickName + ", icon="
				+ icon + ", userPhone=" + userPhone + "]";
	}

	public User(String userId, String nickName, String icon, String background,
			String realName, String personExplain, String sex, long birthDay,
			String constellation, String hometown, String location,
			String mobilePhone, String email) {
		if (!TextUtils.isEmpty(userId))
			this.userId = userId;
		if (!TextUtils.isEmpty(nickName))
			this.nickName = nickName;
		if (!TextUtils.isEmpty(icon))
			this.icon = icon;
		if (!TextUtils.isEmpty(background))
			this.background = background;
		if (!TextUtils.isEmpty(realName))
			this.realName = realName;
		if (!TextUtils.isEmpty(personExplain))
			this.personExplain = personExplain;
		if (!TextUtils.isEmpty(sex))
			this.sex = sex;
		if (birthDay != 0L)
			this.birthDay = birthDay;
		if (!TextUtils.isEmpty(constellation))
			this.constellation = constellation;
		if (!TextUtils.isEmpty(hometown))
			this.hometown = hometown;
		if (!TextUtils.isEmpty(location))
			this.location = location;
		if (!TextUtils.isEmpty(mobilePhone))
			this.mobilePhone = mobilePhone;
		if (!TextUtils.isEmpty(email))
			this.email = email;
	}

	public String getRealName() {
		return realName;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public String getBirthDay() {
		return Tools.long2String(birthDay);
	}

	public void setBirthDay(long birthDay) {
		this.birthDay = birthDay;
	}

	public String getHometown() {
		return hometown;
	}

	public void setHometown(String hometown) {
		this.hometown = hometown;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getConstellation() {
		return constellation;
	}

	public void setConstellation(String constellation) {
		this.constellation = constellation;
	}

	public String getPersonExplain() {
		return personExplain;
	}

	public void setPersonExplain(String personExplain) {
		this.personExplain = personExplain;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getBackground() {
		return background;
	}

	public void setBackground(String background) {
		this.background = background;
	}

	public String getUserPhone() {
		return userPhone;
	}

	public void setUserPhone(String userPhone) {
		this.userPhone = userPhone;
	}

	public User(String userId, String nickName, String icon, String userPhone) {
		super();
		this.userId = userId;
		this.nickName = nickName;
		this.icon = icon;
		this.userPhone = userPhone;
	}

	public User() {
		// TODO Auto-generated constructor stub
	}
}
