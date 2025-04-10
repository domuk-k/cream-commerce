package edu.creamcommerce.interfaces

import edu.creamcommerce.interfaces.response.ApiResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import java.util.NoSuchElementException
import java.util.concurrent.TimeoutException

@RestControllerAdvice
class GlobalExceptionHandler {
    private val log = LoggerFactory.getLogger(javaClass)
    
    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ApiResponse<Nothing>> {
        log.error("Unhandled exception occurred", e)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse.Companion.error("서버 내부 오류가 발생했습니다."))
    }
    
    @ExceptionHandler(NoSuchElementException::class)
    fun handleNoSuchElementException(e: NoSuchElementException): ResponseEntity<ApiResponse<Nothing>> {
        log.debug("Resource not found: {}", e.message)
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.Companion.error(e.message ?: "요청한 리소스를 찾을 수 없습니다."))
    }
    
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(e: MethodArgumentNotValidException): ResponseEntity<ApiResponse<Map<String, String>>> {
        val errors = e.bindingResult.fieldErrors.associate { it.field to (it.defaultMessage ?: "유효하지 않은 값입니다") }
        log.debug("Validation error: {}", errors)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse(error = "입력값이 유효하지 않습니다", data = errors))
    }
    
    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleTypeMismatchException(e: MethodArgumentTypeMismatchException): ResponseEntity<ApiResponse<Nothing>> {
        log.debug("Type mismatch for parameter: {}", e.name)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.Companion.error("파라미터 타입이 올바르지 않습니다: ${e.name}"))
    }
    
    @ExceptionHandler(TimeoutException::class)
    fun handleTimeoutException(e: TimeoutException): ResponseEntity<ApiResponse<Nothing>> {
        log.warn("Request timed out", e)
        return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
            .body(ApiResponse.Companion.error("요청 처리 시간이 초과되었습니다."))
    }
    
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(e: IllegalArgumentException): ResponseEntity<ApiResponse<Nothing>> {
        log.debug("Invalid argument: {}", e.message)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.Companion.error(e.message ?: "요청 파라미터가 올바르지 않습니다."))
    }
}