package com.example.presentation.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    onNavigateBack: () -> Unit
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("حول تطبيق رسيل", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.raseel_icon_1790947523801),
                contentDescription = "Raseel App Icon",
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape)
            )

            Text(
                text = "رسيل (Raseel)",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "الإصدار v1.000 (Phase 3)",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }

            Text(
                text = "تطبيق مراسلة فوري حديث وآمن مصمم بأحدث تقنيات أندرويد وJetpack Compose مع دعم كامل للغة العربية والاتجاه من اليمين لليسار (RTL).",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Capabilities Card
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("أبرز إمكانيات التطبيق", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                    FeatureItem("محادثات فردية وجماعية فورية")
                    FeatureItem("تسجيل وتشغيل المقاطع الصوتية بـ Waveform تفاعلي")
                    FeatureItem("مشاركة الصور والمستندات مع عارض ملء الشاشة")
                    FeatureItem("تخصيص كامل للمظهر: سمات، ألوان، خلفيات، وأحجام الخطوط")
                    FeatureItem("تفاعل التعبيرات السريعة (Reactions) والردود والاقتباس")
                    FeatureItem("تعديل الرسائل وحذفها (لديك / لدى الجميع)")
                    FeatureItem("تثبيت المحادثات والرسائل وكتم التنبيهات")
                    FeatureItem("بحث فوري متقدم داخل المحادثات والرسائل وجهات الاتصال")
                    FeatureItem("نظام مسودات ذكي وحفظ تلقائي محلياً")
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "جميع الحقوق محفوظة © 2026 - Raseel Messenger",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun FeatureItem(title: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = title, style = MaterialTheme.typography.bodyMedium)
    }
}
