package id.usereal.shoppinglistapp

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ShoppingItem(
    val id: Int,
    var name: String,
    var price: Int,
    var quantity: Int,
    var isEditing: Boolean = false
)

@Composable
fun ShoppingList() {
    var shoppingList by remember { mutableStateOf(listOf<ShoppingItem>()) }
    var showDialog by remember { mutableStateOf(false) }
    var isEditing by remember { mutableStateOf(false) }
    var currentItem by remember { mutableStateOf<ShoppingItem?>(null) }
    var itemName by remember { mutableStateOf("") }
    var itemPrice by remember { mutableStateOf("") }
    var itemQuantity by remember { mutableStateOf("") }
    var errorMessageName by remember { mutableStateOf("") }
    var errorMessagePrice by remember { mutableStateOf("") }
    var errorMessageQuantity by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(16.dp)
        ) {
            if (shoppingList.isNotEmpty()) {
                items(shoppingList) { item ->
                    ShoppingListItem(item = item, onEditClick = {
                        currentItem = item
                        itemName = item.name
                        itemPrice = item.price.toString()
                        itemQuantity = item.quantity.toString()
                        isEditing = true
                        showDialog = true
                    }, onDeleteClick = {
                        shoppingList = shoppingList.filter { it.id != item.id }
                    })
                }
            } else {
                item {
                    Box(
                        modifier = Modifier.fillParentMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Tidak ada daftar belanjaan",
                            style = TextStyle(fontWeight = FontWeight.Bold, color = Color.Gray),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        Button(
            onClick = {
                showDialog = true
                isEditing = false
                itemName = ""
                itemPrice = ""
                itemQuantity = ""
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(text = "Tambah Barang")
        }
    }

    if (showDialog) {
        AlertDialog(
            title = if (isEditing) "Edit barang belanjaan" else "Tambah barang belanjaan",
            itemName = itemName,
            itemPrice = itemPrice,
            itemQuantity = itemQuantity,
            errorMessageName = errorMessageName,
            errorMessagePrice = errorMessagePrice,
            errorMessageQuantity = errorMessageQuantity,
            onNameChange = { newValue ->
                itemName = newValue
                errorMessageName = ""
            },
            onPriceChange = { newValue ->
                itemPrice = newValue.filter { it.isDigit() }
                errorMessagePrice = ""
            },
            onQuantityChange = { newValue ->
                itemQuantity = newValue.filter { it.isDigit() }
                errorMessageQuantity = ""
            },
            onConfirm = {
                val itemPriceInt = itemPrice.toIntOrNull() ?: 0
                val itemQuantityInt = itemQuantity.toIntOrNull() ?: 0

                if (itemName.isBlank() && itemPriceInt == 0 && itemQuantityInt == 0) {
                    errorMessageName = "Nama item tidak boleh kosong!"
                    errorMessagePrice = "Harga tidak boleh kosong!"
                    errorMessageQuantity = "Jumlah tidak boleh kosong!"
                } else if (itemName.isBlank()) {
                    errorMessageName = "Nama item tidak boleh kosong!"
                } else if (itemPriceInt == 0) {
                    errorMessagePrice = "Harga item tidak boleh kosong atau nol!"
                } else if (itemQuantityInt == 0) {
                    errorMessageQuantity = "Jumlah item tidak boleh kosong atau nol!"
                } else {
                    if (isEditing && currentItem != null) {
                        // Edit item yang ada
                        shoppingList = shoppingList.map {
                            if (it.id == currentItem!!.id) {
                                it.copy(name = itemName, price = itemPriceInt, quantity = itemQuantityInt)
                            } else it
                        }
                    } else {
                        // Tambahkan item baru
                        val newItem = ShoppingItem(
                            id = shoppingList.size + 1,
                            name = itemName,
                            price = itemPriceInt,
                            quantity = itemQuantityInt
                        )
                        shoppingList = shoppingList + newItem
                    }
                    showDialog = false
                    itemName = ""
                    itemPrice = ""
                    itemQuantity = ""
                }
            },
            onDismiss = {
                showDialog = false
                itemName = ""
                itemPrice = ""
                itemQuantity = ""
            }
        )
    }
}


@Composable
fun ShoppingListItem(
    item: ShoppingItem,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .border(
                border = BorderStroke(2.dp, color = Color(0XFF018786)),
                shape = RoundedCornerShape(20)
            ),
        verticalAlignment = Alignment.CenterVertically // Center items vertically
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .weight(1f)
                .padding(8.dp) // Internal padding for Column content
        ) {
            Text(
                text = item.name,
                style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
            )
            Text(
                text = "Jumlah: ${item.quantity}",
                style = TextStyle(fontSize = 12.sp, fontWeight = FontWeight.Normal)
            )
        }
        Text(
            text = "Rp${item.price}",
            modifier = Modifier
                .padding(8.dp) // Internal padding for Text
        )
        IconButton(
            onClick = onEditClick,
            modifier = Modifier.padding(8.dp) // Internal padding for IconButton
        ) {
            Icon(Icons.Default.Edit, contentDescription = "Edit")
        }

        IconButton(
            onClick = onDeleteClick,
            modifier = Modifier.padding(8.dp) // Internal padding for IconButton
        ) {
            Icon(Icons.Default.Delete, contentDescription = "Delete")
        }
    }
}

@Composable
fun AlertDialog(
    title: String,
    itemName: String,
    itemPrice: String,
    itemQuantity: String,
    errorMessageName: String,
    errorMessagePrice: String,
    errorMessageQuantity: String,
    onNameChange: (String) -> Unit,
    onPriceChange: (String) -> Unit,
    onQuantityChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        title = {
            Text(
                title,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = itemName,
                    onValueChange = onNameChange,
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    label = { Text(text = "Nama barang") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    isError = errorMessageName.isNotEmpty()
                )
                if (errorMessageName.isNotEmpty()) {
                    Text(
                        text = errorMessageName,
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp)
                    )
                }

                OutlinedTextField(
                    value = itemPrice,
                    onValueChange = onPriceChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    label = { Text(text = "Harga barang") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = errorMessagePrice.isNotEmpty()
                )
                if (errorMessagePrice.isNotEmpty()) {
                    Text(
                        text = errorMessagePrice,
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp)
                    )
                }

                OutlinedTextField(
                    value = itemQuantity,
                    onValueChange = onQuantityChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    label = { Text(text = "Jumlah barang") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = errorMessageQuantity.isNotEmpty()
                )
                if (errorMessageQuantity.isNotEmpty()) {
                    Text(
                        text = errorMessageQuantity,
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Button(onClick = onConfirm) {
                        Text(text = "Tambah")
                    }
                    Button(onClick = onDismiss) {
                        Text(text = "Kembali")
                    }
                }
            }
        }
    )
}
