package com.ntduc.flappybird.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.ArrayList

@Parcelize
data class Tube(
    var id: Int = 0,
    var tubeTopRes: Int = 0,
    var tubeBottomRes: Int = 0,
    var gap: Int = 400,                         //Khoảng cách giữa tube trên và dưới
    var distanceBetweenTubes: Int = 0,          //Khoảng cách giữa các tube
    var minTubeOffset: Int = 200,               // = gap/2
    var maxTubeOffset: Int = 0,
    var tubeX: ArrayList<Int> = arrayListOf(),
    var topTubeY: ArrayList<Int> = arrayListOf(),
    var tubeVelocity: Int = 8,                  //Vận tốc tube
    var tubeMove: ArrayList<Int> = arrayListOf(),
    var tubeMoveDefault: Int = 2
) : Parcelable