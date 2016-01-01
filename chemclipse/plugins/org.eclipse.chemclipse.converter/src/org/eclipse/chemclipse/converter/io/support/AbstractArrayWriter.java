/*******************************************************************************
 * Copyright (c) 2012, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.converter.io.support;

public abstract class AbstractArrayWriter implements IArrayWriter {

	private int position;
	private byte[] data;

	public AbstractArrayWriter(byte[] data) {
		this.position = 0;
		this.data = data;
	}

	@Override
	public void writeIntegerAsBigEndian(int b) {

		write(get4BytesAsIntegerBigEndian(b));
	}

	/**
	 * Writes the byte array to this.data at the actual position.
	 * 
	 * @param bytes
	 */
	@Override
	public void write(byte[] bytes) {

		for(int i = 0; i < bytes.length; i++) {
			data[position++] = bytes[i];
		}
	}

	@Override
	public void skipBytes(int bytes) {

		position += bytes;
	}

	/**
	 * Getting a byte array for a specific entry.<br/>
	 * The example has e.g. 1 + 19 chars. The first char defines the
	 * length of the string.<br/>
	 * So you can call the method:<br/>
	 * ... getBytes(19, chrom.getFileString);<br/>
	 * and get a byte array of the length 20.<br/>
	 * 
	 * @param writeBytes
	 * @param entry
	 * @return byte[]
	 */
	@Override
	public byte[] getBytesWithStringLengthIndex(int writeBytes, String entry) {

		byte[] bytes = new byte[++writeBytes];
		int length = entry.length();
		byte[] bytesLength;
		byte[] bytesEntry;
		/*
		 * 1 byte is used to store the length of the string.
		 * That's why the length must not exceed a length of 255.
		 */
		if(writeBytes > 255) {
			writeBytes = 255;
		}
		/*
		 * Checks if the entry is longer than the allowed byte length.
		 */
		if(length > writeBytes) {
			length = writeBytes;
		}
		/*
		 * Getting the byte arrays for the length an the entry
		 */
		bytesLength = get4BytesAsIntegerBigEndian(length);
		bytesEntry = entry.getBytes();
		bytes[0] = bytesLength[3];
		for(int i = 1; i <= length; i++) {
			bytes[i] = bytesEntry[i - 1];
		}
		return bytes;
	}

	/**
	 * Converts an integer value to a byte array.
	 * 
	 * @param value
	 * @return byte[]
	 */
	@Override
	public byte[] get4BytesAsIntegerBigEndian(int value) {

		byte[] bytes = new byte[4];
		for(int i = 0; i < 4; i++) {
			/*
			 * i = 0 - 0000|0000 >> 0000|0000 = 0 i = 1 - 0000|0001 >> 0000|1000
			 * = 8 i = 2 - 0000|0010 >> 0001|0000 = 16 i = 3 - 0000|0011 >>
			 * 0001|1000 = 24
			 */
			int shift = i << 3;
			bytes[3 - i] = (byte)((value & (0xff << shift)) >>> shift);
		}
		return bytes;
	}

	/**
	 * Converts an integer value to a byte array.
	 * 
	 * @param value
	 * @return byte[]
	 */
	@Override
	public byte[] get2BytesAsShortBigEndian(int value) {

		byte[] bytes = new byte[2];
		for(int i = 0; i < 2; i++) {
			/*
			 * i = 0 - 0000|0000 >> 0000|0000 = 0 i = 1 - 0000|0001 >> 0000|1000
			 * = 8
			 */
			int shift = i << 3;
			bytes[1 - i] = (byte)((value & (0xff << shift)) >>> shift);
		}
		return bytes;
	}
}
