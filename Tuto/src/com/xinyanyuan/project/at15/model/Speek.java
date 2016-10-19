package com.xinyanyuan.project.at15.model;

import java.util.List;

import com.xinyanyuan.project.at15.utils.Tools;

public class Speek {
	private String icon;
	private String nickName;
	private String content;
	private String mobileType;
	private int browseNum;
	private int speekId;
	private long publicTime;
	private List<String> photos;
	private List<Praises> praises;
	private List<Comments> comments;

	public Speek(String icon, String nickName, String content,
			String mobileType, int browseNum, int speekId, long publicTime,
			List<String> photos, List<Praises> praises, List<Comments> comments) {
		super();
		this.icon = icon;
		this.nickName = nickName;
		this.content = content;
		this.mobileType = mobileType;
		this.browseNum = browseNum;
		this.speekId = speekId;
		this.publicTime = publicTime;
		this.photos = photos;
		this.praises = praises;
		this.comments = comments;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getMobileType() {
		return mobileType;
	}

	public void setMobileType(String mobileType) {
		this.mobileType = mobileType;
	}

	public String getBrowseNum() {
		return "‰Ø¿¿" + browseNum + "¥Œ";
	}

	public void setBrowseNum(int browseNum) {
		this.browseNum = browseNum;
	}

	public int getSpeekId() {
		return speekId;
	}

	public void setSpeekId(int speekId) {
		this.speekId = speekId;
	}

	public String getPublicTime() {
		return Tools.formatSpeekTime(publicTime);
	}

	public void setPublicTime(long publicTime) {
		this.publicTime = publicTime;
	}

	public List<String> getPhotos() {
		return photos;
	}

	public void setPhotos(List<String> photos) {
		this.photos = photos;
	}

	public List<Praises> getPraises() {
		return praises;
	}

	public void setPraises(List<Praises> praises) {
		this.praises = praises;
	}

	public List<Comments> getComments() {
		return comments;
	}

	public void setComments(List<Comments> comments) {
		this.comments = comments;
	}

	@Override
	public String toString() {
		return "Speek [icon=" + icon + ", nickName=" + nickName + ", content="
				+ content + ", mobileType=" + mobileType + ", browseNum="
				+ browseNum + ", speekId=" + speekId + ", publicTime="
				+ publicTime + ", photos=" + photos + ", praises=" + praises
				+ ", comments=" + comments + "]";
	}

	public Speek() {
		// TODO Auto-generated constructor stub
	}

}
