package com.example.finalandroidapplication.model

data class InterestQueueModel (
    val qID : String = "",
    val queue : List<String>  ,
    val houseID : String = "",
    val min : Int = 0 ,
    val max : Int = 0 ,
)
