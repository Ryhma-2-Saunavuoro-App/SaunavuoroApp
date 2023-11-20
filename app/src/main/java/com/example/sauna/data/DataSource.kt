
package com.example.sauna.data

import com.example.sauna.R

object DataSource {

    val sauna_types = listOf(
        Pair(R.string.ir_sauna, 1),
        Pair(R.string.sahko_sauna, 2), //sähkö sauna on 2x kalliimpi kuin ir sauna
        Pair(R.string.puu_sauna, 3) //puu sauna on 3x kalliimpi kuin ir sauna
    )

    val saunaDurationPrices = mapOf(
        R.string.fifteen_min to 5.00, // 15 min sauna costs $5.00
        R.string.thirty_min to 9.00, // 30 min sauna costs $9.00
        R.string.one_hour to 17.00, // 1 hour sauna costs $17.00
        R.string.two_hours to 30.00, // 2 hours sauna costs $30.00
        R.string.three_hours to 40.00 // 3 hours sauna costs $40.00
    )
}
