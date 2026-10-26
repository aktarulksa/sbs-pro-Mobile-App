package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Country
import com.example.model.Operator
import com.example.model.ServiceType
import com.example.ui.components.CountryFlag
import com.example.ui.components.OperatorBadge
import com.example.ui.components.ServiceRow

@Composable
fun RechargeScreen(
    countries: List<Country>,
    selectedCountry: Country?,
    operators: List<Operator>,
    selectedService: ServiceType,
    onServiceSelected: (ServiceType) -> Unit,
    onCountrySelected: (Country) -> Unit,
    onBackToCountries: () -> Unit,
    onOperatorSelected: (Operator) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredCountries = remember(searchQuery, countries) {
        if (searchQuery.isBlank()) countries
        else countries.filter { it.name.contains(searchQuery, ignoreCase = true) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFEEEEEE))
    ) {
        // Horizontal Service Type Selector matching Screenshot 1 & 4
        ServiceRow(
            selectedService = selectedService,
            onServiceSelected = onServiceSelected
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Main White Card Content area matching Screenshots 1 & 4
        Card(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            shape = RoundedCornerShape(4.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = CardDefaults.outlinedCardBorder()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                if (selectedCountry == null) {
                    // SCREENSHOT 1: "Choose Country:"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Choose Country:",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF424242),
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        // Quick search field
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Filter", fontSize = 12.sp) },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = "Search",
                                    modifier = Modifier.size(16.dp),
                                    tint = Color.Gray
                                )
                            },
                            singleLine = true,
                            modifier = Modifier
                                .width(140.dp)
                                .height(46.dp),
                            textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp),
                            shape = RoundedCornerShape(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("country_grid"),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(filteredCountries, key = { it.id }) { country ->
                            CountryGridItem(
                                country = country,
                                onClick = { onCountrySelected(country) }
                            )
                        }
                    }
                } else {
                    // SCREENSHOT 4: "Bangladesh > Choose Operator:"
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onBackToCountries,
                            modifier = Modifier
                                .testTag("back_to_countries")
                                .size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color(0xFF424242)
                            )
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        Text(
                            text = "${selectedCountry.name} > Choose Operator:",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF424242)
                        )
                    }

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("operator_grid"),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(operators, key = { it.id }) { operator ->
                            OperatorBadge(
                                operator = operator,
                                onClick = { onOperatorSelected(operator) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CountryGridItem(
    country: Country,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .testTag("country_${country.id}")
            .clickable(onClick = onClick)
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CountryFlag(
            flagId = country.flagId,
            size = 58.dp
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = country.name,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF006A4E), // Green label matching Bangladesh in Screenshot 1
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}
