package com.zwyl.guide.util;

/**
 * Create by liao on 14-9-20
 */
public class CRCUtil {

	private static final byte[] hex = "0123456789ABCDEF".getBytes();

	public static String getCRC16(byte[] data) {
		int CRCTABLE[] = {
				0xF078, 0xE1F1, 0xD36A, 0xC2E3, 0xB65C, 0xA7D5, 0x954E, 0x84C7,
				0x7C30, 0x6DB9, 0x5F22, 0x4EAB, 0x3A14, 0x2B9D, 0x1906, 0x088F,
				0xE0F9, 0xF170, 0xC3EB, 0xD262, 0xA6DD, 0xB754, 0x85CF, 0x9446,
				0x6CB1, 0x7D38, 0x4FA3, 0x5E2A, 0x2A95, 0x3B1C, 0x0987, 0x180E,
				0xD17A, 0xC0F3, 0xF268, 0xE3E1, 0x975E, 0x86D7, 0xB44C, 0xA5C5,
				0x5D32, 0x4CBB, 0x7E20, 0x6FA9, 0x1B16, 0x0A9F, 0x3804, 0x298D,
				0xC1FB, 0xD072, 0xE2E9, 0xF360, 0x87DF, 0x9656, 0xA4CD, 0xB544,
				0x4DB3, 0x5C3A, 0x6EA1, 0x7F28, 0x0B97, 0x1A1E, 0x2885, 0x390C,
				0xB27C, 0xA3F5, 0x916E, 0x80E7, 0xF458, 0xE5D1, 0xD74A, 0xC6C3,
				0x3E34, 0x2FBD, 0x1D26, 0x0CAF, 0x7810, 0x6999, 0x5B02, 0x4A8B,
				0xA2FD, 0xB374, 0x81EF, 0x9066, 0xE4D9, 0xF550, 0xC7CB, 0xD642,
				0x2EB5, 0x3F3C, 0x0DA7, 0x1C2E, 0x6891, 0x7918, 0x4B83, 0x5A0A,
				0x937E, 0x82F7, 0xB06C, 0xA1E5, 0xD55A, 0xC4D3, 0xF648, 0xE7C1,
				0x1F36, 0x0EBF, 0x3C24, 0x2DAD, 0x5912, 0x489B, 0x7A00, 0x6B89,
				0x83FF, 0x9276, 0xA0ED, 0xB164, 0xC5DB, 0xD452, 0xE6C9, 0xF740,
				0x0FB7, 0x1E3E, 0x2CA5, 0x3D2C, 0x4993, 0x581A, 0x6A81, 0x7B08,
				0x7470, 0x65F9, 0x5762, 0x46EB, 0x3254, 0x23DD, 0x1146, 0x00CF,
				0xF838, 0xE9B1, 0xDB2A, 0xCAA3, 0xBE1C, 0xAF95, 0x9D0E, 0x8C87,
				0x64F1, 0x7578, 0x47E3, 0x566A, 0x22D5, 0x335C, 0x01C7, 0x104E,
				0xE8B9, 0xF930, 0xCBAB, 0xDA22, 0xAE9D, 0xBF14, 0x8D8F, 0x9C06,
				0x5572, 0x44FB, 0x7660, 0x67E9, 0x1356, 0x02DF, 0x3044, 0x21CD,
				0xD93A, 0xC8B3, 0xFA28, 0xEBA1, 0x9F1E, 0x8E97, 0xBC0C, 0xAD85,
				0x45F3, 0x547A, 0x66E1, 0x7768, 0x03D7, 0x125E, 0x20C5, 0x314C,
				0xC9BB, 0xD832, 0xEAA9, 0xFB20, 0x8F9F, 0x9E16, 0xAC8D, 0xBD04,
				0x3674, 0x27FD, 0x1566, 0x04EF, 0x7050, 0x61D9, 0x5342, 0x42CB,
				0xBA3C, 0xABB5, 0x992E, 0x88A7, 0xFC18, 0xED91, 0xDF0A, 0xCE83,
				0x26F5, 0x377C, 0x05E7, 0x146E, 0x60D1, 0x7158, 0x43C3, 0x524A,
				0xAABD, 0xBB34, 0x89AF, 0x9826, 0xEC99, 0xFD10, 0xCF8B, 0xDE02,
				0x1776, 0x06FF, 0x3464, 0x25ED, 0x5152, 0x40DB, 0x7240, 0x63C9,
				0x9B3E, 0x8AB7, 0xB82C, 0xA9A5, 0xDD1A, 0xCC93, 0xFE08, 0xEF81,
				0x07F7, 0x167E, 0x24E5, 0x356C, 0x41D3, 0x505A, 0x62C1, 0x7348,
				0x8BBF, 0x9A36, 0xA8AD, 0xB924, 0xCD9B, 0xDC12, 0xEE89, 0xFF00
		};

		int CRCVal = 0;

		for (int i = 0; i < data.length; i++) {
			CRCVal = CRCTABLE[(CRCVal ^= ((data[i]) & 0xFF)) & 0xFF] ^ (CRCVal >> 8);
		}

		return Integer.toHexString(CRCVal);
	}

