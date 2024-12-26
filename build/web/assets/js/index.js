const popup = Notification();

function loadHomeComponents() {
    loadCategoryList();
    loadProductList();
}

async function loadCategoryList() {
    const response = await fetch("LoadCategoryList");

    if (response.ok) {
        console.log(response);

        const list = await response.json();
        console.log(list);

//        list.forEach(listItem => {
//            
//        })

    } else {
        popup.error({
            message: "Error occured. Please refresh the page. If the error is persistant, please try again later."
        });
    }
}

async function loadProductList() {
    const response = await fetch("LoadProductList");

    if (response.ok) {

        console.log(response);

        const json = await response.json();

        console.log(json);

        let productCardHtml = document.getElementById("productCard");
        document.getElementById("productCardView").innerHTML = "";

        json.productList.forEach(product => {
            let productCardCloneHtml = productCardHtml.cloneNode(true);

            productCardCloneHtml.querySelector("#productCardLink1").href = "product.html?id=" + product.id;
            productCardCloneHtml.querySelector("#productCardPrimaryImg").src = "product-images/" + product.id + "/image1.png";
            productCardCloneHtml.querySelector("#productCardHoverImg").src = "product-images/" + product.id + "/image2.png";
            productCardCloneHtml.querySelector("#productCardCartButton").addEventListener(
                    "click",
                    e => {
                        addToCart(product.id, 1);
                    }
            );

            productCardCloneHtml.querySelector("#productCardWishlistButton").addEventListener(
                    "click",
                    e => {
                        //add to wishlist function
                    }
            );
            productCardCloneHtml.querySelector("#productCardTitle").innerHTML = product.title;
            productCardCloneHtml.querySelector("#productCardTitle").href = "product.html?id=" + product.id;
            productCardCloneHtml.querySelector("#productCardPrice").innerHTML = "Rs." + product.price;
            document.getElementById("productCardView").appendChild(productCardCloneHtml);

        });

    } else {
        popup.error({
            message: "Error occured. Please refresh the page. If the error is persistant, please try again later."
        });
    }
}