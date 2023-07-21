package server

import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

data class Server(
    val hostname: String,
    var successStateCode: Int,
    var isAvailable: AtomicBoolean = AtomicBoolean(true),
) {
    private val failures: AtomicInteger = AtomicInteger(0)

    fun isUnavailable(maxFailureCount: Int) = failures.get() == maxFailureCount

    fun changeToAvailable() {
        this.isAvailable.set(true)
    }

    fun changeToUnavailable() {
        this.isAvailable.set(false)
    }

    fun incrementFailures(): Int {
        return failures.incrementAndGet()
    }

    fun resetFailures() {
        failures.set(0)
    }

    companion object {
        fun fixture(
            hostname: String = "http://localhost:8080",
            successStateCode: Int = 200,
            isAvailable: AtomicBoolean = AtomicBoolean(true)
        ) = Server(hostname, successStateCode, isAvailable)
    }
}
