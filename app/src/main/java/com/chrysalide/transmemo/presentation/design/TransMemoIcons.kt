package com.chrysalide.transmemo.presentation.design

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.DateRange
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.Settings
import dev.sergiobelda.compose.vectorize.images.Images
import dev.sergiobelda.compose.vectorize.images.icons.filled.Facebook
import dev.sergiobelda.compose.vectorize.images.icons.filled.FactCheck
import dev.sergiobelda.compose.vectorize.images.icons.filled.HeartCheck
import dev.sergiobelda.compose.vectorize.images.icons.filled.Help
import dev.sergiobelda.compose.vectorize.images.icons.filled.Medication
import dev.sergiobelda.compose.vectorize.images.icons.filled.Vaccines
import dev.sergiobelda.compose.vectorize.images.icons.filled.VolunteerActivism
import dev.sergiobelda.compose.vectorize.images.icons.outlined.Group
import dev.sergiobelda.compose.vectorize.images.icons.outlined.HeartCheck
import dev.sergiobelda.compose.vectorize.images.icons.outlined.Inventory
import dev.sergiobelda.compose.vectorize.images.icons.outlined.Medication
import dev.sergiobelda.compose.vectorize.images.icons.outlined.Monitoring
import dev.sergiobelda.compose.vectorize.images.icons.outlined.Vaccines

object TransMemoIcons {
    val Menu = Icons.Rounded.Menu
    val Calendar = Icons.Rounded.DateRange
    val CalendarUnselected = Icons.Outlined.DateRange
    val Intakes = Images.Icons.Filled.Vaccines
    val IntakesUnselected = Images.Icons.Outlined.Vaccines
    val Inventory = Images.Icons.Outlined.Inventory
    val InventoryUnselected = Images.Icons.Outlined.Inventory
    val Products = Images.Icons.Filled.Medication
    val ProductsUnselected = Images.Icons.Outlined.Medication
    val Wellbeing = Images.Icons.Filled.HeartCheck
    val WellbeingUnselected = Images.Icons.Outlined.HeartCheck
    val Statistics = Images.Icons.Outlined.Monitoring
    val StatisticsUnselected = Images.Icons.Outlined.Monitoring
    val Settings = Icons.Rounded.Settings
    val SettingsUnselected = Icons.Outlined.Settings
    val About = Icons.Rounded.Info
    val AboutUnselected = Icons.Outlined.Info

    val Help = Images.Icons.Filled.Help
    val HelpUs = Images.Icons.Filled.VolunteerActivism
    val Facebook = Images.Icons.Filled.Facebook
    val Contributors = Images.Icons.Outlined.Group
    val Licenses = Images.Icons.Filled.FactCheck
    val Back = Icons.AutoMirrored.Filled.ArrowBack
}
