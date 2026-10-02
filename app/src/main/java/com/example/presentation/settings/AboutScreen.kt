package com.example.presentation.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
                title = { Text("عن تطبيق dev", fontWeight = FontWeight.Bold) },
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
            // App Icon
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0F172A))
                    .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.dev_app_icon_1790954953633),
                    contentDescription = "dev App Icon",
                    modifier = Modifier.fillMaxSize()
                )
            }

            Text(
                text = "dev",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "الإصدار v2.000 • Customization Studio (90%)",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }

            Text(
                text = "تطبيق مراسلة فوري حديث وفائق التخصيص مصمم بأحدث معايير Jetpack Compose وMaterial 3 مع محرك ثيمات متقدم، مكتبة تضم 32+ ثيم و32+ خلفية ونظام تصميم فقاعات وأيقونات كامل.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            // Capabilities Card
            Card(
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("أبرز قدرات وميزات dev", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                    FeatureItem("مركز تخصيص متكامل يغير حتى 90% من الواجهة")
                    FeatureItem("مكتبة تحوي 32+ ثيم مجهز مسبقاً (AMOLED, Cyberpunk, Gaming, Glass...)")
                    FeatureItem("32+ خلفية ونمط تجريدي مع دعم الخلفيات الحية (Live Video Wallpaper)")
                    FeatureItem("10+ أنماط أيقونات مختلفة تغطي كافة عناصر التطبيق")
                    FeatureItem("Bubble Designer: تحكم كامل في شكل وحجم وانحناء وظلال الفقاعات")
                    FeatureItem("زر Surprise Me 🎲 لتوليد ثيمات متناسقة عشوائياً")
                    FeatureItem("تصدير واستيراد الثيمات بصيغة .devtheme المشفرة والآمنة")
                    FeatureItem("محادثات فورية فردية وجماعية ومقاطع صوتية ووسائط")
                    FeatureItem("دعم كامل للغة العربية والاتجاه من اليمين لليسار (RTL)")
                    FeatureItem("حفظ محلي فوري لكافة الإعدادات والتفضيلات بدون فقدانها")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "جميع الحقوق محفوظة © 2026 - dev Messaging & Design Engine",
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
