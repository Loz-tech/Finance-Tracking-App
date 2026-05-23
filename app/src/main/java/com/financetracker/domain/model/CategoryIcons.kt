package com.financetracker.domain.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.BeachAccess
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Games
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.LocalGroceryStore
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Sports
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.BeachAccess
import androidx.compose.material.icons.outlined.Bolt
import androidx.compose.material.icons.outlined.Brush
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.ChildCare
import androidx.compose.material.icons.outlined.Computer
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.DirectionsBus
import androidx.compose.material.icons.outlined.DirectionsCar
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Flight
import androidx.compose.material.icons.outlined.Games
import androidx.compose.material.icons.outlined.Headphones
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalCafe
import androidx.compose.material.icons.outlined.LocalGroceryStore
import androidx.compose.material.icons.outlined.LocalHospital
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.Payment
import androidx.compose.material.icons.outlined.Pets
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.Savings
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material.icons.outlined.Sports
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.material.icons.outlined.Work
import androidx.compose.material.icons.rounded.AccountBalance
import androidx.compose.material.icons.rounded.AttachMoney
import androidx.compose.material.icons.rounded.BeachAccess
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.Brush
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.ChildCare
import androidx.compose.material.icons.rounded.Computer
import androidx.compose.material.icons.rounded.CreditCard
import androidx.compose.material.icons.rounded.DirectionsBus
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.Event
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.Flight
import androidx.compose.material.icons.rounded.Games
import androidx.compose.material.icons.rounded.Headphones
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LocalCafe
import androidx.compose.material.icons.rounded.LocalGroceryStore
import androidx.compose.material.icons.rounded.LocalHospital
import androidx.compose.material.icons.rounded.MoreHoriz
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.Payment
import androidx.compose.material.icons.rounded.Pets
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.Savings
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.material.icons.rounded.Sports
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material.icons.rounded.Work
import androidx.compose.ui.graphics.vector.ImageVector

object CategoryIcons {
    data class IconSet(val name: String, val filled: ImageVector, val outlined: ImageVector, val rounded: ImageVector)

    private val REGISTRY = listOf(
        IconSet("Restaurant", Icons.Filled.Restaurant, Icons.Outlined.Restaurant, Icons.Rounded.Restaurant),
        IconSet("DirectionsCar", Icons.Filled.DirectionsCar, Icons.Outlined.DirectionsCar, Icons.Rounded.DirectionsCar),
        IconSet("Home", Icons.Filled.Home, Icons.Outlined.Home, Icons.Rounded.Home),
        IconSet("Movie", Icons.Filled.Movie, Icons.Outlined.Movie, Icons.Rounded.Movie),
        IconSet("ShoppingCart", Icons.Filled.ShoppingCart, Icons.Outlined.ShoppingCart, Icons.Rounded.ShoppingCart),
        IconSet("LocalHospital", Icons.Filled.LocalHospital, Icons.Outlined.LocalHospital, Icons.Rounded.LocalHospital),
        IconSet("School", Icons.Filled.School, Icons.Outlined.School, Icons.Rounded.School),
        IconSet("MoreHoriz", Icons.Filled.MoreHoriz, Icons.Outlined.MoreHoriz, Icons.Rounded.MoreHoriz),
        IconSet("AttachMoney", Icons.Filled.AttachMoney, Icons.Outlined.AttachMoney, Icons.Rounded.AttachMoney),
        IconSet("Favorite", Icons.Filled.Favorite, Icons.Outlined.Favorite, Icons.Rounded.Favorite),
        IconSet("FitnessCenter", Icons.Filled.FitnessCenter, Icons.Outlined.FitnessCenter, Icons.Rounded.FitnessCenter),
        IconSet("Flight", Icons.Filled.Flight, Icons.Outlined.Flight, Icons.Rounded.Flight),
        IconSet("Computer", Icons.Filled.Computer, Icons.Outlined.Computer, Icons.Rounded.Computer),
        IconSet("PhoneAndroid", Icons.Filled.PhoneAndroid, Icons.Outlined.PhoneAndroid, Icons.Rounded.PhoneAndroid),
        IconSet("Wifi", Icons.Filled.Wifi, Icons.Outlined.Wifi, Icons.Rounded.Wifi),
        IconSet("Bolt", Icons.Filled.Bolt, Icons.Outlined.Bolt, Icons.Rounded.Bolt),
        IconSet("WaterDrop", Icons.Filled.WaterDrop, Icons.Outlined.WaterDrop, Icons.Rounded.WaterDrop),
        IconSet(
            "AccountBalance",
            Icons.Filled.AccountBalance,
            Icons.Outlined.AccountBalance,
            Icons.Rounded.AccountBalance
        ),
        IconSet("CreditCard", Icons.Filled.CreditCard, Icons.Outlined.CreditCard, Icons.Rounded.CreditCard),
        IconSet("TrendingUp", Icons.Filled.TrendingUp, Icons.Outlined.TrendingUp, Icons.Rounded.TrendingUp),
        IconSet("Work", Icons.Filled.Work, Icons.Outlined.Work, Icons.Rounded.Work),
        IconSet("Pets", Icons.Filled.Pets, Icons.Outlined.Pets, Icons.Rounded.Pets),
        IconSet("ChildCare", Icons.Filled.ChildCare, Icons.Outlined.ChildCare, Icons.Rounded.ChildCare),
        IconSet("Spa", Icons.Filled.Spa, Icons.Outlined.Spa, Icons.Rounded.Spa),
        IconSet("Brush", Icons.Filled.Brush, Icons.Outlined.Brush, Icons.Rounded.Brush),
        IconSet("Build", Icons.Filled.Build, Icons.Outlined.Build, Icons.Rounded.Build),
        IconSet("Payment", Icons.Filled.Payment, Icons.Outlined.Payment, Icons.Rounded.Payment),
        IconSet("Games", Icons.Filled.Games, Icons.Outlined.Games, Icons.Rounded.Games),
        IconSet("LocalCafe", Icons.Filled.LocalCafe, Icons.Outlined.LocalCafe, Icons.Rounded.LocalCafe),
        IconSet("DirectionsBus", Icons.Filled.DirectionsBus, Icons.Outlined.DirectionsBus, Icons.Rounded.DirectionsBus),
        IconSet(
            "LocalGroceryStore",
            Icons.Filled.LocalGroceryStore,
            Icons.Outlined.LocalGroceryStore,
            Icons.Rounded.LocalGroceryStore
        ),
        IconSet("Event", Icons.Filled.Event, Icons.Outlined.Event, Icons.Rounded.Event),
        IconSet("BeachAccess", Icons.Filled.BeachAccess, Icons.Outlined.BeachAccess, Icons.Rounded.BeachAccess),
        IconSet("Savings", Icons.Filled.Savings, Icons.Outlined.Savings, Icons.Rounded.Savings),
        IconSet("Headphones", Icons.Filled.Headphones, Icons.Outlined.Headphones, Icons.Rounded.Headphones),
        IconSet("Sports", Icons.Filled.Sports, Icons.Outlined.Sports, Icons.Rounded.Sports)
    ).associateBy { it.name }

    fun resolve(iconName: String, style: IconStyle): ImageVector {
        val iconSet = REGISTRY[iconName]
        return when (style) {
            IconStyle.FILLED -> iconSet?.filled
            IconStyle.OUTLINED -> iconSet?.outlined
            IconStyle.ROUNDED -> iconSet?.rounded
        } ?: Icons.Filled.Help
    }

    fun allIcons(): List<IconSet> = REGISTRY.values.toList()
}
