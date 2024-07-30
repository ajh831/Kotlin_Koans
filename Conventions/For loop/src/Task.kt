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