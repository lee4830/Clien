package com.jhapps.clien.`interface`.listener

import com.jhapps.clien.common.beans.ParsingData

interface ParsingListener {
    fun parsingComplete(data: ParsingData)
    fun parsingFailed(data: ParsingData)
}