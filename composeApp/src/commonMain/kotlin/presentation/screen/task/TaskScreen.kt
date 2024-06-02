package presentation.screen.task

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import domain.TaskAction
import domain.ToDoTask

@OptIn(ExperimentalMaterial3Api::class)
data class TaskScreen(val task: ToDoTask? = null) : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = getScreenModel<TaskViewModel>()
        var currentTitle by remember { mutableStateOf(task?.title.orEmpty()) }
        var currentDescription by remember { mutableStateOf(task?.description.orEmpty()) }

        val titleFocusRequester = remember { FocusRequester() }

        LaunchedEffect(Unit) {
            titleFocusRequester.requestFocus()
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        BasicTextField(
                            value = currentTitle,
                            onValueChange = { currentTitle = it },
                            textStyle = TextStyle(
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                                fontWeight = FontWeight.Bold
                            ),
                            singleLine = true,
                            modifier = Modifier.focusRequester(titleFocusRequester),
                            decorationBox = { innerTextField ->
                                if (currentTitle.isEmpty()) {
                                    Text(
                                        text = "Enter the Title",
                                        style = TextStyle(
                                            color = Color.Gray,
                                            fontSize = MaterialTheme.typography.titleLarge.fontSize
                                        )
                                    )
                                }
                                innerTextField()
                            }
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navigator.pop() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Default.ArrowBack,
                                contentDescription = "Back Arrow"
                            )
                        }
                    }
                )
            },
            floatingActionButton = {
                if (currentTitle.isNotEmpty()) {
                    FloatingActionButton(
                        onClick = {
                            if (task != null) {
                                viewModel.setAction(
                                    action = TaskAction.Update(
                                        ToDoTask().apply {
                                            _id = task._id
                                            title = currentTitle
                                            description = currentDescription
                                        }
                                    )
                                )
                            } else {
                                viewModel.setAction(
                                    action = TaskAction.Add(
                                        ToDoTask().apply {
                                            title = currentTitle
                                            description = currentDescription
                                        }
                                    )
                                )
                            }
                            navigator.pop()
                        },
                        shape = RoundedCornerShape(size = 16.dp),
                        containerColor = MaterialTheme.colorScheme.primary
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Checkmark Icon",
                            tint = Color.White
                        )
                    }
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp)
            ) {
                BasicTextField(
                    value = currentDescription,
                    onValueChange = { description -> currentDescription = description },
                    textStyle = TextStyle(
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    modifier = Modifier.fillMaxSize(),
                    decorationBox = { innerTextField ->
                        if (currentDescription.isEmpty()) {
                            Text(
                                text = "Add some description",
                                style = TextStyle(
                                    color = Color.Gray,
                                    fontSize = MaterialTheme.typography.bodyLarge.fontSize
                                )
                            )
                        }
                        innerTextField()
                    }
                )
            }
        }
    }
}
