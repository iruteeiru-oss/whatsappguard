package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ChatLog
import com.example.data.MediaItem
import com.example.data.DeviceStatus
import com.example.ui.theme.BorderColor
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.StatusOnline
import com.example.ui.theme.StatusOffline
import com.example.ui.theme.StatusWarning
import com.example.ui.theme.TealAccent
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonitorApp(viewModel: MonitorViewModel) {
    val currentRole by viewModel.currentRole.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val chatLogs by viewModel.chatLogs.collectAsState()
    val mediaItems by viewModel.mediaItems.collectAsState()
    val deviceStatus by viewModel.deviceStatus.collectAsState()
    val childActiveScreen by viewModel.childActiveScreen.collectAsState()

    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.primary
                ),
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Shield Logo",
                            tint = TealAccent,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = "WhatsGuard Monitor",
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.SansSerif,
                            letterSpacing = 0.5.sp,
                            fontSize = 20.sp
                        )
                    }
                },
                actions = {
                    // Role Toggle in Toolbar
                    Row(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .background(DarkSurfaceVariant, RoundedCornerShape(20.dp))
                            .padding(2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(18.dp))
                                .background(if (currentRole == "parent") TealPrimary else Color.Transparent)
                                .clickable { viewModel.setRole("parent") }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                .testTag("parent_role_button")
                        ) {
                            Text(
                                "Parent Mode",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (currentRole == "parent") Color.Black else TextSecondary
                            )
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(18.dp))
                                .background(if (currentRole == "child") TealPrimary else Color.Transparent)
                                .clickable { viewModel.setRole("child") }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                                .testTag("child_role_button")
                        ) {
                            Text(
                                "Child Mode",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (currentRole == "child") Color.Black else TextSecondary
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (currentRole == "parent") {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = selectedTab == "dashboard",
                        onClick = { viewModel.setSelectedTab("dashboard") },
                        label = { Text("Dashboard") },
                        icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") }
                    )
                    NavigationBarItem(
                        selected = selectedTab == "chats",
                        onClick = { viewModel.setSelectedTab("chats") },
                        label = { Text("Chats") },
                        icon = { Icon(Icons.Default.Chat, contentDescription = "Chats") }
                    )
                    NavigationBarItem(
                        selected = selectedTab == "calls",
                        onClick = { viewModel.setSelectedTab("calls") },
                        label = { Text("Calls") },
                        icon = { Icon(Icons.Default.Phone, contentDescription = "Calls") }
                    )
                    NavigationBarItem(
                        selected = selectedTab == "media",
                        onClick = { viewModel.setSelectedTab("media") },
                        label = { Text("Media Retriever") },
                        icon = { Icon(Icons.Default.Download, contentDescription = "Media Retriever") }
                    )
                    NavigationBarItem(
                        selected = selectedTab == "setup",
                        onClick = { viewModel.setSelectedTab("setup") },
                        label = { Text("Setup") },
                        icon = { Icon(Icons.Default.Build, contentDescription = "Setup") }
                    )
                }
            }
        },
        contentWindowInsets = WindowInsets.safeDrawing
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            if (currentRole == "parent") {
                when (selectedTab) {
                    "dashboard" -> ParentDashboardView(viewModel, deviceStatus)
                    "chats" -> ParentChatsView(viewModel, chatLogs)
                    "calls" -> ParentCallsView(viewModel)
                    "media" -> ParentMediaView(viewModel, mediaItems)
                    "setup" -> SetupWizardView(viewModel)
                }
            } else {
                ChildSimulationView(viewModel, chatLogs)
            }
        }
    }
}

