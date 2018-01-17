package com.tqdev.crudapi.spatial;

public class Geometry {

	private byte[] bytes;

	public Geometry(byte[] bytes) {
		this.bytes = bytes;
	}

	public byte[] getBytes() {
		return bytes;
	}
}
