package com.jhapps.clien.test

class SampleGeneric {


    fun test() {

        greaterThan("a", "b")


    }


    private fun <T : Comparable<T>> greaterThan(lhs: T, rhs: T): Boolean {
        return lhs > rhs
    }


}


/*
* interface TestGeneric<T>{
*  void setItem(T item);
* }
* */
interface TestGeneric<in T> {
    fun setItem(item: T)
}

class TestGenericClass : TestGeneric<Int>{
    override fun setItem(item: Int) {
        //TODO
    }
}


/*
* WildCard Type argument - java
*
*
* T : read/write 모두가능
* ? extends T : read만 가능한 서브타입와일드 카드
* ? super T : write만 가능한 슈퍼 타입 와일드 카드
*
* WildCard Type argument - Kotlin
* T : 별도의 Wildcard 정의가 없이 read/write 가능
* in T : Java의 ? super T와 동일. write만 가능
* out T : Java의 ? extends T와 동일. read만 가능
*
* */