package com.example.flashcardapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.flashcardapp.ui.theme.FlashCardAppTheme

class KotlinSupport {
    @Preview(showBackground = true, showSystemUi = true)
    @Composable
    fun SupportPreview() {
        FlashCardAppTheme {
            FlashcardApp()
        }
    }

    companion object {
        @Composable
    //  <T> allows for customization in the kind of
    //  lists to be worked with in deleting a particular item
        fun <T> DeleteButton(
            item: T,
            list: MutableList<T>,
            delete: String = "Delete",
            textModifier: Modifier = Modifier,
            modifier: Modifier = Modifier
        ) {
            Button(
                onClick = {
                    if (list.contains(item)) {
                        list.remove(item)
                    }
                },
                modifier = modifier
            ) {
                Text(
                    text = delete,
                    modifier = textModifier
                )
            }
        }
    }

    class Slot (
        val textPieces: List<@Composable () -> Unit>,
        val modifier: Modifier = Modifier,
        val backgroundColor: Color = Color.White,
        val horizontalAlignment: Alignment.Horizontal = Alignment.Start,
        val verticalArrangement: Arrangement.Vertical = Arrangement.Top
    ){
        @Composable
//      The custom modifiers have to be placed after the base modifiers in this
//      function (the custom modifiers being this.modifier), thus the default
//      call for this function uses this.modifier rather than the suggested
//      Modifier, which would not include the custom modifiers in the first place
        fun Display(modifier: Modifier = this.modifier) {
            Column(
                horizontalAlignment = horizontalAlignment,
                verticalArrangement = verticalArrangement,
                modifier = Modifier
                    .fillMaxHeight()
                    .background(backgroundColor)
                    .then(modifier)
            ) {
                for (text: @Composable () -> Unit in textPieces) {
                    text()
                }
            }
        }
    }

    class Grid (
        val textGrid: List<List<Slot>>,
        val modifier: Modifier = Modifier
    ) {
        @Composable
        fun Display() {
            Column(
                modifier = modifier
                    .fillMaxSize()
            ) {
                for (row: List<Slot> in textGrid) {
                    Row (
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        for (slot: Slot in row) {
                            slot.Display(modifier = slot.modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }

    data class Page(
        val name: String,
        val content: @Composable () -> Unit
    )

    class App(
        val appName: String = "App"
    ) {
//      For sake of ease of change for common aspects of an app
        var goBackButtonModifier: Modifier = Modifier
        var goBackTextModifier: Modifier = Modifier
        var standardHeadingModifier: Modifier = Modifier
        var standardHeadingTextModifier: Modifier = Modifier
        var standardPageModifier: Modifier =
            Modifier
                .fillMaxSize()
                .padding(32.dp)
                .padding(top = 48.dp)

        var goBackText: String = "Go Back"

//      Changing this value makes testing different pages easier in preview
        var homePageIndex: Int = 0

//      Tracks different possible pages in the app
        var pageList: MutableList<Page> =
            mutableListOf()

    //  Giving access to controller outside of Run() allows for building
    //  the various composable Unit functions with the ability to easily
    //  navigate from one page to the other without actually running it yet
        var navController: NavHostController? = null

        @Composable
    //  The initialization has to be in a composable function
        fun InitializeNavigation() {
            navController = rememberNavController()
        }

        fun addPage(page: Page) {
            pageList.add(page)
        }
        fun addPage(pages: List<Page>) {
            for (page: Page in pages) {
                addPage(page)
            }
        }
        fun removePage(page: Page) {
            pageList.remove(page)
        }

        @Composable
        fun Run() {
            if (pageList.isNotEmpty() && navController != null) {
            //  The controller HAS to be val for running the app
            //  so no possible switch up can occur, but due to needing
            //  it to be var previously to go from null at class initialization
            //  to actual at function call, you have to do the following
                val controller = navController

                if (controller != null) {
                    NavHost(
                        navController = controller,
                        startDestination = pageList[homePageIndex].name
                    ) {
                        for (page: Page in pageList) {
                            composable(page.name) {
                                page.content()
                            }
                        }
                    }
                }
            }
        }

        @Composable
        fun GoToButton(
            pageName: String?,
            navController: NavHostController?,
            buttonModifier: Modifier = goBackButtonModifier,
            textModifier: Modifier = goBackTextModifier,
            text: String = goBackText,
            extraFunctionality: () -> Unit = {},
            extraComposableFunctionality: @Composable () -> Unit = {},
            fontWeight: FontWeight = FontWeight.Normal,
            fontSize: Int = 14
        ) {
            if (pageName != null && navController != null) {
//              Composable functions can't be held in the onClick of a button,
//              This acts as a triggerable thing for the button to trigger
//              to run the Composable function outside the button onClick
                var buttonClicked by remember {
                    mutableStateOf(false)
                }
                Button(
                    onClick = {
                        extraFunctionality()
                        buttonClicked = true
//                      If pageName is empty then whatever page navigation
//                      is needed has already been ennacted (Ex: line 255)
                        if (pageName != "") {
                            navController.navigate(pageName)
                        }
                    },
                    modifier = buttonModifier
                ) {
                    Text(
                        text = text,
                        modifier = textModifier,
                        fontWeight = fontWeight,
                        fontSize = fontSize.sp
                    )
                }

                if (buttonClicked) {
                    extraComposableFunctionality()
                }
            }
        }

        @Composable
        fun GoBackButton(
            navController: NavHostController?,
            buttonModifier: Modifier = goBackButtonModifier,
            textModifier: Modifier = goBackTextModifier,
            text: String = goBackText,
            fontWeight: FontWeight = FontWeight.Normal,
            fontSize: Int = 14,
            extraFunctionality: () -> Unit = {},
            extraComposableFunctionality: @Composable () -> Unit = {}
        ) {
            if (navController != null) {
//              If there is no "previous page" with a go back button,
//              then it's likely a test run and giving a default for the home page
//              allows the button to still be composed and viewed for editing
                val pageName = if (navController.previousBackStackEntry == null) {
                    "Home"
                } else {
                    ""
                }

                GoToButton(
                    pageName,
                    navController,
                    buttonModifier,
                    textModifier,
                    text,
                    fontWeight = fontWeight,
                    fontSize = fontSize,
                    extraFunctionality = {
                        extraFunctionality()
                        if (pageName == "") {
                            navController.popBackStack()
                        }
                    },
                    extraComposableFunctionality = extraComposableFunctionality
                )
            }
        }

        @Composable
        fun StandardHeading(
            heading: String,
            textModifier: Modifier = standardHeadingTextModifier,
            modifier: Modifier = standardHeadingModifier,
        ) {
            Row(
                modifier = modifier
                    .fillMaxWidth(),
                Arrangement.Center,
                Alignment.CenterVertically
            ) {
                GoBackButton(
                    navController,
                    fontSize = 24
                )
                Text(
                    text = heading,
                    modifier = textModifier
                        .padding(16.dp),
                    fontSize = 32.sp
                )
            }
        }

        @Composable
//      Reliable method of displaying a text box that updates with typing
//      a returning a value to whatever this is set equal to
        fun input(
            text: String = ""
        ) : String {
            var value by remember { mutableStateOf("") }

            TextField(
                value = value,
                onValueChange = { value = it },
                label = { Text(text) }
            )

            return value
        }
    }
}