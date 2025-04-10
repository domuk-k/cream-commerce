package edu.creamcommerce.interfaces.response

import org.springframework.http.ResponseEntity

data class ApiResponse<T>(
    val data: T? = null,
    val message: String? = null,
    val error: String? = null
) {
    companion object {
        fun <T> success(data: T, message: String? = null): ApiResponse<T> {
            return ApiResponse(
                data = data,
                message = message
            )
        }
        
        fun <T> error(error: String): ApiResponse<T> {
            return ApiResponse(
                error = error
            )
        }
    }
}

fun <T> T.toSuccessResponse(message: String? = null): ResponseEntity<ApiResponse<T>> {
    return ResponseEntity.ok(ApiResponse.success(this, message))
}

fun emptySuccessResponse(): ResponseEntity<ApiResponse<Nothing>> {
    return ResponseEntity.ok(ApiResponse())
}