// ==========================================
// PARENT: DASHBOARD VIEW
// ==========================================
@Composable
fun ParentDashboardView(viewModel: MonitorViewModel, status: DeviceStatus?) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Live Connection Status",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(12.dp)
                                    .clip(CircleShape)
                                    .background(if (status?.isOnline == true) StatusOnline else StatusOffline)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                if (status?.isOnline == true) "ONLINE" else "OFFLINE",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (status?.isOnline == true) StatusOnline else StatusOffline
                            )
                        }
                        Text(
                            "Stealth Active",
                            fontSize = 12.sp,
                            color = TealAccent,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .border(1.dp, TealAccent, RoundedCornerShape(12.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // Real-time telemetry: Battery and Charging
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Battery Status",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Custom Battery Ring Progress
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.size(80.dp)
                        ) {
                            val batteryPct = status?.batteryPercentage ?: 85
                            Canvas(modifier = Modifier.size(80.dp)) {
                                drawArc(
                                    color = Color(0xFF1E293B),
                                    startAngle = 0f,
                                    sweepAngle = 360f,
                                    useCenter = false,
                                    style = Stroke(width = 8.dp.toPx())
                                )
                                drawArc(
                                    color = if (batteryPct > 20) StatusOnline else StatusOffline,
                                    startAngle = -90f,
                                    sweepAngle = (batteryPct * 3.6f),
                                    useCenter = false,
                                    style = Stroke(width = 8.dp.toPx())
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                if (status?.isCharging == true) {
                                    Icon(
                                        imageVector = Icons.Default.Bolt,
                                        contentDescription = "Charging",
                                        tint = StatusOnline,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Text(
                                    "$batteryPct%",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            if (status?.isCharging == true) "Charging" else "On Battery",
                            fontSize = 13.sp,
                            color = if (status?.isCharging == true) StatusOnline else TextSecondary
                        )
                    }
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = DarkSurface),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Device Stealth Mode",
                            fontSize = 12.sp,
                            color = TextSecondary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Icon(
                            imageVector = if (status?.stealthModeEnabled == true) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Stealth Mode Icon",
                            tint = if (status?.stealthModeEnabled == true) TealAccent else TextSecondary,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.toggleStealthMode() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (status?.stealthModeEnabled == true) TealPrimary else DarkSurfaceVariant
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                if (status?.stealthModeEnabled == true) "Disable" else "Enable",
                                color = if (status?.stealthModeEnabled == true) Color.Black else TextPrimary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // Privacy-First & Permissions Audit Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Live Location Tracking",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TealAccent
                        )
                        Box(
                            modifier = Modifier
                                .background(StatusOnline.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(StatusOnline, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "GPS LIVE",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = StatusOnline
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Tracking child's physical coordinates in real-time under parent supervision.",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Tactical Radar / Map Canvas Grid
                    val lat = status?.locationLatitude ?: 37.7749
                    val lng = status?.locationLongitude ?: -122.4194
                    
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF0B132B)) // High-tech deep space blue
                            .border(1.dp, TealAccent.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height
                            val cx = w / 2
                            val cy = h / 2
                            
                            // Draw Grid Lines
                            val gridSpacing = 30.dp.toPx()
                            for (x in 0..(w / gridSpacing).toInt()) {
                                drawLine(
                                    color = Color(0xFF1C2541).copy(alpha = 0.5f),
                                    start = androidx.compose.ui.geometry.Offset(x * gridSpacing, 0f),
                                    end = androidx.compose.ui.geometry.Offset(x * gridSpacing, h),
                                    strokeWidth = 1f
                                )
                            }
                            for (y in 0..(h / gridSpacing).toInt()) {
                                drawLine(
                                    color = Color(0xFF1C2541).copy(alpha = 0.5f),
                                    start = androidx.compose.ui.geometry.Offset(0f, y * gridSpacing),
                                    end = androidx.compose.ui.geometry.Offset(w, y * gridSpacing),
                                    strokeWidth = 1f
                                )
                            }
                            
                            // Draw Concentric Radar Rings
                            val maxRadius = Math.min(w, h) / 1.5f
                            drawCircle(
                                color = TealAccent.copy(alpha = 0.15f),
                                radius = maxRadius / 3,
                                center = androidx.compose.ui.geometry.Offset(cx, cy),
                                style = Stroke(width = 1.dp.toPx())
                            )
                            drawCircle(
                                color = TealAccent.copy(alpha = 0.1f),
                                radius = maxRadius / 1.5f,
                                center = androidx.compose.ui.geometry.Offset(cx, cy),
                                style = Stroke(width = 1.dp.toPx())
                            )
                            drawCircle(
                                color = TealAccent.copy(alpha = 0.05f),
                                radius = maxRadius,
                                center = androidx.compose.ui.geometry.Offset(cx, cy),
                                style = Stroke(width = 1.dp.toPx())
                            )
                            
                            // Draw Map Roads Simulation (decorative)
                            drawLine(
                                color = Color(0xFF3A506B).copy(alpha = 0.4f),
                                start = androidx.compose.ui.geometry.Offset(0f, cy - 20.dp.toPx()),
                                end = androidx.compose.ui.geometry.Offset(w, cy + 30.dp.toPx()),
                                strokeWidth = 3.dp.toPx()
                            )
                            drawLine(
                                color = Color(0xFF3A506B).copy(alpha = 0.4f),
                                start = androidx.compose.ui.geometry.Offset(cx - 50.dp.toPx(), 0f),
                                end = androidx.compose.ui.geometry.Offset(cx + 40.dp.toPx(), h),
                                strokeWidth = 3.dp.toPx()
                            )
                            
                            // Draw Central Glowing Marker
                            drawCircle(
                                color = StatusOnline.copy(alpha = 0.25f),
                                radius = 24.dp.toPx(),
                                center = androidx.compose.ui.geometry.Offset(cx, cy)
                            )
                            drawCircle(
                                color = StatusOnline.copy(alpha = 0.6f),
                                radius = 10.dp.toPx(),
                                center = androidx.compose.ui.geometry.Offset(cx, cy)
                            )
                            drawCircle(
                                color = Color.White,
                                radius = 4.dp.toPx(),
                                center = androidx.compose.ui.geometry.Offset(cx, cy)
                            )
                        }
                        
                        // Compass Card overlay
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .background(Color(0xFF0F172A).copy(alpha = 0.85f), RoundedCornerShape(8.dp))
                                .border(0.5.dp, TealAccent.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                "COMPASS N-NE",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = TealAccent
                            )
                        }
                        
                        // Signal Strength / Accuracy overlay
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(8.dp)
                                .background(Color(0xFF0F172A).copy(alpha = 0.85f), RoundedCornerShape(8.dp))
                                .border(0.5.dp, TealAccent.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                "GPS ACCURACY: ±4.8m",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = StatusOnline
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Coordinates Text Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "CURRENT COORDINATES",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary
                            )
                            Text(
                                String.format(Locale.US, "%.5f, %.5f", lat, lng),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                        
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                "ACTIVE ZONE",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary
                            )
                            Text(
                                "San Francisco, CA",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                    }
                }
            }
        }

        // Safety-Consent System Security
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Safety-Consent System Security",
                            fontSize = 14.sp,
                            color = TealAccent,
                            fontWeight = FontWeight.Bold
                        )
                        Box(
                            modifier = Modifier
                                .background(TealAccent.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                "PARENTAL CONSENT ACTIVE",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = TealAccent
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "WhatsGuard operates with explicit parental consent to protect children from danger, logging key security vectors under strict COPPA standards.",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Audit Rows
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        PermissionAuditItem(
                            permissionName = "Live Location Tracking (GPS)",
                            status = "ACTIVE (Secure)",
                            statusColor = StatusOnline,
                            description = "GPS tracking is fully enabled. Keeps track of current locations and history safely.",
                            icon = Icons.Default.LocationOn
                        )
                        PermissionAuditItem(
                            permissionName = "SMS Interception (MMS/Text)",
                            status = "ACTIVE (Secure)",
                            statusColor = StatusOnline,
                            description = "Monitors child text messages for distress signs, bullying, or security codes.",
                            icon = Icons.Default.Chat
                        )
                        PermissionAuditItem(
                            permissionName = "Phone Call Records & Logs",
                            status = "ACTIVE (Secure)",
                            statusColor = StatusOnline,
                            description = "Logs dialer calls and incoming/outgoing numbers to detect stranger dangers.",
                            icon = Icons.Default.Phone
                        )
                        PermissionAuditItem(
                            permissionName = "Local Media Folder Crawler",
                            status = "ACTIVE (Secure)",
                            statusColor = StatusOnline,
                            description = "Crawls non-sensitive local cached folders to retrieve incoming/outgoing images.",
                            icon = Icons.Default.Folder
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PermissionAuditItem(
    permissionName: String,
    status: String,
    statusColor: Color,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(DarkSurfaceVariant, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF0F172A)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = permissionName,
                tint = statusColor,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = permissionName,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = status,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = statusColor
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                fontSize = 11.sp,
                color = TextSecondary
            )
        }
    }
}

// ==========================================
// PARENT: CHATS MONITORING VIEW
// ==========================================
@Composable
fun ParentChatsView(viewModel: MonitorViewModel, chatLogs: List<ChatLog>) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedAppFilter by remember { mutableStateOf("All") }

    val appColors = mapOf(
        "WhatsApp" to Color(0xFF00BFA5),
        "Instagram" to Color(0xFFE1306C),
        "Snapchat" to Color(0xFFFFE000),
        "SMS" to Color(0xFF60A5FA)
    )

    val filteredLogs = chatLogs.filter { log ->
        (selectedAppFilter == "All" || log.appSource == selectedAppFilter) && (
            log.contactName.contains(searchQuery, ignoreCase = true) || 
            log.messageText.contains(searchQuery, ignoreCase = true)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Search & Reset Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search logs by contact/message...", color = TextSecondary, fontSize = 14.sp) },
                modifier = Modifier.weight(1f),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = DarkSurface,
                    unfocusedContainerColor = DarkSurface,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(12.dp),
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextSecondary)
                        }
                    } else {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = TextSecondary)
                    }
                }
            )
            
            IconButton(
                onClick = { viewModel.resetAllData() },
                modifier = Modifier
                    .background(DarkSurface, RoundedCornerShape(12.dp))
                    .size(56.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Reset Logs", tint = TealAccent)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Platform Filter Chips
        Text(
            "Filter Platform Source",
            fontSize = 12.sp,
            color = TextSecondary,
            fontWeight = FontWeight.Bold
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("All", "WhatsApp", "Instagram", "Snapchat", "SMS").forEach { app ->
                val isSelected = selectedAppFilter == app
                val color = if (app == "All") TealAccent else appColors[app] ?: TealAccent
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) color else DarkSurface)
                        .border(1.dp, if (isSelected) color else color.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                        .clickable { selectedAppFilter = app }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = app,
                        color = if (isSelected) Color.Black else TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            "Captured Live Transcripts",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (filteredLogs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.ChatBubbleOutline,
                        contentDescription = "No Chats",
                        tint = TextSecondary,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "No captured chat logs found.",
                        fontSize = 14.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredLogs) { log ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth().border(
                            width = 1.dp,
                            color = (appColors[log.appSource] ?: TealAccent).copy(alpha = 0.3f),
                            shape = RoundedCornerShape(14.dp)
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    val appColor = appColors[log.appSource] ?: TealAccent
                                    Icon(
                                        imageVector = if (log.isOutgoing) Icons.Default.CallMade else Icons.Default.CallReceived,
                                        contentDescription = "Direction",
                                        tint = appColor,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = log.contactName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = TextPrimary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    // App badge
                                    Box(
                                        modifier = Modifier
                                            .background(appColor.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                            .padding(horizontal = 5.dp, vertical = 1.dp)
                                    ) {
                                        Text(
                                            text = log.appSource,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = appColor
                                        )
                                    }
                                }
                                
                                // Tag indicating detection method: ScreenReader or OCR Fallback
                                val detectionTag = log.detectionMethod
                                Text(
                                    text = detectionTag,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (detectionTag == "OCR") Color.Black else TextSecondary,
                                    modifier = Modifier
                                        .background(
                                            if (detectionTag == "OCR") TealAccent else DarkSurfaceVariant,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = log.messageText,
                                fontSize = 14.sp,
                                color = TextPrimary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = SimpleDateFormat("MMM d, yyyy - hh:mm a", Locale.getDefault()).format(Date(log.timestamp)),
                                fontSize = 11.sp,
                                color = TextSecondary,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.End
                            )
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// PARENT: PHONE CALLS MONITORING VIEW
// ==========================================
@Composable
fun ParentCallsView(viewModel: MonitorViewModel) {
    val callLogs by viewModel.callLogs.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    val filteredCalls = callLogs.filter { call ->
        searchQuery.isEmpty() ||
        call.contactName.contains(searchQuery, ignoreCase = true) ||
        call.phoneNumber.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Search & Refresh Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search phone call logs...", color = TextSecondary, fontSize = 14.sp) },
                modifier = Modifier.weight(1f),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = DarkSurface,
                    unfocusedContainerColor = DarkSurface,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(12.dp),
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextSecondary)
                        }
                    } else {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = TextSecondary)
                    }
                }
            )
            
            IconButton(
                onClick = { viewModel.resetAllData() },
                modifier = Modifier
                    .background(DarkSurface, RoundedCornerShape(12.dp))
                    .size(56.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Reset Logs", tint = TealAccent)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Parental Compliance Card
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkSurfaceVariant.copy(alpha = 0.5f)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, TealAccent.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Consent active",
                    tint = TealAccent,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "COPPA Consent Verification Active: Only logging direct call durations and dialer contacts.",
                    fontSize = 11.sp,
                    color = TextPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Captured Live Call History",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (filteredCalls.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = "No Calls",
                        tint = TextSecondary,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "No phone call logs captured.",
                        fontSize = 14.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredCalls) { call ->
                    val callColor = when (call.callType) {
                        "Incoming" -> StatusOnline
                        "Outgoing" -> Color(0xFF60A5FA)
                        else -> StatusWarning
                    }
                    val callIcon = when (call.callType) {
                        "Incoming" -> Icons.Default.CallReceived
                        "Outgoing" -> Icons.Default.CallMade
                        else -> Icons.Default.Phone
                    }

                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = callColor.copy(alpha = 0.25f),
                                shape = RoundedCornerShape(14.dp)
                            )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(callColor.copy(alpha = 0.15f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = callIcon,
                                        contentDescription = call.callType,
                                        tint = callColor,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = call.contactName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = TextPrimary
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = call.phoneNumber,
                                        fontSize = 12.sp,
                                        color = TextSecondary
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Box(
                                    modifier = Modifier
                                        .background(callColor.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = call.callType,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = callColor
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (call.callType == "Missed") "No answer" else "${call.durationSeconds}s duration",
                                    fontSize = 11.sp,
                                    color = TextSecondary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(call.timestamp)),
                                    fontSize = 10.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// PARENT: ON-DEMAND MEDIA RETRIEVER VIEW
// ==========================================
@Composable
fun ParentMediaView(viewModel: MonitorViewModel, mediaItems: List<MediaItem>) {
    var selectedAppFilter by remember { mutableStateOf("All") }

    val appColors = mapOf(
        "WhatsApp" to Color(0xFF00BFA5),
        "Instagram" to Color(0xFFE1306C),
        "Snapchat" to Color(0xFFFFE000)
    )

    val filteredMedia = mediaItems.filter { item ->
        selectedAppFilter == "All" || item.appSource == selectedAppFilter
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Speed,
                        contentDescription = "Optimize",
                        tint = TealAccent,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(
                        "Lightweight Media Indexing",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Only metadata (filenames and sizes) is indexed to minimize cellular data and storage usage on the child's device. Download actual files strictly on-demand below.",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Filter Platform Chips
        Text(
            "Filter Platform Source",
            fontSize = 12.sp,
            color = TextSecondary,
            fontWeight = FontWeight.Bold
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("All", "WhatsApp", "Instagram", "Snapchat").forEach { app ->
                val isSelected = selectedAppFilter == app
                val color = if (app == "All") TealAccent else appColors[app] ?: TealAccent
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) color else DarkSurface)
                        .border(1.dp, if (isSelected) color else color.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                        .clickable { selectedAppFilter = app }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = app,
                        color = if (isSelected) Color.Black else TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = if (selectedAppFilter == "All") "Available Indexed Files" else "Available $selectedAppFilter Files",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (filteredMedia.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No indexed media found for $selectedAppFilter.",
                    fontSize = 14.sp,
                    color = TextSecondary
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredMedia) { item ->
                    val appColor = appColors[item.appSource] ?: TealAccent
                    Card(
                        colors = CardDefaults.cardColors(containerColor = DarkSurface),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth().border(
                            width = 1.dp,
                            color = appColor.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(14.dp)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // File type Icon selection
                            val icon = when {
                                item.mimeType.contains("pdf") -> Icons.Default.Description
                                item.mimeType.contains("image") -> Icons.Default.Photo
                                item.mimeType.contains("video") -> Icons.Default.PlayCircle
                                else -> Icons.Default.VolumeUp
                            }
                            
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(DarkSurfaceVariant),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(imageVector = icon, contentDescription = "File Icon", tint = appColor)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = item.fileName,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary,
                                            maxLines = 1,
                                            modifier = Modifier.weight(1f, fill = false)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .background(appColor.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                                .padding(horizontal = 4.dp, vertical = 1.dp)
                                        ) {
                                            Text(
                                                text = item.appSource,
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = appColor
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${String.format(Locale.US, "%.1f", item.fileSize / 1024000.0)} MB • ${item.direction}",
                                        fontSize = 11.sp,
                                        color = TextSecondary
                                    )
                                }
                            }

                            // Retrieve / Open action button
                            Box(modifier = Modifier.padding(start = 8.dp)) {
                                when (item.retrievalStatus) {
                                    "NotRequested" -> {
                                        Button(
                                            onClick = { viewModel.requestMediaDownload(item.id) },
                                            colors = ButtonDefaults.buttonColors(containerColor = appColor),
                                            shape = RoundedCornerShape(10.dp),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                            modifier = Modifier.testTag("request_media_button")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.FileDownload,
                                                contentDescription = "Request Download",
                                                tint = Color.Black,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Request", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    "Requested" -> {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            CircularProgressIndicator(
                                                color = appColor,
                                                strokeWidth = 2.dp,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Fetching...", fontSize = 11.sp, color = appColor, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                    "Retrieved" -> {
                                        var showFileViewDialog by remember { mutableStateOf(false) }
                                        Button(
                                            onClick = { showFileViewDialog = true },
                                            colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                                            shape = RoundedCornerShape(10.dp),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Visibility,
                                                contentDescription = "View File",
                                                tint = appColor,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("View File", color = appColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }

                                        // Simple custom viewer dialog for the requested/retrieved file
                                        if (showFileViewDialog) {
                                            AlertDialog(
                                                onDismissRequest = { showFileViewDialog = false },
                                                title = { Text(item.fileName, color = appColor, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
                                                text = {
                                                    Column(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalAlignment = Alignment.CenterHorizontally
                                                    ) {
                                                        if (item.mimeType.contains("image")) {
                                                            Box(
                                                                modifier = Modifier
                                                                    .fillMaxWidth()
                                                                    .height(180.dp)
                                                                    .clip(RoundedCornerShape(12.dp))
                                                                    .background(Color(0xFF1E293B)),
                                                                contentAlignment = Alignment.Center
                                                            ) {
                                                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                                    Icon(
                                                                        imageVector = Icons.Default.Photo,
                                                                        contentDescription = "Loaded Image",
                                                                        tint = appColor,
                                                                        modifier = Modifier.size(64.dp)
                                                                    )
                                                                    Text("Simulated Image Retrieved", color = TextSecondary, fontSize = 12.sp)
                                                                }
                                                            }
                                                        } else {
                                                            Box(
                                                                modifier = Modifier
                                                                    .fillMaxWidth()
                                                                    .height(140.dp)
                                                                    .clip(RoundedCornerShape(12.dp))
                                                                    .background(Color(0xFF1E293B)),
                                                                contentAlignment = Alignment.Center
                                                            ) {
                                                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                                    Icon(
                                                                        imageVector = icon,
                                                                        contentDescription = "Loaded Doc",
                                                                        tint = appColor,
                                                                        modifier = Modifier.size(56.dp)
                                                                    )
                                                                    Spacer(modifier = Modifier.height(8.dp))
                                                                    Text("Document Preview Safe", color = TextSecondary, fontSize = 12.sp)
                                                                }
                                                            }
                                                        }
                                                        Spacer(modifier = Modifier.height(12.dp))
                                                        Text(
                                                            "Storage Location: ${item.localPath}",
                                                            fontSize = 11.sp,
                                                            color = TextSecondary,
                                                            fontFamily = FontFamily.Monospace
                                                        )
                                                    }
                                                },
                                                confirmButton = {
                                                    TextButton(onClick = { showFileViewDialog = false }) {
                                                        Text("Dismiss", color = appColor)
                                                    }
                                                },
                                                containerColor = DarkSurface
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// PARENT: STEP-BY-STEP SETUP WIZARD VIEW
// ==========================================
@Composable
fun SetupWizardView(viewModel: MonitorViewModel) {
    val storageGranted by viewModel.storagePermissionGranted.collectAsState()
    val folderConfigured by viewModel.whatsappFolderConfigured.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                "WhatsGuard Client Provisioning",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Follow these essential steps to install the active screen tracker and media crawler on the child's device.",
                fontSize = 12.sp,
                color = TextSecondary
            )
        }

        // Step 1 Card: Storage Access
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Step 1: Storage Folder Permission",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TealPrimary
                        )
                        Icon(
                            imageVector = if (storageGranted) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                            contentDescription = "Status",
                            tint = if (storageGranted) StatusOnline else TextSecondary
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Grant local folder crawling permissions on the child's device so WhatsGuard can read WhatsApp media metadata folders silently.",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.setStoragePermission(!storageGranted) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (storageGranted) DarkSurfaceVariant else TealPrimary
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(
                            if (storageGranted) "Revoke Permission" else "Grant Access Permission",
                            color = if (storageGranted) TextPrimary else Color.Black,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Step 2 Card: Folder Configuration
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Step 2: Detect WhatsApp Media Folder",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TealPrimary
                        )
                        Icon(
                            imageVector = if (folderConfigured) Icons.Default.CheckCircle else Icons.Default.RadioButtonUnchecked,
                            contentDescription = "Status",
                            tint = if (folderConfigured) StatusOnline else TextSecondary
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Locates the canonical storage path on the device (usually /Android/media/com.whatsapp/WhatsApp/Media/).",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.setWhatsappFolderConfigured(!folderConfigured) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (folderConfigured) DarkSurfaceVariant else TealPrimary
                        ),
                        shape = RoundedCornerShape(10.dp),
                        enabled = storageGranted // Must do step 1 first
                    ) {
                        Text(
                            if (folderConfigured) "Disconnect Path" else "Locate Media Folders",
                            color = if (folderConfigured) TextPrimary else Color.Black,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Step 3: Accessibility Screen Reader Alert
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Step 3: Enable Accessibility Screen Reader",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TealPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Configure the WhatsGuard Accessibility Service on the child device to sense active chat windows dynamically. This allows real-time text parsing without root privileges.",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(DarkSurfaceVariant, RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Info, contentDescription = "Info", tint = TealAccent)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Note: WhatsApp system layout changes automatically engage the OCR screenshot fallback engine to prevent transcript gaps.",
                            fontSize = 11.sp,
                            color = TextPrimary,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// CHILD: DEVICE SIMULATION & OCR FALLBACK SANDBOX
// ==========================================
@Composable
fun ChildSimulationView(viewModel: MonitorViewModel, chatLogs: List<ChatLog>) {
    val activeScreen by viewModel.childActiveScreen.collectAsState()
    val ocrLoading by viewModel.ocrLoading.collectAsState()
    val ocrError by viewModel.ocrError.collectAsState()

    var simulatedApp by remember { mutableStateOf("WhatsApp") }

    var customMsgText by remember { mutableStateOf("Did you finish the science lab?") }
    var ocrMockText1 by remember { mutableStateOf("Mom: Did you eat?") }
    var ocrMockText2 by remember { mutableStateOf("Me: Yes, had some pasta.") }

    var smsContact by remember { mutableStateOf("Aunt Clara") }
    var smsBody by remember { mutableStateOf("Let me know when you reach home!") }
    var callContact by remember { mutableStateOf("Unknown Number") }
    var callPhone by remember { mutableStateOf("+1 (555) 019-2831") }
    var callType by remember { mutableStateOf("Incoming") }
    var callDuration by remember { mutableStateOf("92") }

    val appColors = mapOf(
        "WhatsApp" to Color(0xFF00BFA5),
        "Instagram" to Color(0xFFE1306C),
        "Snapchat" to Color(0xFFFFE000)
    )

    val onAppSelected: (String) -> Unit = { app ->
        simulatedApp = app
        when (app) {
            "WhatsApp" -> {
                customMsgText = "Did you finish the science lab?"
                ocrMockText1 = "Mom: Did you eat?"
                ocrMockText2 = "Me: Yes, had some pasta."
                viewModel.setChildActiveScreen("Active Chat: Mom")
            }
            "Instagram" -> {
                customMsgText = "Check out this reel, it's hilarious!"
                ocrMockText1 = "jessie_style: Love that video!"
                ocrMockText2 = "Me: Same, shared it with everyone!"
                viewModel.setChildActiveScreen("Active Chat: jessie_style")
            }
            "Snapchat" -> {
                customMsgText = "Sent a snap, check it before it expires"
                ocrMockText1 = "Jake: Streak of 50 days!"
                ocrMockText2 = "Me: Let's make it 100!"
                viewModel.setChildActiveScreen("Active Chat: Jake")
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                "WhatsGuard Client Simulation Sandbox",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TealPrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Simulate child device states, toggle active applications, and trigger OCR fallback dynamically.",
                fontSize = 12.sp,
                color = TextSecondary
            )
        }

        // Active Application Selector Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Active Simulated Application",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TealAccent
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Switch between different installed apps to test real-time monitoring and capture methods.",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("WhatsApp", "Instagram", "Snapchat").forEach { app ->
                            val isSelected = simulatedApp == app
                            val color = appColors[app] ?: TealAccent
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) color else DarkSurfaceVariant)
                                    .clickable { onAppSelected(app) }
                                    .padding(vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = app,
                                    color = if (isSelected) Color.Black else TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // 1. Smart Screen Detection Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "1. Smart Screen Detection (Child Current Screen)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TealAccent
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Determines if the screen reader should log content. It restricts logs to verified chat screens only to secure privacy.",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        val activeChatLabel = when (simulatedApp) {
                            "WhatsApp" -> "Active Chat: Mom"
                            "Instagram" -> "Active Chat: jessie_style"
                            else -> "Active Chat: Jake"
                        }
                        listOf(
                            activeChatLabel to Icons.Default.ChatBubble,
                            "Contact List" to Icons.Default.People,
                            "Settings Profile" to Icons.Default.Settings
                        ).forEach { (screen, icon) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (activeScreen == screen) DarkSurfaceVariant else Color.Transparent)
                                    .clickable { viewModel.setChildActiveScreen(screen) }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = activeScreen == screen,
                                    onClick = { viewModel.setChildActiveScreen(screen) },
                                    colors = RadioButtonDefaults.colors(selectedColor = TealAccent)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(imageVector = icon, contentDescription = screen, tint = appColors[simulatedApp] ?: TealPrimary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(screen, color = TextPrimary, fontSize = 14.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(DarkSurfaceVariant, RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        val isEngaged = activeScreen.startsWith("Active Chat")
                        Text(
                            text = if (isEngaged) "🟢 Screen Reader CAPTURE ENGAGED: Messages will be logged on $simulatedApp."
                                   else "🛑 Screen Reader SUSPENDED: Avoid logging contact profiles or system indexes.",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isEngaged) StatusOnline else StatusWarning
                        )
                    }
                }
            }
        }

        // 2. Real-time Log Simulator with Duplicate Checking
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "2. Push Simulated Real-Time Chat Message",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TealAccent
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    TextField(
                        value = customMsgText,
                        onValueChange = { customMsgText = it },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF0F172A),
                            unfocusedContainerColor = Color(0xFF0F172A),
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        placeholder = { Text("Type simulated incoming message...") }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val senderName = when (simulatedApp) {
                        "WhatsApp" -> "Mom"
                        "Instagram" -> "jessie_style"
                        else -> "Jake"
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.simulateChildMessageReceived(senderName, customMsgText, isOutgoing = false, appSource = simulatedApp)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = appColors[simulatedApp] ?: TealPrimary),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Receive msg", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                viewModel.simulateChildMessageReceived(senderName, customMsgText, isOutgoing = true, appSource = simulatedApp)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Send msg", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "Try pushing a duplicate or pushing when current screen is 'Contact List' to see the smart rules filter it!",
                        fontSize = 11.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        // 3. OCR FALLBACK SANDBOX USING GEMINI
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "3. OCR Fallback Analyzer Sandbox",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TealAccent
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "If the regular screen reader fails, WhatsGuard captures a silent image and extracts message texts using Google Gemini AI.",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Text fields to configure mock OCR texts
                    Text("Simulated Screenshot Unreadable Lines:", fontSize = 11.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    TextField(
                        value = ocrMockText1,
                        onValueChange = { ocrMockText1 = it },
                        label = { Text("Line 1", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF0F172A),
                            unfocusedContainerColor = Color(0xFF0F172A)
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    TextField(
                        value = ocrMockText2,
                        onValueChange = { ocrMockText2 = it },
                        label = { Text("Line 2", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF0F172A),
                            unfocusedContainerColor = Color(0xFF0F172A)
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Simulated Screenshot drawing preview card
                    val screenshotBgColor = when (simulatedApp) {
                        "WhatsApp" -> Color(0xFFE5DDD5)
                        "Instagram" -> Color(0xFF121212)
                        else -> Color(0xFFFFFC00)
                    }
                    val screenshotTextColor = if (simulatedApp == "Instagram") Color.White else Color.Black
                    val screenshotMessageBg = when (simulatedApp) {
                        "WhatsApp" -> Color(0xFFDCF8C6)
                        "Instagram" -> Color(0xFF262626)
                        else -> Color(0xFFFFFFFF)
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(screenshotBgColor)
                            .border(1.dp, Color.LightGray, RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                modifier = Modifier
                                    .background(screenshotMessageBg, RoundedCornerShape(8.dp))
                                    .padding(8.dp)
                                    .align(Alignment.End)
                            ) {
                                Text(ocrMockText2, color = screenshotTextColor, fontSize = 12.sp)
                            }
                            
                            Row(
                                modifier = Modifier
                                    .background(if (simulatedApp == "Instagram") Color(0xFF262626) else Color.White, RoundedCornerShape(8.dp))
                                    .padding(8.dp)
                                    .align(Alignment.Start)
                            ) {
                                Text(ocrMockText1, color = screenshotTextColor, fontSize = 12.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { viewModel.triggerOcrFallbackAnalysis(ocrMockText1, ocrMockText2, appSource = simulatedApp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("ocr_fallback_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = appColors[simulatedApp] ?: TealPrimary),
                        shape = RoundedCornerShape(10.dp),
                        enabled = !ocrLoading
                    ) {
                        if (ocrLoading) {
                            CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Analyzing Screen (Gemini OCR)...", color = Color.Black)
                        } else {
                            Text("Trigger OCR Fallback Analysis", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }

                    ocrError?.let { err ->
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = err,
                            color = StatusWarning,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // 4. SIMULATE SYSTEM ACTIVITIES (SMS & CALLS)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "4. Simulate System Activities (SMS & Calls)",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = TealAccent
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        "Inject real-time call records and SMS text messages directly into the database to verify active tracking capabilities.",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(DarkSurfaceVariant))
                    Spacer(modifier = Modifier.height(16.dp))

                    // SMS Section
                    Text(
                        "Simulate SMS Text Message",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF60A5FA)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = smsContact,
                        onValueChange = { smsContact = it },
                        label = { Text("SMS Sender/Receiver Name", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF0F172A),
                            unfocusedContainerColor = Color(0xFF0F172A)
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = smsBody,
                        onValueChange = { smsBody = it },
                        label = { Text("SMS Body Text", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF0F172A),
                            unfocusedContainerColor = Color(0xFF0F172A)
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.simulateChildMessageReceived(smsContact, smsBody, isOutgoing = false, appSource = "SMS")
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF60A5FA)),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Simulate Recv SMS", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = {
                                viewModel.simulateChildMessageReceived(smsContact, smsBody, isOutgoing = true, appSource = "SMS")
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = DarkSurfaceVariant),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Simulate Send SMS", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(DarkSurfaceVariant))
                    Spacer(modifier = Modifier.height(16.dp))

                    // Calls Section
                    Text(
                        "Simulate Dialer Call Record",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = StatusOnline
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = callContact,
                        onValueChange = { callContact = it },
                        label = { Text("Contact Name", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF0F172A),
                            unfocusedContainerColor = Color(0xFF0F172A)
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = callPhone,
                        onValueChange = { callPhone = it },
                        label = { Text("Phone Number", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF0F172A),
                            unfocusedContainerColor = Color(0xFF0F172A)
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Call Type Selector Chips
                    Text("Call Type:", fontSize = 11.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Incoming", "Outgoing", "Missed").forEach { type ->
                            val isSelected = callType == type
                            val chipColor = when (type) {
                                "Incoming" -> StatusOnline
                                "Outgoing" -> Color(0xFF60A5FA)
                                else -> StatusWarning
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) chipColor else DarkSurfaceVariant)
                                    .clickable { callType = type }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = type,
                                    color = if (isSelected) Color.Black else TextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    TextField(
                        value = callDuration,
                        onValueChange = { callDuration = it },
                        label = { Text("Call Duration (seconds)", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF0F172A),
                            unfocusedContainerColor = Color(0xFF0F172A)
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            viewModel.simulatePhoneCall(
                                callContact,
                                callPhone,
                                callType,
                                callDuration.toIntOrNull() ?: 0
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = StatusOnline),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Simulate Call Event", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
