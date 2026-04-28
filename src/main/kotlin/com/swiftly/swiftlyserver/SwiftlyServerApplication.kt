/*
 * Swiftly Server Application Entry Point
 */
package com.swiftly.swiftlyserver

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SwiftlyServerApplication

fun main(args: Array<String>) {
    runApplication<SwiftlyServerApplication>(*args)
}
