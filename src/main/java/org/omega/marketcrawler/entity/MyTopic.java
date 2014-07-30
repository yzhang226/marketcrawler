package org.omega.marketcrawler.entity;

public class MyTopic extends _BaseEntity {
	
	private static final long serialVersionUID = 3138816061561501848L;
	
	private Integer id;
	private Short boardId;
	private Integer topicId;
	private String author;
	private String title;
	private Integer replies;
	private Integer views;
	
	private String content;
	
	private Integer lastPostTime;
	private Integer publishTime;
	private Integer createTime;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Short getBoardId() {
		return boardId;
	}
	public void setBoardId(Short boardId) {
		this.boardId = boardId;
	}
	public Integer getTopicId() {
		return topicId;
	}
	public void setTopicId(Integer topicId) {
		this.topicId = topicId;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Integer getReplies() {
		return replies;
	}
	public void setReplies(Integer replies) {
		this.replies = replies;
	}
	public Integer getViews() {
		return views;
	}
	public void setViews(Integer views) {
		this.views = views;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public Integer getPublishTime() {
		return publishTime;
	}
	public void setPublishTime(Integer publishTime) {
		this.publishTime = publishTime;
	}
	public Integer getLastPostTime() {
		return lastPostTime;
	}
	public void setLastPostTime(Integer lastPostTime) {
		this.lastPostTime = lastPostTime;
	}
	public Integer getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Integer createTime) {
		this.createTime = createTime;
	}
	
}
