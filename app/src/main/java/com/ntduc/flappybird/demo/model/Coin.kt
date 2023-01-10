package com.ntduc.flappybird.demo.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.ArrayList

@Parcelize
data class Coin(
    var id: Int = 0,
    var coinRes: Int = 0,
    var coinX: ArrayList<Int> = arrayListOf(),
    var coinY: ArrayList<Int> = arrayListOf(),
    var coinVelocity: Int = 8,                  //Vận tốc coin
    var coinShowing: ArrayList<Boolean> = arrayListOf()
) : Parcelable