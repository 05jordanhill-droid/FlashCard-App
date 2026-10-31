package com.example.flashcardapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import com.example.flashcardapp.ui.theme.FlashCardAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FlashCardAppTheme {
                Scaffold( modifier = Modifier.fillMaxSize() ) { innerPadding ->
                    FlashcardApp(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomePreview() {
    FlashCardAppTheme {
        FlashcardApp()
    }
}

@Composable
fun FlashcardApp(modifier: Modifier = Modifier) {
    val app = KotlinSupport.App(
        "Flash Cards App"
    )

    app.InitializeNavigation()

//  Mutable allows me to make edits to the list later on and State/remember
//  allows the page to automatically update if the list is edited
    val flashCardSets: MutableList<FlashCardSet> =
        remember { mutableStateListOf() }

//  Chosen set for viewing and more
    var focusSet: FlashCardSet? = null

    app.goBackButtonModifier = Modifier
    app.goBackTextModifier = Modifier
    app.standardHeadingModifier = Modifier
    app.standardHeadingTextModifier = Modifier

//  Pre-populated set for fast testing
    flashCardSets.add(
        FlashCardSet(
            "Test1",
            remember { mutableStateListOf(
                FlashCard(
                    FlashCardSide(content = "1 + 1"),
                    FlashCardSide(content = "2")
                ),
                FlashCard(
                    FlashCardSide(content = "2 * 2"),
                    FlashCardSide(content = "4")
                ),
                FlashCard(
                    FlashCardSide(content = "10 / 2"),
                    FlashCardSide(content = "5")
                )
            )}
        )
    )

    @Composable
    fun HomePage() {
        Column(
            modifier = app.standardPageModifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            app.GoToButton(
                "ViewSets",
                app.navController,
                Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .weight(1f),
                Modifier,
                "View Sets",
                fontSize = 48
            )
        }
    }
    @Composable
    fun ViewSetsPage() {
//      Allows for scrolling in the case of large
//      data due to dynamically loaded material
        LazyColumn(
            modifier = app.standardPageModifier
        ) {
            item{ app.StandardHeading("View Sets") }

            items(flashCardSets) { set ->
                Row {
                    app.GoToButton(
                        "ViewSet",
                        app.navController,
                        Modifier
                            .weight(1f)
                            .padding(end = 16.dp),
                        Modifier,
                        set.name,
                        extraFunctionality = { focusSet = set }
                    )
                    KotlinSupport.DeleteButton(
                        set,
                        flashCardSets
                    )
                }
            }

            item{
                app.GoToButton(
                    "AddSet",
                    app.navController,
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    text = "Add Set"
                )
            }
        }
    }
    @Composable
    fun ViewSetPage() {
//      Past this point, assurance is needed that
//      focusSet has been assigned beyond null
        if (focusSet != null) {
            LazyColumn(
                modifier = app.standardPageModifier
            ) {
                item{ app.StandardHeading(focusSet.name) }

                item { app.GoToButton(
                    "TestSet",
                    app.navController,
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    text = "Test Set",
//                  This preps focusSet by clearing it of any cards in its
//                  tested (already tested) list and repopulates its testing (to be tested) list
                    extraFunctionality = { focusSet.start() }
                ) }

                items(focusSet.flashCards) { card ->
                    focusSet.EditingOptions(card)
                }

                item{
                    app.GoToButton(
                        "AddCard",
                        app.navController,
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        text = "Add Card"
                    )
                }
            }
        }
    }

    @Composable
    fun MakingCardOptions() {
        if (focusSet != null) {
//          Rather than making and using a whole FlashCard right now
//          making and tracking the aspects of the card allows for
//          easier managing in remember {} without making the FlashCard or
//          FlashCardSide class inherently using remember {} in some manner
            var displayedContent by remember { mutableStateOf("") }
            var hiddenContent by remember { mutableStateOf("") }

            Column(
                modifier = app.standardPageModifier,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                app.StandardHeading("Make a Card")

//          For Displayed card side
//          \/   \/   \/   \/   \/
                Row {
                    FlashCardSide(content = displayedContent).Display()

                    Column {
                        val setDisplayed = app.input("Enter Displayed Text")

                        Row {
                            Button(
                                onClick = {
                                    displayedContent = setDisplayed
                                }
                            ) {
                                Text(
                                    text = "Add Text"
                                )
                            }
                        }
                    }
                }
//          /\   /\   /\   /\   /\

//          For Hidden
//          \/   \/   \/   \/   \/
                Row(
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    FlashCardSide(content = hiddenContent).Display()
                    Column {
                        val setHidden = app.input("Enter Hidden Text")

                        Row {
                            Button(
                                onClick = { hiddenContent = setHidden }
                            ) {
                                Text(
                                    text = "Add Text"
                                )
                            }
                        }
                    }
                }
//          /\   /\   /\   /\   /\

                app.GoBackButton(
                    app.navController,
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 32.dp),
                    Modifier,
                    "Add Card",
                    fontSize = 48,
                    extraFunctionality = {
                        focusSet.addCard(
                            FlashCard(
                                FlashCardSide(content = displayedContent),
                                FlashCardSide(content = hiddenContent)
                            )
                        )
                    }
                )
            }
        }
    }

    @Composable
    fun MakingSetOptions() {
        Column(
            modifier = app.standardPageModifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            app.StandardHeading("Make a Set")

            val setName = app.input("Enter Set Name")

            app.GoBackButton(
                navController = app.navController,
                buttonModifier =  Modifier
                    .fillMaxWidth()
                    .padding(top = 32.dp),
                textModifier =  Modifier,
                text = "Add Set",
                fontSize = 48,
                extraComposableFunctionality = {
                    flashCardSets.add(
                        FlashCardSet(
                            setName,
                            remember { mutableStateListOf() }
                        )
                    )
                }
            )
        }
    }

    @Composable
    fun TestSetPage() {
        if (focusSet != null) {
//          Allows for modulo wrapping via index as we alternate between the two
            val messages = listOf(
                "Testing Side (Click Card)",
                "Answer Side (Click Card)"
            )

            var index by remember { mutableIntStateOf(0) }
            var card by remember { mutableStateOf(focusSet.pullRandom()) }

            val cardDisplays: MutableList<@Composable () -> Unit> = mutableListOf(
                { card.displayed.Display(Modifier.fillMaxWidth()) },
                { card.hidden.Display(Modifier.fillMaxWidth()) }
            )

            Column(
                modifier = app.standardPageModifier,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                app.StandardHeading(focusSet.name)

                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = messages[index % 2]
                    )

                    TextButton(
                        onClick = {
                            if (index % 2 == 1) {
                                if (focusSet.testingCards.isEmpty()) {
                                    app.navController?.popBackStack()
                                } else {
                                    card = focusSet.pullRandom(card)
                                }
                            }
                            index++
                        },
                        shape = RectangleShape
                    ) {
                        cardDisplays[index % 2]()
                    }
                }
            }
        }
    }

    app.addPage(listOf(
        KotlinSupport.Page("Home", { HomePage() }),
        KotlinSupport.Page("ViewSets", { ViewSetsPage() }),
        KotlinSupport.Page("ViewSet", { ViewSetPage() }),
        KotlinSupport.Page("AddCard", { MakingCardOptions() }),
        KotlinSupport.Page("AddSet", { MakingSetOptions() }),
        KotlinSupport.Page("TestSet", { TestSetPage() })
    ))

    app.Run()
}

