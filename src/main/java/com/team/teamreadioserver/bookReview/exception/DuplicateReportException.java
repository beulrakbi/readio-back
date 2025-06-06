package com.team.teamreadioserver.bookReview.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// 이 예외가 발생하면 HTTP 상태 코드 409 Conflict를 반환하도록 설정
@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateReportException extends RuntimeException {
    public DuplicateReportException(String message) {
        super(message);
    }
}