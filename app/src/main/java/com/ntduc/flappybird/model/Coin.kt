package com.ntduc.flappybird.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.ArrayList

@Parcelize
data class Coin(
    var id: Int,
    var coinRes: Int,
    var coinX: ArrayList<Int> = arrayListOf(),
    var coinY: ArrayList<Int> = arrayListOf(),
    var coinVelocity: Int = 8,                  //Vận tốc coin
    var coinShowing: ArrayList<Boolean> = arrayListOf()
) : Parcelable