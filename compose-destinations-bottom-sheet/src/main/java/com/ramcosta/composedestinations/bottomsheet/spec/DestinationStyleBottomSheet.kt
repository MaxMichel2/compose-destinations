package com.ramcosta.composedestinations.bottomsheet.spec

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.navigation.bottomSheet
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.ramcosta.composedestinations.bottomsheet.scope.BottomSheetDestinationScopeImpl
import com.ramcosta.composedestinations.manualcomposablecalls.DestinationLambda
import com.ramcosta.composedestinations.manualcomposablecalls.ManualComposableCalls
import com.ramcosta.composedestinations.navigation.DependenciesContainerBuilder
import com.ramcosta.composedestinations.spec.DestinationStyle
import com.ramcosta.composedestinations.spec.TypedDestinationSpec

/**
 * Marks the destination to be shown with a bottom sheet style.
 * It requires "io.github.raamcosta.compose-destinations:bottom-sheet" dependency.
 *
 * You will need to use a `ModalBottomSheetLayout` wrapping your
 * top level Composable.
 * Example:
 * ```
 * val navController = rememberNavController()
 * val bottomSheetNavigator = rememberBottomSheetNavigator()
 * navController.navigatorProvider += bottomSheetNavigator
 *
 * ModalBottomSheetLayout(
 *     bottomSheetNavigator = bottomSheetNavigator
 * ) {
 *     //YOUR TOP LEVEL COMPOSABLE LIKE `DestinationsNavHost` or `Scaffold`
 * }
 * ```
 */
object DestinationStyleBottomSheet : DestinationStyle() {

    override fun <T> NavGraphBuilder.addComposable(
        destination: TypedDestinationSpec<T>,
        navController: NavHostController,
        dependenciesContainerBuilder: @Composable DependenciesContainerBuilder<*>.() -> Unit,
        manualComposableCalls: ManualComposableCalls
    ) {
        @Suppress("UNCHECKED_CAST")
        val contentWrapper = manualComposableCalls[destination.route] as? DestinationLambda<T>?

        bottomSheet(
            destination.route,
            destination.arguments,
            destination.deepLinks
        ) { navBackStackEntry ->
            CallComposable(
                destination,
                navController,
                navBackStackEntry,
                dependenciesContainerBuilder,
                contentWrapper
            )
        }
    }
}

@Composable
private fun <T> ColumnScope.CallComposable(
    destination: TypedDestinationSpec<T>,
    navController: NavHostController,
    navBackStackEntry: NavBackStackEntry,
    dependenciesContainerBuilder: @Composable DependenciesContainerBuilder<*>.() -> Unit,
    contentWrapper: DestinationLambda<T>?
) {
    val scope = remember(navBackStackEntry) {
        BottomSheetDestinationScopeImpl(
            destination,
            navBackStackEntry,
            navController,
            this,
            dependenciesContainerBuilder
        )
    }

    if (contentWrapper == null) {
        with(destination) { scope.Content() }
    } else {
        contentWrapper(scope)
    }
}