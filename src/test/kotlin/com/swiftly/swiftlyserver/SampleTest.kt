package com.swiftly.swiftlyserver

import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class SampleTest :
    DescribeSpec({
        describe("덧셈 연산") {
            it("1 더하기 1은 2여야 한다") {
                val result = 1 + 1
                result shouldBe 2
            }
        }

        describe("Kotlin String") {
            context("문자열 결합") {
                it("Hello와 World를 합치면 Hello World가 된다") {
                    val s1 = "Hello"
                    val s2 = "World"
                    "$s1 $s2" shouldBe "Hello World"
                }
            }
        }
    })
