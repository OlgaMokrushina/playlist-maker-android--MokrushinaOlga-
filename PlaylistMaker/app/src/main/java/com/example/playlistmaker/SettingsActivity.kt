package com.example.playlistmaker

import androidx.compose.material.icons.filled.Info
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.playlistmaker.ui.theme.PlaylistMakerTheme

class SettingsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            PlaylistMakerTheme {
                SettingsScreen(
                    onBack = { finish() },
                    onShareClick = { shareApp() },
                    onSupportClick = { writeToSupport() },
                    onAgreementClick = { openAgreement() }
                )
            }
        }
    }

    private fun shareApp() {
        val shareText = getString(R.string.share_message)

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }
        startActivity(Intent.createChooser(intent, null))
    }

    private fun writeToSupport() {
        val email = getString(R.string.student_email) // по ТЗ: email студента
        val subject = getString(R.string.email_subject)
        val body = getString(R.string.email_body)

        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:") // важно: именно mailto
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }
        startActivity(intent)
    }

    private fun openAgreement() {
        val url = getString(R.string.practicum_offer_url)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }
}

@Composable
private fun SettingsScreen(
    onBack: () -> Unit,
    onShareClick: () -> Unit,
    onSupportClick: () -> Unit,
    onAgreementClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        SettingsTopBar(onBack = onBack)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Тёмная тема
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 56.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Темная тема", fontSize = 16.sp)
                Switch(
                    checked = false,
                    onCheckedChange = { /* TODO позже */ }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            SettingsRow(
                text = stringResource(R.string.settings_share),
                icon = { Icon(Icons.Filled.Share, contentDescription = null) },
                showArrow = false,
                onClick = onShareClick
            )

            SettingsRow(
                text = stringResource(R.string.settings_support),
                icon = { Icon(Icons.Filled.Info, contentDescription = null) },
                showArrow = false,
                onClick = onSupportClick
            )

            SettingsRow(
                text = stringResource(R.string.settings_agreement),
                icon = { Icon(Icons.Filled.Info, contentDescription = null) },
                showArrow = true,
                onClick = onAgreementClick
            )
        }
    }
}

@Composable
private fun SettingsTopBar(onBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .height(56.dp)
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back"
            )
        }
        Text(
            text = stringResource(R.string.settings_title),
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun SettingsRow(
    text: String,
    icon: @Composable () -> Unit,
    showArrow: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon()
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            fontSize = 16.sp,
            modifier = Modifier.weight(1f)
        )
        if (showArrow) {
            Icon(
                imageVector = Icons.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color(0xFF9E9E9E)
            )
        }
    }
}
