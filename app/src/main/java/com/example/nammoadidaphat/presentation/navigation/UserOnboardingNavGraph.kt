package com.example.nammoadidaphat.presentation.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.nammoadidaphat.presentation.ui.onboarding.AgeScreen
import com.example.nammoadidaphat.presentation.ui.onboarding.FitnessLevelScreen
import com.example.nammoadidaphat.presentation.ui.onboarding.GenderScreen
import com.example.nammoadidaphat.presentation.ui.onboarding.GoalScreen
import com.example.nammoadidaphat.presentation.ui.onboarding.HeightScreen
import com.example.nammoadidaphat.presentation.ui.onboarding.WeightScreen
import com.example.nammoadidaphat.presentation.viewmodel.UserOnboardingViewModel
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.launch

enum class OnboardingPage {
    Gender,
    Age,
    Weight,
    Height,
    Goal,
    FitnessLevel
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun UserOnboardingNavGraph(
    navController: NavHostController,
    onFinished: () -> Unit
) {
    val viewModel: UserOnboardingViewModel = hiltViewModel()
    val coroutineScope = rememberCoroutineScope()
    
    // Use pager state to manage horizontal paging
    val pagerState = rememberPagerState(initialPage = 0)
    
    // Define page count and current page
    val pageCount = OnboardingPage.values().size
    
    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            count = pageCount,
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            userScrollEnabled = false // Disable swiping to ensure only button navigation
        ) { page ->
            when (page) {
                OnboardingPage.Gender.ordinal -> {
                    GenderScreen(
                        viewModel = viewModel,
                        onContinue = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(OnboardingPage.Age.ordinal)
                            }
                        }
                    )
                }
                OnboardingPage.Age.ordinal -> {
                    AgeScreen(
                        viewModel = viewModel,
                        onContinue = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(OnboardingPage.Weight.ordinal)
                            }
                        },
                        onBack = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(OnboardingPage.Gender.ordinal)
                            }
                        }
                    )
                }
                OnboardingPage.Weight.ordinal -> {
                    WeightScreen(
                        viewModel = viewModel,
                        onContinue = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(OnboardingPage.Height.ordinal)
                            }
                        },
                        onBack = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(OnboardingPage.Age.ordinal)
                            }
                        }
                    )
                }
                OnboardingPage.Height.ordinal -> {
                    HeightScreen(
                        viewModel = viewModel,
                        onContinue = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(OnboardingPage.Goal.ordinal)
                            }
                        },
                        onBack = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(OnboardingPage.Weight.ordinal)
                            }
                        }
                    )
                }
                OnboardingPage.Goal.ordinal -> {
                    GoalScreen(
                        viewModel = viewModel,
                        onContinue = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(OnboardingPage.FitnessLevel.ordinal)
                            }
                        },
                        onBack = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(OnboardingPage.Height.ordinal)
                            }
                        }
                    )
                }
                OnboardingPage.FitnessLevel.ordinal -> {
                    FitnessLevelScreen(
                        viewModel = viewModel,
                        onComplete = onFinished,
                        onBack = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(OnboardingPage.Goal.ordinal)
                            }
                        }
                    )
                }
            }
        }
        // Page indicators have been removed as requested
    }
} 