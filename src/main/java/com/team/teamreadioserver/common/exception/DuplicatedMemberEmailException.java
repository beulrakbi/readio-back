package com.team.teamreadioserver.common.exception;

public class DuplicatedMemberEmailException extends RuntimeException{
	public DuplicatedMemberEmailException(String message) {
		super(message);
	}
}
