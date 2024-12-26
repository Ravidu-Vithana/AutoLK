const popup = Notification();

async function changeQty(qtyElementId, change) {

    let qtyElement = document.getElementById(qtyElementId);
    let pid = qtyElement.getAttribute("data-pid");

    if (change == "plus") {

        const response = await fetch("AddToCart?id=" + pid + "&qty=1" + "&operation=plus");

        if (response.ok) {

            const json = await response.json();
            if (json.success) {
                popup.success({
                    message: "Quantity added successfully!"
                });
                qtyElement.value = parseInt(qtyElement.value) + 1;
            } else {
                popup.error({
                    message: json.content
                });
            }

        } else {
            popup.error({
                message: "Error occured. Please try again later."
            });
        }

    } else if (change == "minus") {
        if (qtyElement.value > 1) {
            const response = await fetch("AddToCart?id=" + pid + "&qty=1" + "&operation=minus");

            if (response.ok) {

                const json = await response.json();
                if (json.success) {
                    popup.success({
                        message: "Quantity removed successfully!"
                    });
                    qtyElement.value = parseInt(qtyElement.value) - 1;
                } else {
                    popup.error({
                        message: json.content
                    });
                }

            } else {
                popup.error({
                    message: "Error occured. Please try again later."
                });
            }
        }
    }

//    document.getElementById("finaltotal").innerHTML = new Intl.NumberFormat(
//                    "en-US", {
//                        minimumFractionDigits: 2
//                    }).format(sub_total);

}

var itemNo = 1;

async  function loadCartItems() {

    const emptyCardStatement = `<tr><td colspan="5" class="text-center py-4"><h3 class="fw-bold">Your Cart Is Empty.</h3></td></tr>`;

    const response = await fetch(
            "LoadCartItems",
            );

    if (response.ok) {

        const json = await response.json();
        console.log(json);

        let cartItemView = document.getElementById("cartItemView");

        if (json.length == 0) {
            cartItemView.innerHTML = emptyCardStatement;
            document.getElementById("cartCheckout").classList.add("disabled");
            document.getElementById("cartCheckout").setAttribute("disabled", true);
            document.getElementById("cartItemClearButton").setAttribute("disabled", true);
        } else {
            let cartItem = document.getElementById("cartItem");
            cartItemView.innerHTML = "";

            let total = 0;

            json.forEach(item => {

                let itemSubTotal = item.product.price * item.qty;
                total += itemSubTotal;

                let cartItemClone = cartItem.cloneNode(true);
                cartItemClone.querySelector("#cartItemImg").src = "product-images/" + item.product.id + "/image1.png";
                cartItemClone.querySelector("#cartItemTitle").href = "product.html?id=" + item.product.id;
                cartItemClone.querySelector("#cartItemTitle").innerHTML = item.product.title;
                cartItemClone.querySelector("#cartItemCategory").innerHTML = item.product.subCategory.category.name;
                cartItemClone.querySelector("#cartItemSubCategory").innerHTML = item.product.subCategory.name;

                cartItemClone.querySelector("#cartItemPrice").innerHTML = "Rs." + item.product.price;
                cartItemClone.querySelector(".cart__qty-input.qty").setAttribute("data-pid", item.product.id);
                cartItemClone.querySelector(".cart__qty-input.qty").value = item.qty;
                cartItemClone.querySelector(".cart__qty-input.qty").id = "qty" + itemNo;

                let currentItemNo = itemNo;

                cartItemClone.querySelector(".qtyBtn.minus").addEventListener(
                        "click",
                        e => {
                            changeQty("qty" + currentItemNo, "minus")
                        }
                );

                cartItemClone.querySelector(".qtyBtn.plus").addEventListener(
                        "click",
                        e => {
                            changeQty("qty" + currentItemNo, "plus")
                        }
                );

                cartItemClone.querySelector("#cartItemTotalPrice").innerHTML = "Rs." + (item.qty * item.product.price);
                cartItemClone.querySelector("#cartItemRemoveButton").addEventListener(
                        "click",
                        e => {
                            removeCartItem(item.product.id, "cartItemRemoveButton" + currentItemNo);
                        }
                );
                cartItemClone.querySelector("#cartItemRemoveButton").setAttribute("data-pid", item.product.id);
                cartItemClone.querySelector("#cartItemRemoveButton").id = "cartItemRemoveButton" + currentItemNo;
                cartItemView.appendChild(cartItemClone);

                itemNo++;

            });
            document.getElementById("cartSubTotal").innerHTML = total;
            document.getElementById("cartShippingFee").innerHTML = "-<i><small>Proceed to checkout</small></i>-";
            document.getElementById("cartGrandTotal").innerHTML = "-<i><small>Proceed to checkout</small></i>-";

        }
    } else {

        popup.error({
            message: "Error occured. Please try again later."
        });
    }

}

async function removeItem(pid, buttonId) {
    const response = await fetch(
            "RemoveFromCart?id=" + pid,
            );

    if (response.ok) {

        const json = await response.json();
        console.log(json);

        if (json.success) {
            popup.success({
                message: json.content
            });
            document.getElementById(buttonId).parentNode.parentNode.remove();
            itemNo--;
            if (itemNo == 1) {
                loadCartItems();
            }
            loadCartCount();
        } else {
            popup.error({
                message: json.content
            });
        }

    } else {

        popup.error({
            message: "Error occured. Please try again later."
        });
    }
}

async function removeCartItem(pid, buttonId) {

    popup.dialog({
        title: 'Errm',
        message: 'Do you want to remove this product?',
        callback: (result) => {
            if (result == "ok") {
                removeItem(pid, buttonId);
            }
        }
    });

}

function clearAllCart() {
    
    popup.dialog({
        title: 'Errm',
        message: 'Do you want to remove all products?',
        callback: (result) => {
            if (result == "ok") {
                console.log(itemNo - 1);
                const itemsCount = itemNo - 1;

                for (let x = 1; x <= itemsCount; x++) {
                    const pid = document.getElementById("cartItemRemoveButton" + x).getAttribute("data-pid");
                    removeItem(pid, "cartItemRemoveButton" + x);
                }
            }
        }
    });

}