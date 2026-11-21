package com.earnx.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val Ink = Color(0xFF101014)
private val Panel = Color(0xFF1D1D24)
private val Red = Color(0xFFFF3B45)
private val Muted = Color(0xFFB7B7C2)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { EarnXApp() }
    }
}

@Composable
fun EarnXApp() {
    var balance by remember { mutableIntStateOf(0) }
    var claimed by remember { mutableStateOf(false) }
    var page by remember { mutableStateOf("Home") }
    var notice by remember { mutableStateOf("") }
    var upi by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("100") }

    MaterialTheme(colorScheme = darkColorScheme(
        primary = Red, background = Ink, surface = Panel,
        onPrimary = Color.White, onBackground = Color.White, onSurface = Color.White
    )) {
        Scaffold(
            containerColor = Ink,
            bottomBar = {
                NavigationBar(containerColor = Panel, contentColor = Color.White) {
                    listOf("Home", "Tasks", "Wallet", "Profile").forEach { item ->
                        NavigationBarItem(
                            selected = page == item,
                            onClick = { page = item; notice = "" },
                            icon = { Icon(
                                when(item) {
                                    "Home" -> Icons.Default.Home
                                    "Tasks" -> Icons.Default.CheckCircle
                                    "Wallet" -> Icons.Default.AccountBalanceWallet
                                    else -> Icons.Default.Person
                                }, contentDescription = item
                            ) },
                            label = { Text(item) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Red, selectedTextColor = Red,
                                indicatorColor = Color(0xFF3A2026),
                                unselectedIconColor = Muted, unselectedTextColor = Muted
                            )
                        )
                    }
                }
            }
        ) { pad ->
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(pad).padding(horizontal = 18.dp),
                contentPadding = PaddingValues(top = 22.dp, bottom = 28.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text("EarnX", color = Red, fontSize = 28.sp, fontWeight = FontWeight.Black)
                            Text("Rewards that make sense", color = Muted, fontSize = 13.sp)
                        }
                        Surface(color = Panel, shape = RoundedCornerShape(50)) {
                            Text("DEMO", Modifier.padding(horizontal = 14.dp, vertical = 8.dp), color = Red, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                when(page) {
                    "Home" -> {
                        item { BalanceCard(balance) }
                        item {
                            SectionTitle("Daily reward")
                            CardBlock {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Column(Modifier.weight(1f)) {
                                        Text("Daily check-in", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                                        Text(if (claimed) "Today's demo reward claimed" else "Claim 10 demo points today", color = Muted, fontSize = 13.sp)
                                    }
                                    Button(
                                        onClick = { if (!claimed) { balance += 10; claimed = true; notice = "+10 demo points added" } },
                                        enabled = !claimed
                                    ) { Text(if (claimed) "Claimed" else "Claim") }
                                }
                            }
                        }
                        item { SectionTitle("Explore EarnX") }
                        item { FeatureRow(Icons.Default.PlayCircle, "Watch & Earn", "Rewarded ads will be connected after approval") }
                        item { FeatureRow(Icons.Default.TaskAlt, "Complete Tasks", "Try sample tasks in the Tasks tab") }
                        item { FeatureRow(Icons.Default.GroupAdd, "Refer & Earn", "Invite friends using your referral code") }
                    }
                    "Tasks" -> {
                        item { SectionTitle("Available tasks") }
                        item { TaskCard("Complete your profile", "Sample task • one-time", "Start", { notice = "Profile task is a preview in this first version." }) }
                        item { TaskCard("Read app safety tips", "Sample task • 5 demo points", "Complete", { balance += 5; notice = "+5 demo points added. Sample reward only." }) }
                        item { TaskCard("Invite a friend", "Referral tracking will be added with backend", "Invite", { notice = "Referral sharing will be enabled in the next version." }) }
                    }
                    "Wallet" -> {
                        item { BalanceCard(balance) }
                        item { SectionTitle("Withdrawal request") }
                        item {
                            CardBlock {
                                Text("UPI ID", fontWeight = FontWeight.Bold)
                                OutlinedTextField(value = upi, onValueChange = { upi = it }, label = { Text("name@bank") }, modifier = Modifier.fillMaxWidth())
                                Spacer(Modifier.height(10.dp))
                                Text("Amount (₹)", fontWeight = FontWeight.Bold)
                                OutlinedTextField(value = amount, onValueChange = { amount = it.filter(Char::isDigit).take(6) }, modifier = Modifier.fillMaxWidth())
                                Spacer(Modifier.height(10.dp))
                                Button(onClick = { notice = "Demo only: no money is transferred. Live withdrawals need verified backend, KYC/compliance and payment integration." }, modifier = Modifier.fillMaxWidth()) {
                                    Text("Submit demo request")
                                }
                                Text("No real cash value in this prototype.", color = Muted, fontSize = 12.sp)
                            }
                        }
                    }
                    else -> {
                        item { SectionTitle("Your profile") }
                        item {
                            CardBlock {
                                Text("Guest user", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                Text("Referral code: EARNX-DEMO", color = Muted)
                                Text("Account sign-in and referral tracking are not connected yet.", color = Muted, fontSize = 13.sp)
                            }
                        }
                        item { FeatureRow(Icons.Default.Security, "Safe rewards", "No deposit required. Never pay to unlock a reward.") }
                    }
                }
                if (notice.isNotBlank()) {
                    item {
                        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF342027))) {
                            Text(notice, Modifier.padding(14.dp), color = Color.White, fontSize = 13.sp)
                        }
                    }
                }
                item {
                    Text("Prototype v1 • Points and withdrawals are simulated. No real earnings or payments yet.",
                        color = Muted, fontSize = 11.sp)
                }
            }
        }
    }
}

@Composable
fun BalanceCard(balance: Int) {
    Card(shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = Red)) {
        Column(Modifier.fillMaxWidth().padding(20.dp)) {
            Text("YOUR DEMO BALANCE", color = Color.White.copy(alpha = .85f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text("$balance points", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Black)
            Text("Points are for testing only; they are not redeemable cash.", color = Color.White.copy(alpha = .9f), fontSize = 12.sp)
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(title, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
}

@Composable
fun CardBlock(content: @Composable ColumnScope.() -> Unit) {
    Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Panel)) {
        Column(Modifier.fillMaxWidth().padding(16.dp), content = content)
    }
}

@Composable
fun FeatureRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, subtitle: String) {
    CardBlock {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = Red, modifier = Modifier.size(30.dp))
            Spacer(Modifier.width(14.dp))
            Column {
                Text(title, fontWeight = FontWeight.Bold)
                Text(subtitle, color = Muted, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun TaskCard(title: String, subtitle: String, action: String, onClick: () -> Unit) {
    CardBlock {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold)
                Text(subtitle, color = Muted, fontSize = 12.sp)
            }
            Button(onClick = onClick) { Text(action) }
        }
    }
}
