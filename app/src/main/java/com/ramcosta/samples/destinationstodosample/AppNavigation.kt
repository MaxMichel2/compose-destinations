package com.ramcosta.samples.destinationstodosample

import android.os.Bundle
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.navigation
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.animations.rememberAnimatedNavHostEngine
import com.ramcosta.composedestinations.animations.utils.animatedComposable
import com.ramcosta.composedestinations.animations.utils.bottomSheetComposable
import com.ramcosta.composedestinations.manualcomposablecalls.*
import com.ramcosta.composedestinations.navigation.dependency
import com.ramcosta.composedestinations.scope.resultBackNavigator
import com.ramcosta.composedestinations.scope.resultRecipient
import com.ramcosta.composedestinations.utils.contains
import com.ramcosta.composedestinations.utils.dialogComposable
import com.ramcosta.samples.destinationstodosample.commons.DrawerController
//import com.ramcosta.samples.destinationstodosample.di.viewModel
import com.ramcosta.samples.destinationstodosample.ui.screens.Feed
import com.ramcosta.samples.destinationstodosample.ui.screens.GoToProfileConfirmation
import com.ramcosta.samples.destinationstodosample.ui.screens.NavGraphs
import com.ramcosta.samples.destinationstodosample.ui.screens.TestScreen
import com.ramcosta.samples.destinationstodosample.ui.screens.destinations.*
import com.ramcosta.samples.destinationstodosample.ui.screens.greeting.GreetingScreen
import com.ramcosta.samples.destinationstodosample.ui.screens.greeting.GreetingUiEvents
import com.ramcosta.samples.destinationstodosample.ui.screens.greeting.GreetingUiState
import com.ramcosta.samples.destinationstodosample.ui.screens.greeting.GreetingViewModel
import com.ramcosta.samples.destinationstodosample.ui.screens.profile.ProfileScreen
import com.ramcosta.samples.destinationstodosample.ui.screens.profile.ProfileUiEvents
import com.ramcosta.samples.destinationstodosample.ui.screens.profile.ProfileUiState
import com.ramcosta.samples.destinationstodosample.ui.screens.profile.ProfileViewModel
import com.ramcosta.samples.destinationstodosample.ui.screens.settings.Settings
import com.ramcosta.samples.destinationstodosample.ui.screens.settings.SettingsViewModel
import com.ramcosta.samples.destinationstodosample.ui.screens.settings.ThemeSettings
import org.koin.androidx.compose.getStateViewModel
import org.koin.androidx.compose.getViewModel
import org.koin.androidx.compose.viewModel

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialNavigationApi::class)
@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    drawerController: DrawerController,
    navController: NavHostController,
) {
    // ------- Defining default animations for root and nested nav graphs example -------
//    val navHostEngine = rememberAnimatedNavHostEngine(
//        rootDefaultAnimations = RootNavGraphDefaultAnimations.ACCOMPANIST_FADING,
//        defaultAnimationsForNestedNavGraph = mapOf(
//            NavGraphs.settings to NestedNavGraphDefaultAnimations(
//                enterTransition = { fadeIn(animationSpec = tween(2000)) },
//                exitTransition = { fadeOut(animationSpec = tween(2000)) }
//            )
//        )
//    )

    val navHostEngine = rememberAnimatedNavHostEngine()

    DestinationsNavHost(
        navGraph = NavGraphs.root,
        startRoute = if (Math.random() > 0.5) FeedDestination else NavGraphs.root.startRoute,
        engine = navHostEngine,
        navController = navController,
        modifier = modifier,
        dependenciesContainerBuilder = {
            dependency(drawerController)

            if (NavGraphs.settings.contains(destination)) {
                val parentEntry =
                    remember { navController.getBackStackEntry(NavGraphs.settings.route) }
                dependency(
                    androidx.lifecycle.viewmodel.compose.viewModel<SettingsViewModel>(
                        parentEntry
                    )
                )
            }
        }
    ) {
        profileScreen()
        greetingScreen(drawerController)
    }
}

@ExperimentalAnimationApi
private fun ManualComposableCallsBuilder.profileScreen() {

    // animatedComposable is needed to get an AnimatedVisibilityScope to use as the receiver for our
    // ProfileScreen
    animatedComposable(ProfileScreenDestination) {
//        val vm = getStateViewModel<ProfileViewModel>(state = { navBackStackEntry.arguments ?: Bundle() })
        val vm by viewModel<ProfileViewModel>()

        ProfileScreen(
            vm as ProfileUiState,
            vm as ProfileUiEvents
        )
    }
}

private fun ManualComposableCallsBuilder.greetingScreen(drawerController: DrawerController) {
    composable(GreetingScreenDestination) {
        val vm by viewModel<GreetingViewModel>()

        GreetingScreen(
            navigator = destinationsNavigator,
            drawerController = drawerController,
            uiEvents = vm as GreetingUiEvents,
            uiState = vm as GreetingUiState,
            resultRecipient = resultRecipient()
        )
    }
}

// region ------- Without using DestinationsNavHost example -------
@Suppress("UNUSED")
@ExperimentalMaterialNavigationApi
@ExperimentalAnimationApi
@Composable
fun SampleAppAnimatedNavHostExample(
    modifier: Modifier,
    navController: NavHostController,
    drawerController: DrawerController
) {
    AnimatedNavHost(
        modifier = modifier,
        navController = navController,
        startDestination = GreetingScreenDestination.route,
        route = "root"
    ) {

        animatedComposable(GreetingScreenDestination) {
            val vm = viewModel<GreetingViewModel>()

            GreetingScreen(
                navigator = destinationsNavigator(navController),
                drawerController = drawerController,
                uiEvents = vm as GreetingUiEvents,
                uiState = vm as GreetingUiState,
                resultRecipient = resultRecipient()
            )
        }

        animatedComposable(FeedDestination) {
            Feed()
        }

        dialogComposable(GoToProfileConfirmationDestination) {
            GoToProfileConfirmation(
                resultNavigator = resultBackNavigator(navController)
            )
        }

        animatedComposable(TestScreenDestination) {
            TestScreen(
                id = navArgs.id,
                stuff1 = navArgs.stuff1,
                stuff2 = navArgs.stuff2,
                stuff3 = navArgs.stuff3,
                stuff5 = navArgs.stuff5,
                stuff6 = navArgs.stuff6,
            )
        }

        animatedComposable(ProfileScreenDestination) {
            val vm = viewModel<ProfileViewModel>()

            ProfileScreen(
                vm as ProfileUiState,
                vm as ProfileUiEvents
            )
        }

        navigation(
            startDestination = SettingsDestination.route,
            route = "settings"
        ) {
            animatedComposable(SettingsDestination) {
                Settings(
                    viewModel = getViewModel(),
                    navigator = destinationsNavigator(navController),
                    themeSettingsResultRecipient = resultRecipient()
                )
            }

            bottomSheetComposable(ThemeSettingsDestination) {
                ThemeSettings(
                    viewModel = getViewModel(),
                    resultNavigator = resultBackNavigator(navController)
                )
            }
        }
    }
}
// endregion