	public static String crcTable(byte[] bytes) {
		int[] table = {
				0x0000, 0xC0C1, 0xC181, 0x0140, 0xC301, 0x03C0, 0x0280, 0xC241,
				0xC601, 0x06C0, 0x0780, 0xC741, 0x0500, 0xC5C1, 0xC481, 0x0440,
				0xCC01, 0x0CC0, 0x0D80, 0xCD41, 0x0F00, 0xCFC1, 0xCE81, 0x0E40,
				0x0A00, 0xCAC1, 0xCB81, 0x0B40, 0xC901, 0x09C0, 0x0880, 0xC841,
				0xD801, 0x18C0, 0x1980, 0xD941, 0x1B00, 0xDBC1, 0xDA81, 0x1A40,
				0x1E00, 0xDEC1, 0xDF81, 0x1F40, 0xDD01, 0x1DC0, 0x1C80, 0xDC41,
				0x1400, 0xD4C1, 0xD581, 0x1540, 0xD701, 0x17C0, 0x1680, 0xD641,
				0xD201, 0x12C0, 0x1380, 0xD341, 0x1100, 0xD1C1, 0xD081, 0x1040,
				0xF001, 0x30C0, 0x3180, 0xF141, 0x3300, 0xF3C1, 0xF281, 0x3240,
				0x3600, 0xF6C1, 0xF781, 0x3740, 0xF501, 0x35C0, 0x3480, 0xF441,
				0x3C00, 0xFCC1, 0xFD81, 0x3D40, 0xFF01, 0x3FC0, 0x3E80, 0xFE41,
				0xFA01, 0x3AC0, 0x3B80, 0xFB41, 0x3900, 0xF9C1, 0xF881, 0x3840,
				0x2800, 0xE8C1, 0xE981, 0x2940, 0xEB01, 0x2BC0, 0x2A80, 0xEA41,
				0xEE01, 0x2EC0, 0x2F80, 0xEF41, 0x2D00, 0xEDC1, 0xEC81, 0x2C40,
				0xE401, 0x24C0, 0x2580, 0xE541, 0x2700, 0xE7C1, 0xE681, 0x2640,
				0x2200, 0xE2C1, 0xE381, 0x2340, 0xE101, 0x21C0, 0x2080, 0xE041,
				0xA001, 0x60C0, 0x6180, 0xA141, 0x6300, 0xA3C1, 0xA281, 0x6240,
				0x6600, 0xA6C1, 0xA781, 0x6740, 0xA501, 0x65C0, 0x6480, 0xA441,
				0x6C00, 0xACC1, 0xAD81, 0x6D40, 0xAF01, 0x6FC0, 0x6E80, 0xAE41,
				0xAA01, 0x6AC0, 0x6B80, 0xAB41, 0x6900, 0xA9C1, 0xA881, 0x6840,
				0x7800, 0xB8C1, 0xB981, 0x7940, 0xBB01, 0x7BC0, 0x7A80, 0xBA41,
				0xBE01, 0x7EC0, 0x7F80, 0xBF41, 0x7D00, 0xBDC1, 0xBC81, 0x7C40,
				0xB401, 0x74C0, 0x7580, 0xB541, 0x7700, 0xB7C1, 0xB681, 0x7640,
				0x7200, 0xB2C1, 0xB381, 0x7340, 0xB101, 0x71C0, 0x7080, 0xB041,
				0x5000, 0x90C1, 0x9181, 0x5140, 0x9301, 0x53C0, 0x5280, 0x9241,
				0x9601, 0x56C0, 0x5780, 0x9741, 0x5500, 0x95C1, 0x9481, 0x5440,
				0x9C01, 0x5CC0, 0x5D80, 0x9D41, 0x5F00, 0x9FC1, 0x9E81, 0x5E40,
				0x5A00, 0x9AC1, 0x9B81, 0x5B40, 0x9901, 0x59C0, 0x5880, 0x9841,
				0x8801, 0x48C0, 0x4980, 0x8941, 0x4B00, 0x8BC1, 0x8A81, 0x4A40,
				0x4E00, 0x8EC1, 0x8F81, 0x4F40, 0x8D01, 0x4DC0, 0x4C80, 0x8C41,
				0x4400, 0x84C1, 0x8581, 0x4540, 0x8701, 0x47C0, 0x4680, 0x8641,
				0x8201, 0x42C0, 0x4380, 0x8341, 0x4100, 0x81C1, 0x8081, 0x4040,
		};

		int crc = 0x0000;

		for (byte b : bytes) {
			crc = (crc >>> 8) ^ table[(crc ^ b) & 0xff];
		}

		return Integer.toHexString(crc);
	}

//	public static String mkCrc16(byte[] b) {
//		sun.misc.CRC16 crc16 = new sun.misc.CRC16();
//		for (int i = 0; i < b.length; i++)
//			crc16.update(b[i]);
//		return Integer.toHexString(crc16.value);
//	}

	public static final String evalCRC16(byte[] data) {
		int crc = 0xFFFF;

		for (int i = 0; i < data.length; i++) {
			crc = (data[i] << 8) ^ crc;

			for (int j = 0; j < 8; ++j) {
				if ((crc & 0x8000) != 0)
					crc = (crc << 1) ^ 0x1021;
				else
					crc <<= 1;
			}
		}

		return Integer.toHexString((crc ^ 0xFFFF) & 0xFFFF);
	}

	private static int parse(char c) {
		if (c >= 'a')
			return (c - 'a' + 10) & 0x0f;
		if (c >= 'A')
			return (c - 'A' + 10) & 0x0f;
		return (c - '0') & 0x0f;
	}

	public static byte[] HexString2Bytes(String hexstr) {
		byte[] b = new byte[hexstr.length() / 2];
		int j = 0;

		for (int i = 0; i < b.length; i++) {
			char c0 = hexstr.charAt(j++);
			char c1 = hexstr.charAt(j++);
			b[i] = (byte) ((parse(c0) << 4) | parse(c1));
		}
		return b;
	}
}
