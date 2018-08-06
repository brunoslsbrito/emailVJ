package com.vistorieja.emailws;

public class PasswordEncoderGenerator {
	public static void main(String[] args) {

//		String password = "123456";
		String password = PasswordGenerator.getRandomPassword(8);
		System.out.println(password.toString());

	}
}
