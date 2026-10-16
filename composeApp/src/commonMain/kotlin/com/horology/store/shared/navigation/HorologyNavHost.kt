package com.horology.store.shared.navigation

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.horology.store.shared.ui.cart.CartScreen
import com.horology.store.shared.ui.catalog.CatalogScreen
import com.horology.store.shared.ui.detail.ProductDetailScreen
import com.horology.store.shared.ui.favorites.FavoritesScreen
import com.horology.store.shared.ui.home.HomeScreen
import com.horology.store.shared.ui.shell.ShellScreen

@Composable
fun HorologyNavHost() {
    val nav = rememberNavController()
    NavHost(
        navController = nav,
        startDestination = Home,
        enterTransition = {
            slideInHorizontally(
                animationSpec = spring(stiffness = Spring.StiffnessMedium),
                initialOffsetX = { it / 6 },
            ) + fadeIn(tween(190))
        },
        exitTransition = { fadeOut(tween(140)) },
        popEnterTransition = { fadeIn(tween(190)) },
        popExitTransition = {
            slideOutHorizontally(
                animationSpec = spring(stiffness = Spring.StiffnessMedium),
                targetOffsetX = { -it / 6 },
            ) + fadeOut(tween(140))
        },
    ) {
        composable<Home> {
            ShellScreen(selectedTab = 0, onSelectTab = tabSelect(nav)) {
                HomeScreen()
            }
        }
        composable<Catalog> {
            ShellScreen(selectedTab = 1, onSelectTab = tabSelect(nav)) {
                CatalogScreen()
            }
        }
        composable<Favorites> {
            ShellScreen(selectedTab = 2, onSelectTab = tabSelect(nav)) {
                FavoritesScreen()
            }
        }
        composable<Cart> {
            ShellScreen(selectedTab = 3, onSelectTab = tabSelect(nav)) {
                CartScreen()
            }
        }
        composable<ProductDetail> { entry ->
            val route = entry.toRoute<ProductDetail>()
            ProductDetailScreen(
                productId = route.productId,
                onBack = { nav.popBackStack() },
            )
        }
    }
}

private fun tabSelect(nav: NavHostController): (Int) -> Unit = { tab ->
    val destination = when (tab) {
        0 -> Home
        1 -> Catalog
        2 -> Favorites
        else -> Cart
    }
    nav.navigate(destination) {
        popUpTo(Home) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
