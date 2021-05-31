package com.jhapps.clien.test

import java.util.*

class TestHashTable(val size: Int) {

    /*
    * 배열 선언
    * [0] : LinkedList<Node>
    * [1] : LinkedList<Node>
    * [2] : LinkedList<Node>
    * ...
    * ...
    * [N] : LinkedList<Node>
    *
    * */
    private val data: Array<LinkedList<Node>?> = Array(size, { null })


    /*
    * Key값을 아스키코드로 변환하여 모두 더함.
    * */
    private fun getHashCode(key: String): Int {
        var hashCode = 0
        for (c in key.toCharArray()) {
            hashCode += c.toInt()
        }
        return hashCode
    }

    /*
    * getHashCode 함수로 반환된 hashCode를 배열 사이즈로 나눈 나머지를 사용하여 인덱스를 만듦.
    * */
    private fun convertToIndex(hashCode: Int): Int {
        return hashCode % size
    }

    /*
    * put과 get에서 사용.
    *
    * list(linkedList)에서 key값으로 검색되는 Node가 있는지 확인.
    *
    * */
    private fun searchKey(list: LinkedList<Node>?, key: String): Node? {
        //Null 저장된 적없는 값이라면 리턴.
        if (list == null) return null

        //list가 존재한다면 리스트 안에서 동일한 key가 존재하는지 확인 후 존재하면 Node리턴.
        for (n in list) {
            if (n.key.equals(key)) {
                return n
            }
        }

        //모두 검색했지만 없다면 Null
        return null
    }


    /* 값 넣기 */
    fun put(key: String, value: String) {
        //key를 기반으로 해시코드 생성
        val hashCode = getHashCode(key)
        //생성된 해시코드로 data(LinkedList<Node>[])의 인덱스를 변환
        val index = convertToIndex(hashCode)
        //data[index](LinkedList<Node>[])
        var list = data[index]
        //리스트가 첫번째 값이라면
        if (list == null) {
            list = LinkedList()
            //배열에 새로운 링크드리스트 생성.
            data[index] = list
        }
        //중복된 값이 있는지 확인.
        var node = searchKey(list, key)
        if (node == null) {
            //중복된 값이 없는경우 LinkedList<Node> 마지막에 새로운 Node<key, value>를 삽입
            list.addLast(Node(key, value))
        } else {
            //기존값이 있다면 value만 갱신
            node._value = value
        }

    }

    /* 값 찾기 */
    fun get(key: String): String? {
        //Key를 기반으로 HashCode 생성
        val hashCode = getHashCode(key)
        //hashCode를 기반으로 index 반환
        val index = convertToIndex(hashCode)
        //배열 data[index](LinkedList<Node>[])
        val list = data[index]
        //Node 찾기
        val node = searchKey(list, key)

        //값이 존재하면 반환 아니라면 Null
        return node?._value

    }


    //노드 클래스
    data class Node(var key: String, var _value: String)
}