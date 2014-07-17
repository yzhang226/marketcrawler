package org.omega.marketcrawler.entity;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;


public class AltCoin extends _BaseEntity {
	
	private static final long serialVersionUID = 4040098830692159352L;
	
	public static final byte STATUS_ACTIVE = 0;
	public static final byte STATUS_INACTIVE = 1;
	public static final byte STATUS_WATCHED = 11;
	
	private Integer id;
	private int topicId;
	
	/**  active = 0, inactive = 1, watched = 11 */
	private byte status;
	/** 0 - 5 */
	private byte interest;
	
	// topic info
	private String author;
	private String title;
	private Integer replies;
	private Integer views;
//	private String link;
	private String publishContent;
	
	private Date launchTime;
	private Timestamp lastPostTime;
	private Timestamp publishDate;
	private Timestamp createTime;
	
	// coin info
	private String name;
	private String abbrName;
	private String algo;
	private String proof;
	private String launchRaw;
	
	// coin detail
	private Long totalAmount;
	private Integer blockTime;
	private Integer halfBlocks;
	private Integer halfDays;
	private Double blockReward;
	private String difficultyAdjust;
	private Long preMined;
	private Double minedPercentage;
	
	// pow info
	private Double powDays;
	private Integer powHeight;
	private Long powAmount;
	
	//
	private String memo;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public int getTopicId() {
		return topicId;
	}

	public void setTopicId(int topicId) {
		this.topicId = topicId;
	}

	public byte getStatus() {
		return status;
	}

	public void setStatus(byte status) {
		this.status = status;
	}

	public byte getInterest() {
		return interest;
	}

	public void setInterest(byte interest) {
		this.interest = interest;
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

//	public String getLink() {
//		return link;
//	}
//
//	public void setLink(String link) {
//		this.link = link;
//	}

	public String getPublishContent() {
		return publishContent;
	}

	public void setPublishContent(String publishContent) {
		this.publishContent = publishContent;
	}

	public Date getLaunchTime() {
		return launchTime;
	}

	public void setLaunchTime(Date launchTime) {
		this.launchTime = launchTime;
	}

	public Timestamp getLastPostTime() {
		return lastPostTime;
	}

	public void setLastPostTime(Timestamp lastPostTime) {
		this.lastPostTime = lastPostTime;
	}

	public Timestamp getPublishDate() {
		return publishDate;
	}

	public void setPublishDate(Timestamp publishDate) {
		this.publishDate = publishDate;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAbbrName() {
		return abbrName;
	}

	public void setAbbrName(String abbrName) {
		this.abbrName = abbrName;
	}

	public String getAlgo() {
		return algo;
	}

	public void setAlgo(String algo) {
		this.algo = algo;
	}

	public String getProof() {
		return proof;
	}

	public void setProof(String proof) {
		this.proof = proof;
	}

	public String getLaunchRaw() {
		return launchRaw;
	}

	public void setLaunchRaw(String launchRaw) {
		this.launchRaw = launchRaw;
	}

	public Long getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(Long totalAmount) {
		this.totalAmount = totalAmount;
	}

	public Integer getBlockTime() {
		return blockTime;
	}

	public void setBlockTime(Integer blockTime) {
		this.blockTime = blockTime;
	}

	public Integer getHalfBlocks() {
		return halfBlocks;
	}

	public void setHalfBlocks(Integer halfBlocks) {
		this.halfBlocks = halfBlocks;
	}

	public Integer getHalfDays() {
		return halfDays;
	}

	public void setHalfDays(Integer halfDays) {
		this.halfDays = halfDays;
	}

	public Double getBlockReward() {
		return blockReward;
	}

	public void setBlockReward(Double blockReward) {
		this.blockReward = blockReward;
	}

	public String getDifficultyAdjust() {
		return difficultyAdjust;
	}

	public void setDifficultyAdjust(String difficultyAdjust) {
		this.difficultyAdjust = difficultyAdjust;
	}

	public Long getPreMined() {
		return preMined;
	}

	public void setPreMined(Long preMined) {
		this.preMined = preMined;
	}

	public Double getMinedPercentage() {
		return minedPercentage;
	}

	public void setMinedPercentage(Double minedPercentage) {
		this.minedPercentage = minedPercentage;
	}

	public Double getPowDays() {
		return powDays;
	}

	public void setPowDays(Double powDays) {
		this.powDays = powDays;
	}

	public Integer getPowHeight() {
		return powHeight;
	}

	public void setPowHeight(Integer powHeight) {
		this.powHeight = powHeight;
	}

	public Long getPowAmount() {
		return powAmount;
	}

	public void setPowAmount(Long powAmount) {
		this.powAmount = powAmount;
	}

	public String getMemo() {
		return memo;
	}
	
	public void setMemo(String memo) {
		this.memo = memo;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + topicId;
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AltCoin other = (AltCoin) obj;
		if (topicId != other.topicId)
			return false;
		return true;
	}

	public String toHtml() {
		StringBuilder sb = new StringBuilder("<tr>");
		sb.append("<td>").append(null != publishDate ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(publishDate) : "").append("</td>");
		sb.append("<td>").append(title).append("</td>");
		sb.append("<td>").append(author).append("</td>");
		sb.append("<td>").append(replies).append("</td>");
		sb.append("<td>").append(views).append("</td>");
		sb.append("</tr>\n");
		
		return sb.toString();
	}
	
	public String toReableText() {
		StringBuilder sb = new StringBuilder();
		sb.append(id).append(", ").append(topicId).append(", ").append(status).append(", ").append(interest).append(", ")
		.append(author).append(", ").append(title).append(", ").append(replies).append(", ").append(views).append(", ")
		.append(publishContent).append(", ").append(launchTime).append(", ")
		.append(lastPostTime).append(", ").append(publishDate).append(", ").append(createTime).append(", ").append(name).append(", ")
		.append(abbrName).append(", ").append(algo).append(", ").append(proof).append(", ").append(launchRaw).append(", ")
		.append(totalAmount).append(", ").append(blockTime).append(", ").append(halfBlocks).append(", ").append(halfDays).append(", ")
		.append(blockReward).append(", ").append(difficultyAdjust).append(", ").append(preMined).append(", ").append(minedPercentage).append(", ")
		.append(powDays).append(", ").append(powHeight).append(", ").append(powAmount).append(", ").append(memo);
		return sb.toString();
	}
	
	
}
