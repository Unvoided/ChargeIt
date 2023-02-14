package com.unvoided.chargeit.pages.subpages

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.unvoided.chargeit.data.Station

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoTab(station: Station) {
    station.usageCost?.let {
        ListItem(headlineText = {
            Text("Usage Cost", fontWeight = FontWeight.Bold)
        }, supportingText = {
            Text(
                modifier = Modifier.padding(5.dp),
                text = it,
                style = MaterialTheme.typography.labelLarge
            )
        })
    }
    Divider()
    station.numberOfPoints?.let {
        ListItem(
            headlineText = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Number of Points",
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.size(10.dp))
                    Badge(containerColor = MaterialTheme.colorScheme.primary) {
                        Text(text = "$it")
                    }
                }
            },
        )
    }
    Divider()
    station.operatorInfo?.let {
        if (it.contactEmail != null || it.phonePrimaryContact != null) ListItem(headlineText = {
            Text("Contact", fontWeight = FontWeight.Bold)
        }, supportingText = {
            Row(modifier = Modifier.padding(5.dp)) {
                it.phonePrimaryContact?.let { number ->

                    Text(
                        text = "Phone number:", fontWeight = FontWeight.Bold
                    )
                    Text(text = " $number")

                }
                it.contactEmail?.let { email ->
                    Text(
                        text = "Email:", fontWeight = FontWeight.Bold
                    )
                    Text(text = " $email")
                }
            }

        })
    }

}