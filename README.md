# :sparkles: 코틀린 교안 풀이
코틀린 교안 문제들의 답안입니다.

문제 풀이가 완료되는 대로 업데이트 됩니다.

(Collection의 Introduction까지 진행)ㄷ


## 목차
1. [Introduction](#1-introduction)
    1. [Hello, world!](#11-hello-world)
    2. [Named arguments](#12-named-arguments)
    3. [Default arguments](#13-default-arguments)
    4. [Triple-quoted Strings](#14-triple-quoted-strings)
    5. [String templates](#15-string-templates)
    6. [Nullable types](#16-nullable-types)
    7. [Nothing type](#17-nothing-type)
    8. [Lambdas](#18-lambdas)
2. [Classes](#2-classes)
    1. [Data classes](#21-data-classes)
    2. [Smart casts](#22-smart-casts)
    3. [Sealed classes](#23-sealed-classes)
    4. [Rename on import](#24-rename-on-import)
    5. [Extension functions](#25-extension-functions)
3. [Conventions](#3-conventions)
    1. [Comparison](#31-comparison)
    2. [Ranges](#32-ranges)
    3. [For loop](#33-for-loop)
    4. [Operators overloading](#34-operators-overloading)
    5. [Invoke](#35-invoke)
4. [Collections](#4-collections)
    1. [Introduction](#41-introduction)
    2. [Sort](#42-sort) 
    3. [Filter map](#43-filter-map)
    4. [All Any and other predicates](#44-all-any-and-other-predicates)
    5. [Associate](#45-associate) 
    6. [GroupBy](#46-groupby)
    7. [Partition](#47-partition)

## 1. Introduction

### 1.1. Hello, world!
```kotlin
fun start(): String = "OK"
```

### 1.2. Named arguments
```kotlin
fun joinOptions(options: Collection<String>) =
        options.joinToString(
                separator = ", ",
                prefix = "[",
                postfix = "]"
        )
```

### 1.3. Default arguments
```kotlin
fun foo(name: String, number: Int = 42, toUpperCase: Boolean = false) =
        (if (toUpperCase) name.uppercase() else name) + number

fun useFoo() = listOf(
        foo("a"),
        foo("b", number = 1),
        foo("c", toUpperCase = true),
        foo(name = "d", number = 2, toUpperCase = true)
)

```

### 1.4. Triple-quoted Strings
```kotlin
const val question = "life, the universe, and everything"
const val answer = 42

val tripleQuotedString = """
    #question = "$question"
    #answer = $answer""".trimMargin("#")

fun main() {
    println(tripleQuotedString)
}
```

### 1.5. String templates
```kotlin
val month = "(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)"

fun getPattern(): String = """\d{2} $month \d{4}"""

```

### 1.6. Nullable types
```kotlin
fun sendMessageToClient(
        client: Client?, message: String?, mailer: Mailer
) {
    val email = client?.personalInfo?.email
    if (email != null && message != null) {
        mailer.sendMessage(email, message)
    }
}

class Client(val personalInfo: PersonalInfo?)
class PersonalInfo(val email: String?)
interface Mailer {
    fun sendMessage(email: String, message: String)
}

```

### 1.7. Nothing type
```kotlin
import java.lang.IllegalArgumentException

fun failWithWrongAge(age: Int?) : Nothing {
    throw IllegalArgumentException("Wrong age: $age")
}

fun checkAge(age: Int?) {
    if (age == null || age !in 0..150) failWithWrongAge(age)
    println("Congrats! Next year you'll be ${age + 1}.")
}

fun main() {
    checkAge(10)
}
```

### 1.8. Lambdas
```kotlin
fun containsEven(collection: Collection<Int>): Boolean =
        collection.any { x -> x % 2 == 0 }
```

```kotlin
fun containsEven(collection: Collection<Int>): Boolean =
        collection.any { it % 2 == 0 }
```

## 2. Classes

### 2.1. Data classes
```kotlin
data class Person (
    val name: String,
    val age: Int
    )

fun getPeople(): List<Person> {
    return listOf(Person("Alice", 29), Person("Bob", 31))
}

fun comparePeople(): Boolean {
    val p1 = Person("Alice", 29)
    val p2 = Person("Alice", 29)
    return p1 == p2  // should be true
}
```

### 2.2. Smart casts
```kotlin
fun eval(expr: Expr): Int =
        when (expr) {
            is Num -> expr.value
            is Sum -> eval(expr.left) + eval(expr.right)
            else -> throw IllegalArgumentException("Unknown expression")
        }

interface Expr
class Num(val value: Int) : Expr
class Sum(val left: Expr, val right: Expr) : Expr
```

### 2.3. Sealed classes
```kotlin
fun eval(expr: Expr): Int =
        when (expr) {
            is Num -> expr.value
            is Sum -> eval(expr.left) + eval(expr.right)
        }

sealed interface Expr
class Num(val value: Int) : Expr
class Sum(val left: Expr, val right: Expr) : Expr
```

### 2.4. Rename on import
```kotlin
import kotlin.random.Random as KRandom
import java.util.Random as JRandom

fun useDifferentRandomClasses(): String {
    return "Kotlin random: " +
             KRandom.nextInt(2) +
            " Java random:" +
             JRandom().nextInt(2) +
            "."
}
```

### 2.5. Extension functions
```kotlin
fun Int.r(): RationalNumber = RationalNumber(this,1)

fun Pair<Int, Int>.r(): RationalNumber = RationalNumber(first, second)

data class RationalNumber(val numerator: Int, val denominator: Int)
```

## 3. Conventions

### 3.1. Comparison
```kotlin
data class MyDate(val year: Int, val month: Int, val dayOfMonth: Int) : Comparable<MyDate> {
    override fun compareTo(other: MyDate) = when {
            year != other.year -> year - other.year
            month != other.month -> month - other.month
            else -> dayOfMonth - other.dayOfMonth
    }

}

fun test(date1: MyDate, date2: MyDate) {
    // this code should compile:
    println(date1 < date2)
}

```

### 3.2. Ranges
```kotlin
fun checkInRange(date: MyDate, first: MyDate, last: MyDate): Boolean {
    return date in first..last
}
```

### 3.3. For loop
```kotlin
class DateRange(val start: MyDate, val end: MyDate) : Iterable<MyDate> {
    override fun iterator() : Iterator<MyDate> {
        // hasNext, next 구현이 되어야 됨
        return object : Iterator<MyDate> {
            var current: MyDate = start

            override fun hasNext(): Boolean = current <= end

            override fun next() : MyDate {
                // 다음 요소가 없으면 예외 던지기
                if(!hasNext()) throw NoSuchElementException()
                val result = current
                current = current.followingDate()
                return result
            }
        }
    }
}

fun iterateOverDateRange(firstDate: MyDate, secondDate: MyDate, handler: (MyDate) -> Unit) {
    for (date in firstDate..secondDate) {
        handler(date)
    }
}
```

### 3.4. Operators overloading
```kotlin

```

### 3.5. Invoke
```kotlin
class Invokable {
    var numberOfInvocations: Int = 0
        private set

    operator fun invoke(): Invokable {
        numberOfInvocations++
        return this
    }
}

fun invokeTwice(invokable: Invokable) = invokable()()

```

## 4. Collections

### 4.1. Introduction
```kotlin
fun Shop.getSetOfCustomers(): Set<Customer> = this.customers.toSet()
```

### 4.2. Sort
```kotlin
// Return a list of customers, sorted in the descending by number of orders they have made
fun Shop.getCustomersSortedByOrders(): List<Customer> =
customers.sortedByDescending { it.orders.size }
```

### 4.3. Filter map
```kotlin
// Find all the different cities the customers are from
fun Shop.getCustomerCities(): Set<City> =
        customers.map { it.city }.toSet()

// Find the customers living in a given city
fun Shop.getCustomersFrom(city: City): List<Customer> =
        customers.filter { it.city.equals(city) }
```

### 4.4. All Any and other predicates
```kotlin
// Return true if all customers are from a given city
fun Shop.checkAllCustomersAreFrom(city: City): Boolean =
        customers.all { it.city == city } == true

// Return true if there is at least one customer from a given city
fun Shop.hasCustomerFrom(city: City): Boolean =
        customers.any { it.city == city } == true

// Return the number of customers from a given city
fun Shop.countCustomersFrom(city: City): Int =
        customers.count { it.city == city }

// Return a customer who lives in a given city, or null if there is none
fun Shop.findCustomerFrom(city: City): Customer? =
        customers.find { it.city == city }

```

### 4.5. Associate
```kotlin
// Build a map that stores the customers living in a given city
fun Shop.groupCustomersByCity(): Map<City, List<Customer>> =
        customers.groupBy { it.city }
```

### 4.6. GroupBy
```kotlin
// Build a map that stores the customers living in a given city
fun Shop.groupCustomersByCity(): Map<City, List<Customer>> =
        customers.groupBy { it.city }
```

### 4.7. Partition
```kotlin
// Return customers who have more undelivered orders than delivered
fun Shop.getCustomersWithMoreUndeliveredOrders(): Set<Customer> = customers.filter {
      val (delivered, undelivered) = it.orders.partition { it.isDelivered }
      delivered.size < undelivered.size
   }.toSet()

```