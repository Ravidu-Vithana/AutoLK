const popup = Notification();

// Payment completed. It can be a successful failure.
payhere.onCompleted = function onCompleted(orderId) {
    console.log("Payment completed. OrderID:" + orderId);
    // Note: validate the payment and show success or failure page to the customer
};

// Payment window closed
payhere.onDismissed = function onDismissed() {
    // Note: Prompt user to pay again or show an error page
    console.log("Payment dismissed");
};

// Error occurred
payhere.onError = function onError(error) {
    // Note: show an error page
    console.log("Error:" + error);
};



async function loadCheckOutData() {

    const response = await fetch(
            "LoadCheckout",
            );
    if (response.ok) {
        const json = await response.json();
        console.log(json);
        if (json.success) {
            //store response data   
            const address = json.address;
            const cityList = json.cityList;
            const cartList = json.cartList;
            //load cities
            let citySelect = document.getElementById("input-city")
            citySelect.length = 1;
            cityList.forEach(city => {
                let cityOption = document.createElement("option");
                cityOption.value = city.id;
                cityOption.innerHTML = city.name;
                citySelect.appendChild(cityOption);
            });
            //load Current Address
            let city = document.getElementById("input-city");
            let currentAddressCheckbox = document.getElementById("check-address");
            currentAddressCheckbox.addEventListener("change", e => {
                let first_name = document.getElementById("input-firstname");
                let last_name = document.getElementById("input-lastname");
                let city = document.getElementById("input-city");
                let line1 = document.getElementById("input-line-1");
                let line2 = document.getElementById("input-line-2");
                let postal_code = document.getElementById("input-postcode");
                let mobile = document.getElementById("input-mobile");
                if (currentAddressCheckbox.checked) {
                    first_name.value = address.first_name;
                    last_name.value = address.last_name;
                    city.value = address.city.id;
                    city.disabled = true;
                    city.dispatchEvent(new Event("change"));
                    line1.value = address.line1;
                    line2.value = address.line2;
                    postal_code.value = address.postal_code;
                    mobile.value = address.mobile;
                } else {
                    first_name.value = "";
                    last_name.value = "";
                    city.value = 0;
                    city.disabled = false;
                    city.dispatchEvent(new Event("change"));
                    line1.value = "";
                    line2.value = "";
                    postal_code.value = "";
                    mobile.value = "";
                }
            });

            // Load cart items
            let cartItemView = document.getElementById("cartItemView");
            let cartItem = document.getElementById("cartItem");
            let subTotal = document.getElementById("subTotal");
            let shipping = document.getElementById("shipping");
            let fullTotal = document.getElementById("total");
            cartItemView.innerHTML = ""; // Clear table body

            let sub_total = 0;
            cartList.forEach(item => {
                let cartItemClone = cartItem.cloneNode(true); // Clone the item row
                cartItemClone.querySelector("#itemTitle").innerHTML = item.product.title;
                cartItemClone.querySelector("#itemPrice").innerHTML = "Rs." + item.product.price;
                cartItemClone.querySelector("#itemQuantity").innerHTML = item.qty;
                let item_sub_total = item.product.price * item.qty;
                sub_total += item_sub_total;
                cartItemClone.querySelector("#itemSubTotal").innerHTML = "Rs." + item_sub_total;
                cartItemView.appendChild(cartItemClone);
            });

            subTotal.innerHTML = sub_total;

            citySelect.addEventListener("change", e => {

                let item_count = cartList.length;
                let shipping_amount = 0;
                //check if the city is Colombo or not
                if (citySelect.value == 5) {
                    //colombo
                    shipping_amount = item_count * 1000;
                } else {
                    //out of colombo
                    shipping_amount = item_count * 2500;
                }


                shipping.innerHTML = shipping_amount;
                //update total
                let total = sub_total + shipping_amount;
                fullTotal.innerHTML = total;
            });
            city.dispatchEvent(new Event("change"));
        } else {
            window.location = "sign-in.html";
        }
    }
}

async function checkout() {

    let isCurrentAddress = document.getElementById("check-address").checked;
    let firstName = document.getElementById("input-firstname");
    let lastName = document.getElementById("input-lastname");
    let mobile = document.getElementById("input-mobile");
    let postal_code = document.getElementById("input-postcode");
    let line1 = document.getElementById("input-line-1");
    let line2 = document.getElementById("input-line-2");
    let city = document.getElementById("input-city");
    const data = {
        isCurrentAddress: isCurrentAddress,
        firstName: firstName.value,
        lastName: lastName.value,
        city_id: city.value,
        line1: line1.value,
        line2: line2.value,
        postal_code: postal_code.value,
        mobile: mobile.value
    };
    //request data(json)
    const response = await fetch(
            "Checkout",
            {
                method: "POST",
                body: JSON.stringify(data),
                headers: {
                    "Content-Type": "application/json"
                }
            }
    );
    if (response.ok) {
        const json = await  response.json();
        const popup = Notification();
        console.log(json);
        if (json.success) {
            console.log(json.payhereJson);
            payhere.startPayment(json.payhereJson);

        } else {
            popup.error({

                message: json.message
            });
        }
    } else {
        console.log("Try again Later!");
        }

}