var totalPrice = 0;

var cartItems = {
    ${cartItemsContent}
};

var cartPrices = {
    ${cartPricesContent}
};


function updateItem(id) {
    document.getElementById('cart' + id + 'Number').innerHTML = cartItems[id];
    document.getElementById(id + 'Price').innerHTML = cartItems[id] * cartPrices[id];
    document.getElementById('totalPrice').innerHTML = totalPrice;
    var cartItemDisplay = document.getElementById('cart' + id);
    if (cartItems[id] == 0) {
        cartItemDisplay.style.display="none";
    } else {
        cartItemDisplay.style.display="flex"
    }
}

function addItem(id) {
    cartItems[id] ++;
    totalPrice += cartPrices[id];
    updateItem(id);
}

function removeItem(id) {
    if(cartItems[id] > 0) {
        cartItems[id]--;
        totalPrice -= cartPrices[id];
        updateItem(id);
    }
}

function removeAll(id){
    totalPrice -= cartPrices[id] * cartItems[id];
    cartItems[id] = 0;
    updateItem(id);
}


function addItemsToCartForm(){
    var cartContents = document.getElementsByClassName("cartInput");
    var form = document.getElementById("sendOrderForm");

    while(cartContents.length > 0){
        var cartItem = cartContents[0];
        console.log("DELETE");
        console.log(cartItem);
        cartItem.parentNode.removeChild(cartItem);
    }

    for(const [artNr, count] of Object.entries(cartItems)){
        if (count != 0){
            var itemInput = document.createElement("input");
            itemInput.setAttribute("name", "itemNr[]");
            itemInput.setAttribute("class", "cartInput");
            itemInput.setAttribute("type", "hidden");
            itemInput.setAttribute("value", artNr);
            form.appendChild(itemInput);

            itemInput = document.createElement("input");
            itemInput.setAttribute("name", "itemCount[]");
            itemInput.setAttribute("class", "cartInput");
            itemInput.setAttribute("type", "hidden");
            itemInput.setAttribute("value", count);
            form.appendChild(itemInput);
        }
    }
}
