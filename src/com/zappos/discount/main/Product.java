package com.zappos.discount.main;

//POJO for the product details
public class Product {
	
	private String price;
	private String productUrl;
	private String productName;
	private String thumbnailImageUrl;
	private String percentOff;
	private String productId;
	private int isFavorite; // to keep the state of the favorite button
	
	
	public Product(String price, String productUrl, String productName,
			String thumbnailImageUrl, String percentOff, String productId,int isFavorite) {
		super();
		this.price = price;
		this.productUrl = productUrl;
		this.productName = productName;
		this.thumbnailImageUrl = thumbnailImageUrl;
		this.percentOff = percentOff;
		this.productId = productId;
		this.isFavorite = isFavorite;
	}
	public String getPrice() {
		return price;
	}
	public void setPrice(String price) {
		this.price = price;
	}
	public String getProductUrl() {
		return productUrl;
	}
	public void setProductUrl(String productUrl) {
		this.productUrl = productUrl;
	}
	public String getProductName() {
		return productName;
	}
	
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public String getThumbnailImageUrl() {
		return thumbnailImageUrl;
	}
	public void setThumbnailImageUrl(String thumbnailImageUrl) {
		this.thumbnailImageUrl = thumbnailImageUrl;
	}
	public String getPercentOff() {
		return percentOff;
	}
	public void setPercentOff(String percentOff) {
		this.percentOff = percentOff;
	}
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	public int getIsFavorite() {
		return isFavorite;
	}
	public void setIsFavorite(int isFavorite) {
		this.isFavorite = isFavorite;
	}
	
	
}
