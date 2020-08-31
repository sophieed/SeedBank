package application.integration.support;

import java.util.Random;

import application.seeds.SeedPacket;

/**
 * Seed packet builder with a fluent API to create a {@link SeedPacket} object with the specified values. 
 * Where values are not specified, appropriate defaults will be added where necessary, and non-required 
 * will be null.
 */
public class SeedPacketBuilder {
	
	private String name;
	private String type;
	private int packSize = 0;
	private int numberRemaining = 0;
	
	
	/**
	 * Specify the seed packet's name.
	 * @param name The name to be used
	 * @return this
	 */
	public SeedPacketBuilder withName(String name) {
		this.name = name;
		return this;
	}
	
	
	/**
	 * Specify the seed packet's type.
	 * @param type The type to be used
	 * @return this
	 */
	public SeedPacketBuilder withType(String type) {
		this.type = type;
		return this;
	}
	
	
	/**
	 * Specify the number remaining for the seed packet.
	 * @param numberRemaining The number remaining to be used
	 * @return this
	 */
	public SeedPacketBuilder withNumberRemaining(int numberRemaining) {
		this.numberRemaining = numberRemaining;
		return this;
	}
	
	
	/**
	 * Specify the pack size for the seed packet.
	 * @param packSize The pack size to be used
	 * @return this
	 */
	public SeedPacketBuilder withPackSize(int packSize) {
		this.packSize = packSize;
		return this;
	}
	
	
	/**
	 * Build the seed packet with the specified values and return it. Where values have not been specified, 
	 * a random alternative will be generated
	 * @return
	 */
	public SeedPacket build() {
		
		SeedPacket seedPacket = new SeedPacket();
		
		seedPacket.setName(name.isBlank() ? randomString(10) : name);
		seedPacket.setType(type.isBlank() ? randomString(5) : type);
		seedPacket.setNumberRemaining(numberRemaining);
		seedPacket.setPackSize(packSize);
		
		return seedPacket;
	}
	
	
	/*
	 * Private helper method to generate a random alphabetic string of a given length
	 */
	private String randomString(int length) {
	
		int leftLimit = 97; // Letter 'a'
	    int rightLimit = 122; // Letter 'z'
	    
	    Random random = new Random();
	    StringBuilder buffer = new StringBuilder(length);
	    for (int i = 0; i < length; i++) {
	        int randomLimitedInt = leftLimit + (int) 
	          (random.nextFloat() * (rightLimit - leftLimit + 1));
	        buffer.append((char) randomLimitedInt);
	    }
	    return buffer.toString();
	}

}