class FlashCardSet (
    val name: String,
    val flashCards: MutableList<FlashCard>
) {
    val testingCards: MutableList<FlashCard> = mutableListOf()
    val testedCards: MutableList<FlashCard> = mutableListOf()

    fun pullRandom(card: FlashCard?=null) : FlashCard {
//      First call of pullRandom has no card having already been read
        if (card != null) {
            testedCards.add(card)
        }

        val randomCard = testingCards.random()

        testingCards.remove(randomCard)

        return randomCard
    }

    fun start() {
        testingCards.clear()
        testedCards.clear()

        flashCards.forEach { card -> testingCards.add(card) }
    }

    @Composable
    fun EditingOptions(
        card: FlashCard
    ) {
        Row(
            modifier = Modifier.padding(top = 32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            card.displayed.Display(
                Modifier
                    .weight(1f)
            )
            card.hidden.Display(
                Modifier
                    .weight(1f)
            )
            KotlinSupport.DeleteButton(
                card,
                flashCards,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }

    fun addCard(card: FlashCard) {
        flashCards.add(card)
    }
}

// A card has no function other than holding two sides together
data class FlashCard (
    val displayed: FlashCardSide,
    val hidden: FlashCardSide
)

class FlashCardSide (
    var content: String? = null,
) {
    @Composable
    fun Display(modifier: Modifier = Modifier) {
        Box (
            Modifier
                .then(modifier)
                .width(100.dp)
                .aspectRatio(1f)
                .border(2.dp, Color.Black),
            contentAlignment = Alignment.Center
        ) {
            content?.let {
                Text(
                    text = it,
                    modifier = Modifier
                )
            }
        }
    }
}