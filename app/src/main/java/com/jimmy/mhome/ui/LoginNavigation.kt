package com.jimmy.mhome.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.animation.navigation
import com.jimmy.mhome.login.Login
import com.jimmy.mhome.forgotpassword.ForgotPassword
import com.jimmy.mhome.forgotpassword.ForgotPasswordConfirm
import com.jimmy.mhome.register.Register
import com.jimmy.mhome.register.RegisterConfirmPassword
import com.jimmy.mhome.register.RegisterOtp
import com.jimmy.mhome.register.Terms
import com.jimmy.mhome.ui.util.composable
import timber.log.Timber

@ExperimentalAnimationApi
fun NavGraphBuilder.addLoginTopLevel(
    navController: NavController,
) {
    navigation(
        route = Screen.Login.route, //route = "Login"
        startDestination = LeafScreen.MainLogin.createRoute(Screen.Login),
    ) {
        addMainLogin(navController, Screen.Login)
        addLogin(navController, Screen.Login)
        addRegister(navController, Screen.Login)
        addForgotPassword(navController, Screen.Login)
        addTerms(navController, Screen.Login)
    }
}

@ExperimentalAnimationApi
private fun NavGraphBuilder.addLogin(
    navController: NavController,
    root: Screen,
) {

    composable(
        //route = "Login/Login"
        route = LeafScreen.Login.createRoute(root),
        debugLabel = "Login"
    ) {

        Login(
            openForgotPassword = {
                navController.navigate(LeafScreen.ForgotPassword.createRoute(root))
                {
                    // Avoid multiple copies of the same destination when
                    launchSingleTop = true
                    // Restore state when re-selecting a previously selected item
                    restoreState = true
                }
            },

            openRegister = {
                navController.navigate(LeafScreen.Register.createRoute(root)){
                    // Avoid multiple copies of the same destination when
                    launchSingleTop = true
                    // Restore state when re-selecting a previously selected item
                    restoreState = true
                }
            },

            openMainScreen = {
//                navController.popBackStack()
                // Pop up to the start destination of the graph to
                // avoid building up a large stack of destinations
                // on the back stack as users select items

                navController.navigate(Screen.Home.route){
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                        inclusive = true
                    }
                    // Avoid multiple copies of the same destination when
                    launchSingleTop = true
                    // Restore state when re-selecting a previously selected item
                    restoreState = true
                }
            }
        )

    }
}


@ExperimentalAnimationApi
private fun NavGraphBuilder.addTerms(
    navController: NavController,
    root: Screen,
) {

    composable(
        route = LeafScreen.Terms.createRoute(root),
        debugLabel = "Terms"
    ) {

        Terms()

    }
}

@ExperimentalAnimationApi
private fun NavGraphBuilder.addForgotPassword(
    navController: NavController,
    root: Screen,
) {
    composable(
        route = LeafScreen.ForgotPassword.createRoute(root),
        debugLabel = "ForgotPassword"
    ) {

        // ForgotPassword()
        ForgotPassword(
            openLogin = { navController.navigateUp() },
            openPasswordConfirm = {
                navController.navigate(LeafScreen.ForgotPasswordConfirm.createRoute(root)){
                    // Avoid multiple copies of the same destination when
                    launchSingleTop = true
                    // Restore state when re-selecting a previously selected item
                    restoreState = true
                }
            }
        )

    }

    composable(
        route = LeafScreen.ForgotPasswordConfirm.createRoute(root),
        debugLabel = "ForgotPassword2"
    ) {

        ForgotPasswordConfirm(
            openLogin = {
                navController.navigate(LeafScreen.Login.createRoute(root)){
                    // Avoid multiple copies of the same destination when
                    launchSingleTop = true
                    // Restore state when re-selecting a previously selected item
                    restoreState = true
                }
            }
        )
    }


}

@ExperimentalAnimationApi
private fun NavGraphBuilder.addRegister(
    navController: NavController,
    root: Screen,
) {
    composable(
        route = LeafScreen.Register.createRoute(root),
        debugLabel = "Register"
    ) {

        Register(
            openLogin = {
                navController.navigate(LeafScreen.Login.createRoute(root)){
                    // Avoid multiple copies of the same destination when
                    launchSingleTop = true
                    // Restore state when re-selecting a previously selected item
                    restoreState = true
                }
            },
            onTermsClicked = {
                navController.navigate(LeafScreen.Terms.createRoute(root)){
                    // Avoid multiple copies of the same destination when
                    launchSingleTop = true
                    // Restore state when re-selecting a previously selected item
                    restoreState = true
                }

            },

            openOtp = {
                navController.navigate(LeafScreen.RegisterOtp.createRoute(root)){
                    // Avoid multiple copies of the same destination when
                    launchSingleTop = true
                    // Restore state when re-selecting a previously selected item
                    restoreState = true
                }
            }

        )
    }

    composable(
        route = LeafScreen.RegisterOtp.createRoute(root),
        debugLabel = "RegisterOtp"
    ) {

        RegisterOtp(
            openPasswordConfirm = {
                navController.navigate(LeafScreen.RegisterConfirmPassword.createRoute(root)){
                    // Avoid multiple copies of the same destination when
                    launchSingleTop = true
                    // Restore state when re-selecting a previously selected item
                    restoreState = true
                }
            }
        )
    }

    composable(
        route = LeafScreen.RegisterConfirmPassword.createRoute(root),
        debugLabel = "Register confirm password"
    ) {
        RegisterConfirmPassword(
            openLogin = {
                navController.navigate(LeafScreen.Login.createRoute(Screen.Login)){
                    // Avoid multiple copies of the same destination when
                    launchSingleTop = true
                    // Restore state when re-selecting a previously selected item
                    restoreState = true
                }

            }
        )

    }
}
